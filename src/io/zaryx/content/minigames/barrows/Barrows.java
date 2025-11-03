package io.zaryx.content.minigames.barrows;


import io.zaryx.Server;
import io.zaryx.content.achievement_diary.impl.MorytaniaDiaryEntry;
import io.zaryx.content.minigames.barrows.brothers.*;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.items.EquipmentSet;
import io.zaryx.util.Misc;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class Barrows {

    public static final Boundary GRAVEYARD = new Boundary(3537, 3268, 3583, 3397, 0);
    public static final Boundary TOMBS = new Boundary(3533, 9677, 3580, 9721, 3);
    public static final Boundary TUNNEL = new Boundary(3523, 9666, 3579, 9723, 0);
    public static final Boundary AROUND_CHEST_ROOM = new Boundary(3544, 9686, 3558, 9702, 0);
    private static final Boundary[] AREA = { GRAVEYARD, TOMBS, TUNNEL };

    public static final double SPECIAL_CHANCE = 0.2;

    public void resetGame() {
//        // Maze is reset on teleporting into tunnels.
        //maze.randomizeSpawnPoint();
//        chest.reset();
//        npcController.reset();
        puzzle.reset();
    }
    /**
     * Randomizes the puzzle on doors.
     */
    private  BarrowsPuzzleDisplay puzzle;

    private Player player;

    private ArrayList<Brother> brothers;

    private int monstersKilled;

    private boolean completed;

    public Barrows(Player player) {
        this.player = player;
        initialize();
        this.puzzle = new BarrowsPuzzleDisplay();

        resetGame();
    }

    //you need to split it up
//    public void customclips(){
//
////
//        if (Boundary.isIn(player,TUNNEL)) {
//            //individual
//
//            player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,3540,9694, player.getHeight());
//            player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,3534,9700, player.getHeight());
//            player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,3535,9700, player.getHeight());
//
//
//            for(int x = 3534 ;x<= 3535;x++)
//                for(int y = 9701 ;y<= 9705;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 3541 ;x<= 3545;x++)
//                for(int y = 9711 ;y<= 9712;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//
//            for(int x = 3558 ;x<= 3562;x++)
//                for(int y = 9694 ;y<= 9695;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 3568 ;x<= 3569;x++)
//                for(int y = 9684 ;y<= 9688;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 3541 ;x<= 3545;x++)
//                for(int y = 9694 ;y<= 9695;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//            return;
//        }
//
//
////        if (Boundary.isIn(player,Boundary.LUMBRIDGE)) {
////            for(int x = 3241 ;x<= 3251;x++)
////                for(int y = 3225 ;y<= 3226;y++)
////                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
////        }
//        if (Boundary.isIn(player,Boundary.TAVELRY_DUNGEON)) {
//
//            for(int x = 2423 ;x<= 2427;x++)
//                for(int y = 9809 ;y<= 9820;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 2427 ;x<= 2435;x++)
//                for(int y = 9829 ;y<= 9831;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//
//
//            //tavelry
//            for(int x = 2934 ;x<= 2938;x++)
//                player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, 9829, player.getHeight());
//            for(int x = 2933 ;x<= 2938;x++)
//                player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, 9828, player.getHeight());
//            for(int x = 2931 ;x<= 2936;x++)
//                player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, 9827, player.getHeight());
//
//            for(int x = 2931 ;x<= 2938;x++)
//                player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, 9826, player.getHeight());
//
//
//
//
//            for(int x = 2936 ;x<= 2941;x++)
//                for(int y = 9809 ;y<= 9813;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 2938 ;x<= 2942;x++)
//                for(int y = 9803 ;y<= 9809;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//
//            for(int x = 2941 ;x<= 2946;x++)
//                for(int y = 9799 ;y<= 9802;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 2950 ;x<= 2970;x++)
//                for(int y = 9770 ;y<= 9797;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            //System.out.println("here?");
//            for(int x = 3530 ;x<= 3539;x++)
//                for(int y = 9707 ;y<= 9716;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//            for(int x = 3547 ;x<= 3556;x++)
//                for(int y = 9707 ;y<= 9716;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 3564 ;x<= 3573;x++)
//                for(int y = 9690 ;y<= 9699;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 3530 ;x<= 3539;x++)
//                for(int y = 9690 ;y<= 9699;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 3547 ;x<= 3556;x++)
//                for(int y = 9690 ;y<= 9699;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 3564 ;x<= 3573;x++)
//                for(int y = 9690 ;y<= 9699;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 3530 ;x<= 3539;x++)
//                for(int y = 9673 ;y<= 9682;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 3530 ;x<= 3556;x++)
//                for(int y = 9673 ;y<= 9682;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//            for(int x = 3563 ;x<= 3573;x++)
//                for(int y = 9673 ;y<= 9682;y++)
//                    player.getRegionProvider().removeClip(RegionProvider.NPC_TILE_FLAG,x, y, player.getHeight());
//
//
//
//            return;
//        }
//
//
//    }
    public void initialize() {
        this.puzzle = new BarrowsPuzzleDisplay();
        reset();
        resetGame();
        completed = false;
    }

    public void reset() {
        brothers =new ArrayList<>();
        brothers.add(new Ahrim(player));
        brothers.add(new Dharok(player));
        brothers.add(new Guthan(player));
        brothers.add(new Karil(player));
        brothers.add(new Torag(player));
        brothers.add(new Verac(player));
        brothers.get(new Random().nextInt(brothers.size())).setFinalBrother(true);
        monstersKilled = 0;
    }

    /**
     * Draws the interface that shows which brothers the player has killed whilst in the barrow minigame.
     */
//    public void drawInterface() {
//        Optional<Brother> Ahrim = player.getBarrows().getBrother(1672);
//        Optional<Brother> Dharok = player.getBarrows().getBrother(1673);
//        Optional<Brother> Guthan  = player.getBarrows().getBrother(1674);
//        Optional<Brother> Karil = player.getBarrows().getBrother(1675);
//        Optional<Brother> Torag = player.getBarrows().getBrother(1676);
//        Optional<Brother> Verac = player.getBarrows().getBrother(1677);
//        PlayerAssistant assistant = player.getPA();
//        for (Brother brother : brothers) {
//            String text =  brother.getName();
//            String color = brother.isDefeated() ? "878787" : "878787";
//            assistant.sendFrame126("<col=" + color + ">" + text + "</col>", brother.getFrameId());
//        }
//        if(Ahrim.get().isDefeated()){
//            assistant.sendFrame70(0,0,60887);
//        } else {
//            assistant.sendFrame70(-1000,0,60887);
//        }
//        if(Dharok.get().isDefeated()){
//            assistant.sendFrame70(0,0,60888);
//        } else {
//            assistant.sendFrame70(-1000,0,60888);
//        }
//        if(Guthan.get().isDefeated()){
//            assistant.sendFrame70(0,0,60889);
//        } else {
//            assistant.sendFrame70(-1000,0,60889);
//        }
//        if(Karil.get().isDefeated()){
//            assistant.sendFrame70(0,0,60890);
//        } else {
//            assistant.sendFrame70(-1000,0,60890);
//        }
//        if(Torag.get().isDefeated()){
//            assistant.sendFrame70(0,0,60891);
//        } else {
//            assistant.sendFrame70(-1000,0,60891);
//        }
//        if(Verac.get().isDefeated()){
//            assistant.sendFrame70(0,0,60892);
//        } else {
//            assistant.sendFrame70(-1000,0,60892);
//        }
//        assistant.sendFrame126("Kill Count: "+Integer.toString(getKillCount()), 27508);
//    }

    /**
     * Determines whether the player is within the Barrows area.
     *
     * @return True if the player is in the Barrows area, false otherwise.
     */
    public boolean inBarrows() {
        return Boundary.isIn(player, AREA);
    }

    /**
     * Return an ArrayList of all brothers.
     *
     * @return ArrayList of all brothers.
     */
    public ArrayList<Brother> getBrothers() {
        return brothers;
    }

    /**
     * Get the instance of a Barrows brother by their id.
     *
     * @param npcId The NPC id of the Barrows brother.
     * @return The instance of the brother.
     */
    public Optional<Brother> getBrother(int npcId) {
        return brothers.stream().filter(brother -> brother.getId() == npcId).findFirst();
    }

    /**
     * Get the instance of a Barrows brother by their name.
     *
     * @param name The name of the Barrows brother.
     * @return The instance of the brother.
     */
    public Optional<Brother> getBrother(String name) {
        return brothers.stream().filter(brother -> brother.getName().toLowerCase().equals(name.toLowerCase())).findFirst();
    }

    /**
     * Test whether a given id matches that of a brother.
     *
     * @param npcId The id which we want to test.
     * @return True in case the id matches a brother, false otherwise.
     */
    public boolean isBrother(int npcId) {
        return brothers.stream().anyMatch(brother -> brother.getId() == npcId);
    }

    /**
     * Get the Barrows brother which will appear in the hidden tunnel.
     *
     * @return The instance of the Barrows brother.
     */
    public Optional<Brother> getLastBrother() {
        return brothers.stream().filter(Brother::isFinal).findFirst();
    }

    /**
     * Set a given brother to final, and all others to non-final.
     *
     * @param name The name of the brother which is to be set to final.
     */
    public void setLastBrother(String name) {
        brothers.forEach(brother -> {
            if (brother.getName().toLowerCase().equals(name.toLowerCase())) {
                brother.setFinalBrother(true);
            } else if (brother.isFinal()) {
                brother.setFinalBrother(false);
            }
        });
    }

    /**
     * Handle the spawning of a brother by their coffin id.
     *
     * @param coffinId The id of the coffin.
     */
    public void spawnBrother(int coffinId) {
        Optional<Brother> brother = brothers.stream().filter(b -> b.getCoffinId() == coffinId).findFirst();
        brother.ifPresent(Brother::handleSpawn);
    }

    /**
     * Try to dig down the mound.
     */
    public void digDown() {
        brothers.stream().anyMatch(Brother::digDown);
    }

    /**
     * Move up the stairs of a sarcophagus.
     *
     * @param stairsId The object id of the stairs.
     */
    public void moveUpStairs(Position pos) {
        Optional<Brother> brother = brothers.stream().filter(b -> b.getStairsId().getX() == pos.getX() && b.getStairsId().getY() == pos.getY()).findFirst();
//if(brother.isPresent())//;
//        System.out.println("here. "+pos);
        brother.ifPresent(Brother::moveUpStairs);
    }

    /**
     * Returns the instance of a currently spawned brother.
     *
     * @return The instance of the brother.
     */
    public Optional<Brother> getActive() {
        return brothers.stream().filter(brother -> !brother.isDefeated() && brother.isActive()).findFirst();
    }

    /**
     * Count how many brothers have been killed since starting the current Barrows instance.
     *
     * @return The total kill count.
     */
    private int countDeadBrothers() {
        return brothers.stream().filter(Brother::isDefeated).collect(Collectors.toList()).size();
    }

    /**
     * Increase the amount of tunnel monsters killed by 1.
     */
    public void increaseMonsterKilled() {
        monstersKilled++;
    }

    /**
     * Set how many monsters have been killed in the tunnel.
     *
     * @param amount The amount of monsters that have been killed in the tunnel.
     */
    public void setMonstersKilled(int amount) {
        monstersKilled = amount;
    }

    /**
     * Returns the amount of monsters killed in the hidden tunnel.
     *
     * @return The amount of monsters killed in the hidden tunnel.
     */
    public int getMonsterKillCount() {
        return monstersKilled;
    }

    /**
     * Return the total killcount of the current Barrows instance.
     *
     * @return The total killcount.
     */
    public int getKillCount() {
        return monstersKilled + countDeadBrothers();
    }

    /**
     * Return whether the Barrows minigame is completed or not.
     *
     * @return True in case the event is completed, false otherwise.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Set whether the minigame has been completed or not.
     *
     * @param completed True in case the minigame is completed, false otherwise.
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Drains prayer from the Player, depending on how many brothers are killed.
     */
    void drainPrayer() {
        int newPrayer = player.playerLevel[6] - (8 + countDeadBrothers());
        player.playerLevel[6] = Integer.max(0, newPrayer);
        player.getPA().refreshSkill(6);
    }

//    public void test() {
//        Optional<Brother> Ahrim = player.getBarrows().getBrother(1672);
//        Optional<Brother> Dharok = player.getBarrows().getBrother(1673);
//        Optional<Brother> Guthan  = player.getBarrows().getBrother(1674);
//        Optional<Brother> Karil = player.getBarrows().getBrother(1675);
//        Optional<Brother> Torag = player.getBarrows().getBrother(1676);
//        Optional<Brother> Verac = player.getBarrows().getBrother(1677);
//        completed = true;
//        setCompleted(true);
//        RewardList rewardList = new RewardList();
//        setMonstersKilled(25);
//        if (Ahrim.isPresent()&&Dharok.isPresent()&&Guthan.isPresent()&&Karil.isPresent()&&Torag.isPresent()&&Verac.isPresent()){
//            Ahrim.get().handleDeath();
//
//            Dharok.get().handleDeath();
//            Guthan.get().handleDeath();
//            Karil.get().handleDeath();
//            Torag.get().handleDeath();
//            Verac.get().handleDeath();
//        }
//        for (int i = 0; i < 12; i++) {
//            player.getPA().itemOnInterface(-1, -1, 64503, i);
//        }
//        brothers.stream().filter(Brother::isDefeated).forEach(br -> rewardList.addAll(br.getRewards()));
//        giveRewards(rewardList);
//        reset();
//    }

    /**
     * Handle clicking the chest in the hidden tunnel.
     */
    public void useChest() {
        getLastBrother().ifPresent(brother -> {
            if (!completed) {
                if (!brother.isDefeated()) {
                    brother.spawnBrother();
                } else {
                    completed = true;
                    RewardList rewardList = new RewardList();
                    brothers.stream().filter(Brother::isDefeated).forEach(br -> rewardList.addAll(br.getRewards()));
                    player.setBarrowsChestCounter(player.getBarrowsChestCounter() + 1);
                    //  player.sendMessage("Your Barrows Chest count is: @red@"+ player.getBarrowsChestCounter() + "@bla@.");

                    giveRewards(rewardList);
                    //Achievements.increase(player, AchievementType.BARROWS_RUNS, 1);
                    //DailyTasks.increase(player, PossibleTasks.BARROWS_CHESTS);


                    if (!Server.getEventHandler().isRunning(player, "barrows_tunnel")) {
                        Server.getEventHandler().submit(new TunnelEvent("barrows_tunnel", player, 1));
                    }
                    player.sendMessage("The tunnel starts to collapse as you search the chest.");
                    if (EquipmentSet.AHRIM.isWearingBarrows(player) || EquipmentSet.KARIL.isWearingBarrows(player)
                            || EquipmentSet.DHAROK.isWearingBarrows(player)
                            || EquipmentSet.VERAC.isWearingBarrows(player)
                            || EquipmentSet.GUTHAN.isWearingBarrows(player)
                            || EquipmentSet.TORAG.isWearingBarrows(player)) {
                        player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.BARROWS_CHEST);
                    }
                    reset();
                }
            } else {
                player.sendMessage("The chest has already been emptied.");
            }
        });
    }

    /**
     * Give the player a certain amount of rewards from the minigame.
     *
     * @param rewardList The list of rewards which to choose from.
     */
    private void giveRewards(RewardList rewardList) {
        for (int i = 0; i < 12; i++) {
            player.getPA().itemOnInterface(-1, -1, 64503, i);
        }
        int totalvalue = 0;
        ArrayList<RewardItem> randomRewards = getWeightedRewards(rewardList, 6);
        StringBuilder sb = new StringBuilder();
        @SuppressWarnings("unused")
        int gotBarrows = 0;
        int spot = 0;
        player.getPA().showInterface(25400);
        for (RewardItem item : randomRewards) {
            int itemId = item.getId();
            int amount = item.getAmount();
            totalvalue += player.getShops().getItemShopValue(item.getId()) * item.getAmount();
            boolean morytaniaLegs = player.getRechargeItems().hasAnyItem(13114, 13115);
            if (item.getRarityLevel() == RewardLevel.COMMON) {
                if (morytaniaLegs) {
                    amount = amount + amount / 2;
                }
            }
            //spot++;
            if (!player.getItems().addItem(itemId, amount)) {
                Server.itemHandler.createGroundItem(player, itemId, player.getX(), player.getY(), player.heightLevel, amount);
            }
            player.getPA().itemOnInterface(itemId, amount, 64503, spot);
            spot++;
            if (item.getRarityLevel() == RewardLevel.RARE) {
                //  player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.BARROWS_PIECE);
                gotBarrows = 1;
            }
            sb.append(item.getId()).append(", ");
        }

        player.sendMessage("@blu@Your chest is worth around "+ Misc.insertCommasToNumber(Integer.toString(totalvalue))+" coins.");
    }

    /**
     * Select a certain amount of items from the given list, using weighted selection.
     *
     * @param rewardList The list of items which to choose from.
     * @param amount The amount of items which is to be chosen.
     * @return A list containing all randomly chosen items.
     */
    private ArrayList<RewardItem> getWeightedRewards(RewardList rewardList, int amount) {
        ArrayList<RewardItem> randomRewards =new ArrayList<>();
        int totalWeight = rewardList.getTotalWeight(getKillCount());

        for (int i = 0; i < amount; i++) {
            double random = Math.random() * totalWeight;
            int index = 0;
            for (int n = 0; n < rewardList.size(); n++) {
                switch (rewardList.get(n).getRarityLevel()) {
                    case COMMON:
                        random -= rewardList.firstTierRarity(getKillCount());
                        break;
                    default:
                        random -= rewardList.get(n).getRarityLevel().getRarity();
                        break;
                }
                if (random <= 0.0d) {
                    index = n;
                    break;
                }
            }
            RewardItem reward = rewardList.get(index);
            boolean flag = false;
            for (RewardItem item : randomRewards) {
                if (reward.getId() == item.getId()) {
                    item.setMinAmount(reward.getMinAmount() + item.getMinAmount());
                    item.setMaxAmount(reward.getMaxAmount() + item.getMaxAmount());
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                randomRewards.add(reward);
            }
        }
        return randomRewards;
    }

    public BarrowsPuzzleDisplay getPuzzle() {
        return puzzle;
    }
}
