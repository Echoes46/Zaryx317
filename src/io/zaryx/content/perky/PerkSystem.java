package io.zaryx.content.perky;

import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;

import java.util.*;

public class PerkSystem {

    private final Player player;
    public List<GameItem> gameItems = new ArrayList<>();
    public List<Perks> perks = new ArrayList<>();

    public PerkSystem(Player player) {
        this.player = player;
    }

    public void attunePerk(int itemid) {
        SortedMap<String, Perks> map = new TreeMap<String, Perks>();

        for (Perks value : Perks.values()) {
            map.put(value.name(), value);
        }

        if (!canAttune(itemid)) {
            return;
        }

        if (!player.getItems().hasItemOnOrInventory(itemid)) {
            player.sendMessage("How did you manage to get here?");
            return;
        }

        for (Perks value : Perks.values()) {
            if (value.itemID == itemid && player.getItems().hasItemOnOrInventory(itemid)) {
                player.getPerkSytem().gameItems.add(new GameItem(itemid, 1));
                player.getItems().deleteItem2(itemid, 1);
                player.sendMessage("You attune " + ItemDef.forId(itemid).getName() + ".");
                perks.add(value);
                break;
            }
        }

        updateInterface(false);
        refreshBonuses();
    }
    private void refreshBonuses() {
        /*
         * Refresh combat/equipment bonuses after perk changes.
         * This makes perk effects apply instantly without relogging.
         */
        player.getItems().calculateBonuses();
        player.getPA().requestUpdates();
    }
    public void removePerk(int itemid) {
        boolean removed = player.getPerkSytem().gameItems.removeIf(gameItem -> gameItem.getId() == itemid);

        if (!removed) {
            player.sendMessage("@red@You do not have this perk attuned.");
            updateInterface(true);
            refreshBonuses();
            return;
        }

        player.sendMessage("You unattune " + ItemDef.forId(itemid).getName() + ".");
        player.getItems().addItemUnderAnyCircumstance(itemid, 1);

        perks.removeIf(perk -> perk.itemID == itemid);

        updateInterface(true);
        refreshBonuses();
    }

    public List<GameItem> gameItems() {
        return gameItems;
    }

    public boolean canAttune(int itemid) {
        if (player.getPerkSytem().gameItems.size() >= 9) {
            player.sendMessage("You cannot attune anymore perk's you already have 9 attuned.");
            return false;
        }

        if (player.wildLevel > 0) {
            player.sendMessage("You cannot attune perk's while in the wilderness.");
            return false;
        }

        boolean alreadyAttuned = player.getPerkSytem().gameItems.stream()
                .anyMatch(item -> item.getId() == itemid);

        if (alreadyAttuned) {
            player.sendMessage("You cannot attune anymore of this perk, as you already have one attuned.");
            return false;
        }

        for (GameItem gameItem : player.getPerkSytem().gameItems) {
            int activeId = gameItem.getId();

            if (conflicts(activeId, itemid)) {
                player.sendMessage("@red@You can only equip one of these perks at a time!");
                return false;
            }
        }

        return true;
    }

    private boolean conflicts(int activeId, int newId) {
        return isPair(activeId, newId, 33108, 33112)
                || isPair(activeId, newId, 33122, 33226)
                || isPair(activeId, newId, 33106, 33222)
                || isPair(activeId, newId, 33105, 33220)
                || isPair(activeId, newId, 33107, 33221);
    }

    private boolean isPair(int activeId, int newId, int first, int second) {
        return (activeId == first && newId == second)
                || (activeId == second && newId == first);
    }

    public void updateInterface(boolean remove) {
        if (remove) {
            for (int i = 0; i < 9; i++) {
                player.getPA().itemOnInterface(-1,0, 65022, i);
            }
        }

        if (player.getPerkSytem().gameItems.isEmpty()) {
            return;
        }

        for (int i = 0; i < player.getPerkSytem().gameItems.size(); i++) {
            player.getPA().itemOnInterface(player.getPerkSytem().gameItems.get(i).getId(),player.getPerkSytem().gameItems.get(i).getAmount(), 65022, i);
        }
    }

    public boolean obtainPerk(int itemID) {
        ArrayList<Perks> perks = new ArrayList<>();

        if (!perks.isEmpty()) {
            perks.clear();
        }

        if (itemID == 26547) { // Combat
            if (System.currentTimeMillis() - player.clickDelay <= 2200) {
                return true;
            }
            player.clickDelay = System.currentTimeMillis();
            for (Perks value : Perks.values()) {
                if (value.perkType == PerkType.COMBAT) {
                    perks.add(value);
                }
            }
            player.getItems().deleteItem2(itemID, 1);
            player.getItems().addItemUnderAnyCircumstance(perks.get(Misc.random((perks.size()-1))).itemID, 1);
            return true;
        } else if (itemID == 26546) { // Skilling
            if (System.currentTimeMillis() - player.clickDelay <= 2200) {
                return true;
            }
            player.clickDelay = System.currentTimeMillis();
            for (Perks value : Perks.values()) {
                if (value.perkType == PerkType.SKILLING) {
                    perks.add(value);
                }
            }
            player.getItems().deleteItem2(itemID, 1);
            player.getItems().addItemUnderAnyCircumstance(perks.get(Misc.random((perks.size()-1))).itemID, 1);
            return true;
        } else if (itemID == 26548) { // Misc
            if (System.currentTimeMillis() - player.clickDelay <= 2200) {
                return true;
            }
            player.clickDelay = System.currentTimeMillis();
            for (Perks value : Perks.values()) {
                if (value.perkType == PerkType.MISC) {
                    perks.add(value);
                }
            }
            player.getItems().deleteItem2(itemID, 1);
            player.getItems().addItemUnderAnyCircumstance(perks.get(Misc.random((perks.size()-1))).itemID, 1);
            return true;
        }
        return false;
    }
}
