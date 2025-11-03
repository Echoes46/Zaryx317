package io.zaryx.content.upgrade;

import io.zaryx.content.achievement.AchievementType;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.content.prestige.PrestigePerks;
import io.zaryx.content.skills.Skill;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Right;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.items.ImmutableItem;
import io.zaryx.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class UpgradeInterface {

    private Player player;
    private UpgradeMaterials selectedUpgrade;
    public ArrayList<UpgradeMaterials> upgradeMaterialsArrayList;

    private int TOKEN_ID = 995;

    public UpgradeInterface(Player player) {
        this.player = player;
    }

    public boolean handleButton(int buttonId) {

        switch (buttonId) {
            case 35022:
                handleUpgrade(false);
                return true;
            case 35005:
                openInterface(UpgradeMaterials.UpgradeType.WEAPON);
                return true;
            case 35006:
                openInterface(UpgradeMaterials.UpgradeType.ARMOUR);
                return true;
            case 35007:
                openInterface(UpgradeMaterials.UpgradeType.ACCESSORY);
                return true;
            case 35008:
                openInterface(UpgradeMaterials.UpgradeType.MISC);
                return true;
        }

        return false;
    }

    public void handleItemAction(int slot) {
        if (upgradeMaterialsArrayList != null && upgradeMaterialsArrayList.get(slot) != null) {
            showUpgrade(upgradeMaterialsArrayList.get(slot));
        }
    }


    public void showUpgrade(UpgradeMaterials upgrade) {

        selectedUpgrade = upgrade;

        player.getPA().itemOnInterface(new GameItem(upgrade.getReward().getId(), upgrade.getReward().getAmount()), 35017,0);
        player.getPA().sendString(35018, "Points req: @whi@" + Misc.formatCoins(upgrade.getCost()));


        if (upgrade.getReward().getId() == 99999 ||
                        upgrade.getReward().getId() == 99998) {
            player.getPA().sendString(35019, "Success rate: @whi@" + upgrade.getSuccessRate() + "%");

        } else {
            player.getPA().sendString(35019, "Success rate: @cya@" + getBoost(upgrade.getSuccessRate()) + "%");
        }
        if (player.playerLevel[Skill.FORTUNE.getId()] < upgrade.getLevelRequired()) {
            player.getPA().sendString(35021, "Lvl : " + upgrade.getLevelRequired());

        }

    }


    public void openInterface(UpgradeMaterials.UpgradeType type) {
        player.getPA().sendConfig(5334, type.ordinal());

        selectedUpgrade = null;

        player.getPA().itemOnInterface(new GameItem(-1, 1), 35017, 0);
        player.getPA().sendString(35018, "Point req: @whi@---");
        player.getPA().sendString(35019, "Success rate: @whi@---");
        player.sendMessage("@bla@[@red@Upgrade Points@bla@]@blu@ Your remaining points : " + Misc.formatCoins(player.foundryPoints));
        upgradeMaterialsArrayList = UpgradeMaterials.getForType(type);
        for (int i = 0; i < 72; i++) {
            if (upgradeMaterialsArrayList.size() > i) {
                player.getPA().itemOnInterface(
                        upgradeMaterialsArrayList.get(i).getRequired().getId(), upgradeMaterialsArrayList.get(i).getRequired().getAmount(), 35150, i);
            } else {
                player.getPA().itemOnInterface(-1, 1, 35150, i);
            }
        }

        player.getPA().showInterface(35000);
    }


    public void handleUpgrade(boolean all) {
        if (System.currentTimeMillis() - player.clickDelay <= 2200) {
            player.sendMessage("You must wait before trying to upgrade again!");
            return;
        }
        player.clickDelay = System.currentTimeMillis();

        if (selectedUpgrade == null) {
            player.sendMessage("Choose an item to upgrade.");
            return;
        }

        Arrays.stream(UpgradeMaterials.values()).forEach(val -> {
            if (val.getRequired().getId() == selectedUpgrade.getRequired().getId()) {
                if (player.getLevelForXP(player.playerXP[Skill.FORTUNE.getId()]) < val.getLevelRequired()) {
                    player.sendMessage("You don't have the required Fortune level to upgrade this item.");
                    return;
                }

                if (getRestrictions(val, all)) {
                        if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33072) && Misc.random(0, 100) >= 90)
                        {
                            player.getItems().deleteItem2(val.getRequired().getId(), val.getRequired().getAmount());
                        } else {
                            player.getItems().deleteItem2(val.getRequired().getId(), val.getRequired().getAmount());
                        }

                    if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33072) && Misc.random(0, 100) >= 90)
                    {
                        player.sendMessage("@red@Your Fusion Master Perk Save's the Cost of your upgrade!");
                    } else {
                        player.foundryPoints = (player.foundryPoints - val.getCost());
                    }
//                    if (player.getItems().isWearingItem(26314)) {
//                        player.getItems().deleteItem2(val.getRequired().getId(), val.getRequired().getAmount() / 4);
//                    }
                    TimerTask task = new TimerTask() {
                        int tick = 0;

                        @Override
                        public void run() {
                            if (tick == 0) {
                                player.sendMessage("You try to upgrade....");
                            } else if (tick == 2) {
                                int amount = (player.getItems().getInventoryCount(26886));
                                int amount1 = (player.getItems().getInventoryCount(26885));
                                int amount2 = (player.getItems().getInventoryCount(26884));
                                int amount3 = (player.getItems().getInventoryCount(26883));
                                int amount4 = (player.getItems().getInventoryCount(26882));
                                double a = Misc.random(0, 99);
                                double b = val.getSuccessRate();
                                b = getBoost(val.getSuccessRate());

                                if (player.getItems().getInventoryCount(26886) == amount) {
                                    player.getItems().deleteItem2(26886, amount);
                                }
                                if (player.getItems().getInventoryCount(26885) == amount1) {
                                    player.getItems().deleteItem2(26885, amount1);
                                }
                                if (player.getItems().getInventoryCount(26884) == amount2) {
                                    player.getItems().deleteItem2(26884, amount2);
                                }
                                if (player.getItems().getInventoryCount(26883) == amount3) {
                                    player.getItems().deleteItem2(26883, amount3);
                                }
                                if (player.getItems().getInventoryCount(26882) == amount4) {
                                    player.getItems().deleteItem2(26882, amount4);
                                }


                                boolean success =  a <= b;
                                if (success) {
                                    player.sendMessage("You successfully upgraded your item!");
                                    Achievements.increase(player, AchievementType.UPGRADE, 1);
                                    player.getInventory().addToInventory(new ImmutableItem(val.getReward()));
                                    if (val.isRare()) {
                                            String msg = "@blu@<img=18>[UPGRADE]<img=18>@red@ " + player.getDisplayName()
                                                    + " Has successfully achieved "
                                                    + val.getReward().getDef().getName();

                                            PlayerHandler.executeGlobalMessage(msg);

                                        if (val.isRare() && val.getType().equals(UpgradeMaterials.UpgradeType.WEAPON)) {
                                            player.getCollectionLog().handleDrop(player, 6, val.getReward().getId(), 1);
                                        } else if (val.isRare() && val.getType().equals(UpgradeMaterials.UpgradeType.ARMOUR)) {
                                            player.getCollectionLog().handleDrop(player, 7, val.getReward().getId(), 1);
                                        } else if (val.isRare() && val.getType().equals(UpgradeMaterials.UpgradeType.ACCESSORY)) {
                                            player.getCollectionLog().handleDrop(player, 8, val.getReward().getId(), 1);
                                        } else if (val.isRare() && val.getType().equals(UpgradeMaterials.UpgradeType.MISC)) {
                                            player.getCollectionLog().handleDrop(player, 9, val.getReward().getId(), 1);
                                        }

                                    }
                                    player.getPA().addSkillXPMultiplied(val.getXp(), Skill.FORTUNE.getId(), true);

                                } else {
                                    boolean ReturnItem = (Math.random() * 100) <= getDonator();
                                    if (ReturnItem && player.amDonated >= 100 && val.getRequired().getId() != 33189 && val.getRequired().getId() != 33190 && val.getRequired().getId() != 33191) {
                                        player.sendMessage("Your donator rank saves your item!");
                                        player.getItems().addItemUnderAnyCircumstance(val.getRequired().getId(), 1);
                                    }
                                    if (val.getRequired().getId() == 33189 || val.getRequired().getId() == 33190 || val.getRequired().getId() == 33191) {
                                        player.getItems().addItemUnderAnyCircumstance(val.getRequired().getId(), 1);
                                    }
                                    player.sendMessage("You failed to upgrade!");
                                }

                                player.sendMessage("@bla@[@red@UPGRADE@bla@]@blu@ Your remaining points : " + Misc.formatCoins(player.foundryPoints));
                                cancel();
                            }
                            tick++;
                        }

                    };

                    Timer timer = new Timer();
                    timer.schedule(task, 500, 500);
                }
            }
        });
    }

    public int getDonator() {

        int multiplier = 0;

        if (player.getRights().isOrInherits(Right.Almighty_Donator)) {
            multiplier += 65;
        } else if (player.getRights().isOrInherits(Right.Apex_Donator)) {
            multiplier += 50;
        } else if (player.getRights().isOrInherits(Right.Platinum_Donator)) {
            multiplier += 45;
        } else if (player.getRights().isOrInherits(Right.Gilded_Donator)) {
            multiplier += 40;
        } else if (player.getRights().isOrInherits(Right.Supreme_Donator)) {
            multiplier += 35;
        } else if (player.getRights().isOrInherits(Right.Major_Donator)) {
            multiplier += 30;
        } else if (player.getRights().isOrInherits(Right.Extreme_Donator)) {
            multiplier += 25;
        } else if (player.getRights().isOrInherits(Right.Great_Donator)) {
            multiplier += 20;
        }

        if (PrestigePerks.hasRelic(player, PrestigePerks.ATTUNE_PERKS)) {
            multiplier += 10;
        }

        return multiplier;
    }

    public double getBoost(double chance) {
        double percentBoost = 0D;

        if (player.amDonated >= 25 && player.amDonated < 50) {
            percentBoost += 1;
        } else if (player.amDonated >= 50 && player.amDonated < 100) {
            percentBoost += 3;
        } else if (player.amDonated >= 100 && player.amDonated < 250) {
            percentBoost += 5;
        } else if (player.amDonated >= 250 && player.amDonated < 500) {
            percentBoost += 7;
        }else if (player.amDonated >= 500 && player.amDonated < 1250) {
            percentBoost += 9;
        }else if (player.amDonated >= 1250 && player.amDonated < 2500) {
            percentBoost += 11;
        }else if (player.amDonated >= 2500 && player.amDonated < 4000) {
            percentBoost += 13;
        }else if (player.amDonated >= 4000 && player.amDonated < 6500) {
            percentBoost += 15;
        }else if (player.amDonated >= 6500 && player.amDonated < 15000) {
            percentBoost += 20;
        }else if (player.amDonated >= 15000) {
            percentBoost += 30;
        }

        if (player.centurion > 0) {
            percentBoost += 20;
        }
        int amount = (player.getItems().getInventoryCount(26886));
        int amount1 = (player.getItems().getInventoryCount(26885));
        int amount2 = (player.getItems().getInventoryCount(26884));
        int amount3 = (player.getItems().getInventoryCount(26883));
        int amount4 = (player.getItems().getInventoryCount(26882));
        if (player.getItems().hasItemOnOrInventory(26886)) {
            percentBoost += 50 * amount;
        }
        if (player.getItems().hasItemOnOrInventory(26885)) {
            percentBoost += 20 * amount1;
        }
        if (player.getItems().hasItemOnOrInventory(26884)) {
            percentBoost += 10 * amount2;
        }
        if (player.getItems().hasItemOnOrInventory(26883)) {
            percentBoost += 5 * amount3;
        }
        if (player.getItems().hasItemOnOrInventory(26882)) {
            percentBoost += 1 * amount4;
        }

        if (PrestigePerks.hasRelic(player, PrestigePerks.ATTUNE_PERKS)) {
            percentBoost += 10;
        }

        double multiplier = 1 + (percentBoost / 100D);
//        System.out.println("Multiplier: " + multiplier);
        chance += percentBoost;
//        System.out.println("Chance: " + chance);
        return chance;
    }

    private boolean getRestrictions(UpgradeMaterials data, boolean all) {
        ItemDef definition = ItemDef.forId(data.getRequired().getId() + 1);
        boolean noted = false;
        //if (definition.isNoted() && definition.isStackable()) {
        //    String name = definition.getName();
        //    definition = ItemDef.forId(data.getRequired().getId());
        //    String originalName = definition.getName();
        //    noted = name.equals(originalName);
        //}
        if (noted && player.getItems().getInventoryCount(data.getRequired().getId() + 1) > 1) {
            int amount = all ? player.getItems().getInventoryCount(data.getRequired().getId() + 1) : 1;
            if (player.getItems().getInventoryCount(data.getRequired().getId() + 1) < amount) {
                player.sendMessage("You do not have the required items!");
                return false;
            }
            if (player.foundryPoints < (data.getCost())) {
                player.sendMessage("You don't have enough upgrade points to upgrade this item.");
                return false;
            }
        } else {
            if (player.getItems().getInventoryCount(data.getRequired().getId()) < data.getRequired().getAmount()) {
                player.sendMessage("You do not have the required items!");
                return false;
            }
            if (player.foundryPoints < (data.getCost())) {
                player.sendMessage("You don't have enough upgrade points to upgrade this item.");
                return false;
            }
        }
        return true;
    }


}
