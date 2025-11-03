package io.zaryx.content.minigames.coinflip;

import io.zaryx.annotate.PostInit;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.items.ItemAction;
import io.zaryx.util.Misc;


public class CoinFlip {

    @PostInit
    public static void itemActionHandler() {
        ItemAction.registerInventory(13192, 1, (player, item) -> openInterface(player, item.getId()));
        ItemAction.registerInventory(10944, 1, (player, item) -> openInterface(player, item.getId()));
    }

    private static void sendGifChange(Player player, String image) {
        player.getPA().sendGifChange(24490, image);
    }

    public static void openInterface(Player player, int cardID) {
        if (cardID == -1) {
            return;
        }
        player.coinFlipCard = cardID;
        reDrawInterface(player);
    }

    private static void reDrawInterface(Player player) {
        sendGifChange(player, Misc.random(1) == 0 ? "Blue-Coin-STILL" : "Red-Coin-STILL");

        if (player.coinFlipColor.equalsIgnoreCase("red")) {
            player.getPA().sendConfig(2001, 1);
            player.getPA().sendConfig(2000, 0);
        } else if (player.coinFlipColor.equalsIgnoreCase("blue")) {
            player.getPA().sendConfig(2001, 0);
            player.getPA().sendConfig(2000, 1);
        } else {
            player.getPA().sendConfig(2001, 0);
            player.getPA().sendConfig(2000, 1);
        }

        if (player.coinFlipPrize != -1) {
            player.getPA().itemOnInterface(player.coinFlipPrize,1,24496,0);
            player.getPA().sendString(24498, ItemDef.forId(player.coinFlipPrize).getName());
        } else {
            player.getPA().itemOnInterface(-1,1, 24496,0);
            player.getPA().sendString(24498, "");
        }

        player.getPA().showInterface(24485);
    }

    public static void handleSpin(Player player) {
        if (player.coinFlipProgress) {
            player.sendMessage("@red@Don't interrupt the spin, or else you face loosing rewards!");
            return;
        }

        player.getItems().deleteItem2(player.coinFlipCard, 1);
        player.coinFlipProgress = true;

        if (player.coinFlipColor.equalsIgnoreCase("red")) {
            int rng = Misc.random(0, 100);
            if (rng < 35) {
                sendGifChange(player, "Red-Winner");
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        player.getItems().addItemUnderAnyCircumstance(player.coinFlipPrize, 1);
                        PlayerHandler.executeGlobalMessage("<img=19><col=FFD500><shad=0>[CoinFlip]@bla@ " + player.getDisplayName() + " has just @red@won @bla@ a @gr1@" + ItemDef.forId(player.coinFlipPrize).getName() + " @bla@from a coin flip!");
                        container.stop();
                    }
                },4);
            } else {
                sendGifChange(player, "Red-Loser");
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        PlayerHandler.executeGlobalMessage("<img=19><col=FFD500><shad=0>[CoinFlip]@bla@ " + player.getDisplayName() + " has just @red@failed @bla@at obtaining a @gr1@" + ItemDef.forId(player.coinFlipPrize).getName() + " @bla@from a coin flip!");
                        container.stop();
                    }
                },4);
            }
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    sendGifChange(player, "Red-Coin-STILL");
                    player.coinFlipProgress = false;
                    container.stop();
                }
            }, 6);
        } else if (player.coinFlipColor.equalsIgnoreCase("blue")) {
            int rng = Misc.random(0, 100);
            if (rng < 35) {
                sendGifChange(player, "Blue-Winner");
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        player.getItems().addItemUnderAnyCircumstance(player.coinFlipPrize, 1);
                        PlayerHandler.executeGlobalMessage("<img=19><col=FFD500><shad=0>[CoinFlip]@bla@ " + player.getDisplayName() + " has just @red@won @bla@ a @gr1@" + ItemDef.forId(player.coinFlipPrize).getName() + " @bla@from a coin flip!");
                        container.stop();
                    }
                },4);
            } else {
                sendGifChange(player, "Blue-Loser");
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        PlayerHandler.executeGlobalMessage("<img=19><col=FFD500><shad=0>[CoinFlip]@bla@ " + player.getDisplayName() + " has just @red@failed @bla@at obtaining a @gr1@" + ItemDef.forId(player.coinFlipPrize).getName() + " @bla@from a coin flip!");
                        container.stop();
                    }
                },4);

            }
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    sendGifChange(player, "Blue-Coin-STILL");
                    player.coinFlipProgress = false;
                    container.stop();
                }
            }, 6);
        }
    }

    public static boolean handleButton(Player player, int id) {
        if (id == 24499) {
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            player.getPA().closeAllWindows();
            CoinFlipJson coinFlipJson = CoinFlipJson.getInstance();
            int itemlistsize = coinFlipJson.getLootItemsForCardId(player.coinFlipCard).size();
            player.getPA().setScrollableMaxHeight(24502, (int) (2.37 * itemlistsize));
            for (int i = 0; i < coinFlipJson.getLootItemsForCardId(player.coinFlipCard).size(); i++) {
                player.getPA().itemOnInterface(coinFlipJson.getLootItemsForCardId(player.coinFlipCard).get(i).getId(),
                        coinFlipJson.getLootItemsForCardId(player.coinFlipCard).get(i).getAmount(), 24503, i);
            }
            //Settings choose item
            player.getPA().showInterface(24500);
            return true;
        }
        if (id == 24491) {
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            if (player.coinFlipColor.equalsIgnoreCase("")) {
                player.sendMessage("@red@You need to select a side first!");
                return true;
            }
            if (player.coinFlipPrize == -1) {
                player.sendMessage("@red@You need to select a prize before flipping the coin!");
                return true;
            }
            handleSpin(player);
            return true;
        }
        if (id == 24489) {
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            //Red Coin
            player.getPA().sendConfig(2001, 1);
            player.getPA().sendConfig(2000, 0);
            player.coinFlipColor = "red";
            sendGifChange(player, "Red-Coin-STILL");
            return true;
        }
        if (id == 24488) {
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            //Blue Coin
            player.getPA().sendConfig(2001, 0);
            player.getPA().sendConfig(2000, 1);
            player.coinFlipColor = "blue";
            sendGifChange(player, "Blue-Coin-STILL");
            return true;
        }
        return false;
    }

    public static boolean handleItemChoice(Player player, int interfaceID, int slot, int ItemID) {
        if (interfaceID == 24503) {
            player.coinFlipPrize = ItemID;
            reDrawInterface(player);
            return true;
        }
        return false;
    }
}
