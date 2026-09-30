package io.zaryx.model.items;

import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.bank.BankItem;
import io.zaryx.model.items.bank.BankTab;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

/** Game-thread reward delivery. A whole bundle fits in inventory, or a whole bundle fits in bank. */
public final class RewardDelivery {
    private RewardDelivery() { }

    public enum Destination { INVENTORY, BANK, NO_SPACE }

    public static boolean give(Player player, Collection<? extends GameItem> rewards) {
        return give(player, rewards.toArray(new GameItem[0]));
    }

    public static boolean give(Player player, GameItem... rewards) {
        Destination destination = deliver(player, Arrays.asList(rewards));
        notify(player, destination);
        return destination != Destination.NO_SPACE;
    }

    public static void notify(Player player, Destination destination) {
        if (destination == Destination.NO_SPACE) {
            player.sendMessage(player.getMode().isUltimateIronman()
                    ? "Make inventory space for your rewards, then claim again. Ultimate ironmen cannot bank rewards."
                    : "There is not enough room for your rewards in your inventory or bank. Make space and claim again.");
        } else {
            player.sendMessage("Your rewards were delivered to your "
                    + (destination == Destination.INVENTORY ? "inventory." : "bank."));
        }
    }

    public static Destination deliver(Player player, Collection<? extends GameItem> rewards) {
        List<GameItem> items = new ArrayList<>();
        for (GameItem item : rewards) {
            if (item == null || item.getId() < 0 || item.getAmount() <= 0)
                throw new IllegalArgumentException("Invalid reward item");
            items.add(item.copy());
        }
        if (fitsInventory(player.playerItems, player.playerItemsN, items,
                id -> ItemDef.forId(id).isStackable())) {
            for (GameItem item : items) {
                if (!player.getItems().addItem(item.getId(), item.getAmount(), false))
                    throw new IllegalStateException("Reward inventory changed during delivery");
            }
            return Destination.INVENTORY;
        }
        if (player.getMode().isUltimateIronman()) return Destination.NO_SPACE;

        Map<Integer, Long> existing = new HashMap<>();
        Map<Integer, BankTab> tabs = new HashMap<>();
        for (BankTab tab : player.getBank().getBankTab()) {
            if (tab == null) continue;
            for (BankItem item : tab.getItems()) {
                existing.merge(item.getId() - 1, (long) item.getAmount(), Long::sum);
                tabs.putIfAbsent(item.getId() - 1, tab);
            }
        }
        IntUnaryOperator bankId = id -> ItemDef.forId(id).isNoted() ? ItemDef.forId(id).getNoteId() : id;
        Map<Integer, Long> additions = bankAdditions(items, bankId);
        int free = Math.max(0, player.getBank().getBankCapacity() - player.getBank().getItemCount());
        if (!fitsBank(existing, free, additions)) return Destination.NO_SPACE;
        for (Map.Entry<Integer, Long> entry : additions.entrySet()) {
            BankTab tab = tabs.getOrDefault(entry.getKey(), player.getBank().getBankTab(0));
            tab.add(new BankItem(entry.getKey() + 1, entry.getValue().intValue()));
        }
        player.getItems().resetTempItems();
        if (player.isBanking) player.getItems().queueBankContainerUpdate();
        return Destination.BANK;
    }

    static boolean fitsInventory(int[] ids, int[] amounts, Collection<? extends GameItem> rewards,
                                 IntPredicate stackable) {
        long free = 0;
        Map<Integer, Long> stacks = new HashMap<>();
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] <= 0) free++;
            else if (stackable.test(ids[i] - 1)) stacks.merge(ids[i] - 1, (long) amounts[i], Long::sum);
        }
        for (GameItem reward : rewards) {
            if (stackable.test(reward.getId())) {
                if (!stacks.containsKey(reward.getId())) free--;
                long total = stacks.getOrDefault(reward.getId(), 0L) + reward.getAmount();
                if (total > Integer.MAX_VALUE) return false;
                stacks.put(reward.getId(), total);
            } else free -= reward.getAmount();
            if (free < 0) return false;
        }
        return true;
    }

    static Map<Integer, Long> bankAdditions(Collection<? extends GameItem> rewards, IntUnaryOperator bankId) {
        Map<Integer, Long> additions = new LinkedHashMap<>();
        for (GameItem item : rewards) additions.merge(bankId.applyAsInt(item.getId()), (long) item.getAmount(), Long::sum);
        return additions;
    }

    static boolean fitsBank(Map<Integer, Long> existing, int free, Map<Integer, Long> additions) {
        for (Map.Entry<Integer, Long> entry : additions.entrySet()) {
            if (!existing.containsKey(entry.getKey())) free--;
            if (free < 0 || existing.getOrDefault(entry.getKey(), 0L) + entry.getValue() > Integer.MAX_VALUE)
                return false;
        }
        return true;
    }

    /** Used when a claim's synchronous save must succeed before acknowledging delivery. */
    public static Runnable rollback(Player player) {
        int[] ids = player.playerItems.clone(), amounts = player.playerItemsN.clone();
        Map<BankTab, List<BankItem>> bank = new LinkedHashMap<>();
        for (BankTab tab : player.getBank().getBankTab()) {
            if (tab == null) continue;
            List<BankItem> copy = new ArrayList<>();
            for (BankItem item : tab.getItems()) copy.add(new BankItem(item.getId(), item.getAmount()));
            bank.put(tab, copy);
        }
        return () -> {
            System.arraycopy(ids, 0, player.playerItems, 0, ids.length);
            System.arraycopy(amounts, 0, player.playerItemsN, 0, amounts.length);
            bank.forEach((tab, copy) -> { tab.getItems().clear(); tab.getItems().addAll(copy); });
            player.getItems().resetItems(3214);
            if (player.isBanking) player.getItems().queueBankContainerUpdate();
        };
    }
}
