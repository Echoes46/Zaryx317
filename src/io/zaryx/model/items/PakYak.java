package io.zaryx.model.items;

import io.zaryx.Server;
import io.zaryx.content.lootbag.LootingBagItem;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.zaryx.model.multiplayersession.MultiplayerSessionStage;
import io.zaryx.model.multiplayersession.MultiplayerSessionType;
import io.zaryx.model.multiplayersession.duel.DuelSession;

import java.util.Objects;

public class PakYak {

    public Player player;
    private int enterAmountItem = -1;
    private int enterAmountInterface = -1;
    public void setEnterAmountVariables(int itemId, int interfaceId) {
        this.enterAmountItem = itemId;
        this.enterAmountInterface = interfaceId;
    }
    public void finishEnterAmount_deposit(int id, int amount) {
        deposit(id, amount,false);
    }
    public void finishEnterAmount_withdraw(int id, int amount) {
        withdraw(id, amount);
    }

    public PakYak(Player player) {
        this.player = player;
    }

    public boolean configurationPermitted() {
        if (player.getBankPin().requiresUnlock()) {
            player.getBankPin().open(2);
            return false;
        }

        DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
                MultiplayerSessionType.DUEL);
        if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
                && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
            player.sendMessage("Your actions have declined the duel.");
            duelSession.getOther(player).sendMessage("The challenger has declined the duel.");
            duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
            return false;
        }
        if (Server.getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.TRADE)) {
            player.sendMessage("You must decline the trade to start walking.");
            return false;
        }
        if (player.underAttackByPlayer > 0 || player.underAttackByNpc > 0 && player.underAttackByNpc != player.lastNpcAttacked && !player.getPosition().inMulti()) {
            // player.sendMessage("Can't do this in combat.");
            return false;
        }
        if (player.isStuck) {
            player.isStuck = false;
            player.sendMessage("@red@You've disrupted stuck command, you will no longer be moved home.");
            return false;
        }
        return true;
    }

    public boolean handleButton(int buttonId) {
        if (buttonId == 45347) {
            closedepositbox();
            return true;
        }
//        if (buttonId == 42109) {
//            depositall();
//            return true;
//        }
//        if (buttonId == 42110) {
//            depositEquipment();
//            return true;
//        }
        return false;
    }

    /**
     * Opens the check looting bag interface
     */
    public void openPakYak() {
        //    items.clear();
        if (!configurationPermitted()) {
            player.sendMessage("You cannot do this right now.");
            return;
        }
        onClose();
        sendItems();
        sendInventoryItems();
        player.getPA().sendFrame248(35342, 5063);

        player.inpakyak = true;
    }



    public boolean containsItem(int id) {
        for (LootingBagItem item : player.pakyakitems) {
            if (item.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public int findIndexInLootBag(int id) {
        for (LootingBagItem item : player.pakyakitems) {
            if (item.getId() == id) {
                return player.pakyakitems.indexOf(item);
            }
        }
        return -1;
    }

    /**
     * Handles withdrawal from the lootingbag
     * @param id		The id of the item being withdrawn
     * @param amount	The amount of the item being withdrawn
     */
    public boolean withdraw(int id, int amount) {
        int index = findIndexInLootBag(id);
        int amountToAdd = 0;
//			if (!player.inClanWarsSafe() && id != 13307) {
//				player.sendMessage("You are only able to withdraw blood money from here.");
//				return false;
//			}
        if (player.pakyakitems.size() <= 0) {
            return false;
        }
        if (index == -1) {
            return false;
        }
        LootingBagItem item = player.pakyakitems.get(index);
        if (item == null) {
            return false;
        }
        if (item == null || item.getId() <= 0 || item.getAmount() <= 0 || player.getItems().freeSlots() <= 0) {
            return false;
        }
        if (!configurationPermitted()) {
            player.sendMessage("You cannot do this right now.");
            return false;
        }
//        if (player.getItems().getItemAmount(id)+ amount >= Integer.MAX_VALUE || player.getItems().getItemAmount(id) + amount <= 0) {
//            return false;
//        }
        if ((player.pakyakitems.get(player.pakyakitems.indexOf(item)).getAmount()) > amount) {
            amountToAdd = amount;
            player.pakyakitems.get(player.pakyakitems.indexOf(item)).incrementAmount(-amount);
        } else {
            amountToAdd = item.getAmount();
            player.pakyakitems.remove(index);
        }
        //     player.getPA().sendFrame126("" + (player.pakyakitems.size()) + "/"+ player.safeBoxSlots, 35005);
        player.getItems().addItem(item.getId(), amountToAdd);
        sendItems();
        sendInventoryItems();
        return true;
    }

    /**
     * Handles depositing of items into the looting bag
     * @param id		The id of the item being deposited
     * @param amount	The amount of the item being deposited
     */
    public void deposit(int id, int amount, boolean atonce) {
        if (amount >= Integer.MAX_VALUE) {
            amount = player.getItems().getItemAmount(id);
        }
        if (amount >= player.getItems().getItemAmount(id)) {
            amount = player.getItems().getItemAmount(id);
        }
        int bagSpotsLeft = player.packyakSlots - player.pakyakitems.size();
        boolean stackable = player.getItems().isStackable(id);
        boolean bagContainsItem = containsItem(id);
        if (amount > bagSpotsLeft && !stackable) {
            if (!(stackable && bagContainsItem)) {
                amount = bagSpotsLeft;
            }
        }
        if (!player.getItems().playerHasItem(id)) {
            return;
        }
        if (player.pakyakitems.size() >= player.packyakSlots) {
            player.sendMessage("The pack yak cannot hold anymore items.");
            return;
        }


        if (!configurationPermitted()) {
            player.sendMessage("You cannot do this right now.");
            return;
        }
        if (countItems(id) + amount >= Integer.MAX_VALUE || countItems(id) + amount <= 0) {
            return;
        }


        //add here!
        player.getItems().deleteItem2(id,amount);
        addItemToList(id,amount);



        if(!atonce) {
            resetItems();//uncommenting this stops inventory flash?
        }
        sendItems();
        sendInventoryItems();
        //   }

    }


    public int countItems(int id) {
        int count = 0;
        for (LootingBagItem item : player.pakyakitems) {
            if (item.getId() == id) {
                count += item.getAmount();
            }
        }
        return count;
    }

    public void addItemToList(int id, int amount) {
        for (LootingBagItem item : player.pakyakitems) {
            if (item.getId() == id) {
                if (item.getAmount() + amount >= Integer.MAX_VALUE) {
                    return ;
                }
                if (player.getItems().isStackable(id)) {
                    item.incrementAmount(amount);
                    return ;
                }
            }
        }
        if (!player.getItems().isStackable(id)) {
            for(int i = 0 ; i < amount; i++)
                player.pakyakitems.add(new LootingBagItem(id, 1));
//            for(int i = 1 ; i < amount; i++)
//            player.pakyakitems.add(new LootingBagItem(id, i));
        } else {
            player.pakyakitems.add(new LootingBagItem(id, amount));
        }

        return;
    }



//    public boolean addItemToListLootingBag(int id, int amount) {
//        for (LootingBagItem item : player.pakyakitems) {
//            if (item.getId() == id) {
//                if (item.getAmount() + amount >= Integer.MAX_VALUE) {
//                    return false;
//                }
//                if (player.getItems().isStackable(id)) {
//                    item.incrementAmount(amount);
//                    return true;
//                }
//            }
//        }
//        player.pakyakitems.add(new LootingBagItem(id, amount));
//        sendItems();
//        player.getItems().addItemToBankOrDrop(id, amount);
//        return true;
//    }
    /**
     * Closing the looting bag and resetting
     */
    public void closedepositbox() {
        player.setSidebarInterface(3, 3213);
        player.getPA().closeAllWindows();
    }

    public void onClose() {
        player.inpakyak = false;
    }

//    public void depositall() {
//        if (System.currentTimeMillis() - player.lastBankDeposit < 5000)
//            return;
//
//        player.lastBankDeposit = System.currentTimeMillis();
//
//        for (int slot = 0; slot < player.playerItems.length; slot++) {
//            if (player.playerItems[slot] > 0 && player.playerItemsN[slot] > 0) {
//
//                deposit(player.playerItems[slot] - 1, player.playerItemsN[slot],true);
//            }
//        }
//        resetItems();
//        sendItems();
//        sendInventoryItems();
//
//    }

//    public void depositEquipment() {
//        for (int slot = 0; slot < player.playerEquipment.length; slot++) {
//            if (player.playerEquipment[slot] > 0 && player.playerEquipmentN[slot] > 0) {
//                deposit(player.playerEquipment[slot], player.playerEquipmentN[slot], true);
//
//            }
//        }
//    }

    public void sendItems() {
        final int START_ITEM_INTERFACE = 45349;
        for (int i = 0; i < 28; i++) {
            player.getPA().itemOnInterface(-1, -1, START_ITEM_INTERFACE, i);
        }
        for (int i = 0; i < 28; i++) {
            int id = 0;
            int amt = 0;

            if (i < player.pakyakitems.size()) {
                LootingBagItem item = player.pakyakitems.get(i);
                if (item != null) {
                    id = item.getId();
                    amt = item.getAmount();
                }
            }

            if (id <= 0) {
                id = -1;
            }

            player.getPA().itemOnInterface(id, amt, START_ITEM_INTERFACE, i);
        }
    }

    public void sendInventoryItems() {
        player.getItems().resetItems(5064);

        player.getItems().resetTempItems();
    }


    //updates inventory?
    private void resetItems() {
        player.getItems().resetItems(3214);

        player.getPA().requestUpdates();
    }

}


