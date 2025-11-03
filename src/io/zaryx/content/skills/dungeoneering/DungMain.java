package io.zaryx.content.skills.dungeoneering;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.item.lootable.impl.RaidsChestCommon;
import io.zaryx.content.item.lootable.impl.RaidsChestRare;
import io.zaryx.content.items.LootItem;
import io.zaryx.content.items.LootTable;
import io.zaryx.content.skills.SkillHandler;
import io.zaryx.model.collisionmap.doors.Location;
import io.zaryx.model.definitions.NpcStats;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.world.objects.GlobalObject;
import io.zaryx.util.Misc;

import java.util.*;
import java.util.stream.Collectors;

import static io.zaryx.content.skills.dungeoneering.DungConstants.checkInstances;

public class DungMain extends SkillHandler {

    private static final String DUNG_DAMAGE_ATTRIBUTE_KEY = "dung_damage";


    //TODO
    public boolean killerWatt;
    public boolean passagesNpcs;
    public boolean miningRoomNpcs;
    public boolean ice;
    public boolean chest;
    public boolean mystic;
    public boolean orkGreen;

    public boolean boss; //TODO
    public boolean bossDead; //TODO

    private int correctGather;
    private int correctKey;

    private static final int FROZEN_KEY = 3741;
    private static final int BRAIN = 4199;
    private static final int AXE = 1351;
    private static final int SKULL = 13195;
    private static final int BLUEW = 8974;


    private int dungPoints;
    private int groupPoints;

    private boolean orkKilled;

    public long lastActivity = -1;

    private Object objectTask = new Object(); //TODO EDIT CERTAIN OBJECT FOR TASK

    private final Map<String, Long> playerLeftAt = Maps.newConcurrentMap();

    private final Map<String, Integer> dungPlayers = Maps.newConcurrentMap();

    private final Map<String, Integer> activeDungRoom = Maps.newConcurrentMap();

    /**
     * The door location of the current paths
     */
    private final ArrayList<Location> roomPaths = new ArrayList<Location>();
    /**
     * The names of the current rooms in path
     */
    private final ArrayList<String> roomNames = new ArrayList<String>();

    private Player getPlayerToReceiveUnique(List<Player> players) {
        return Misc.random(players);
    }

    public static GameItem rollRegular() {
        return RaidsChestCommon.randomChestRewards(); //TODO REWARDS CHEST
    }

    public static GameItem rollUnique() { //TODO REWARDSCHEST RARE CHANCE
        return RaidsChestRare.randomChestRewards();
    }

    private boolean roomSpawned;

    private int getRoomForPlayer(Player player) {
        return activeDungRoom.getOrDefault(player.getLoginNameLower(), 0);
    }





    public void filterPlayers() {
        dungPlayers.entrySet().stream().filter(entry -> !PlayerHandler.getOptionalPlayerByLoginName(entry.getKey()).isPresent()).forEach(entry -> dungPlayers.remove(entry.getKey()));
    }

    /**
     * Kill all spawns for the dung leader if left
     */
    public void killAllSpawns() {
        NPCHandler.kill(currentHeight, currentHeight + 3, 394, 3341, 7563, 7566, 7585, 7560, 7544, 7573, 7604, 7606, 7605, 7559, 7527, 7528, 7529, 7553, 7554, 7555); //TODO
    }

    public void removePlayer(Player c) {

        dungPlayers.remove(c.getLoginNameLower());
        dungPoints = dungPlayers.entrySet().stream().mapToInt(val -> val.getValue()).sum();
        if (dungPlayers.isEmpty()) {
            //TODO
        }
    }

    public List<Player> getPlayers() {

        List<Player> activePlayers = Lists.newArrayList();
        filterPlayers();
        dungPlayers.keySet().stream().forEach(playerName -> {
            PlayerHandler.getOptionalPlayerByLoginName(playerName).ifPresent(player -> activePlayers.add(player));
        });
        return activePlayers;
    }

    public boolean login(Player player) {
        long leftAt = playerLeftAt.getOrDefault(player.getLoginNameLower(), (long) -1);
        if (leftAt > 0) {
            playerLeftAt.remove(player.getLoginNameLower());
            if (System.currentTimeMillis() - leftAt <= 60000) {
                dungPlayers.put(player.getLoginNameLower(), 0);
                player.setDungInstance(this);
                player.sendMessage("@red@You rejoin the Dungeon");
                lastActivity = -1;
                return true;
            }
        }
        return false;
    }

    public void resetRoom(Player player) {
        this.activeDungRoom.put(player.getLoginNameLower(), 0);
    }

    public void resetBossRoom(Player player) {
        this.activeDungRoom.put(player.getLoginNameLower(), 9);
    }

    public void logout(Player player) {
        player.setDungInstance(null);
        removePlayer(player);
        playerLeftAt.put(player.getLoginNameLower(), System.currentTimeMillis());
        checkInstances();
    }

    public boolean hadPlayer(Player player) {
        long leftAt = playerLeftAt.getOrDefault(player.getLoginNameLower(), (long) -1);
        return leftAt > 0;
    }

    public int addPoints(Player player, int points) {
        if (!dungPlayers.containsKey(player.getLoginNameLower())) return 0;
        int currentPoints = dungPlayers.getOrDefault(player.getLoginNameLower(), 0);
        dungPlayers.put(player.getLoginNameLower(), currentPoints + points);
        groupPoints = dungPlayers.entrySet().stream().mapToInt(val -> val.getValue()).sum();
        return currentPoints + points;
    }

    public int currentHeight;
    private int path;
    private int way;
    public int currentRoom;
    private boolean chestRoomDoorOpen = true;
    private final int chestToOpenTheDoor = 5 + Misc.random(20);
    private final HashSet<Integer> endRoomSearched = new HashSet<>();

    /**
     * Door locations of current Paths
     */
    private final ArrayList<Location> dungPaths = new ArrayList<>();

    /**
     * Names of the current room in path
     */

    private final ArrayList<String> dungNames = new ArrayList<>();

    /**
     * Current monsters needed to kill
     */

    private int mobCount;

    /**
     * gets the start location for the path
     *
     * @return path
     */

    public Location getStartLocation() {
        switch (path) {
            case 0:
            return DungRooms.STARTING_ROOM.doorLocation;
        }
        return DungRooms.STARTING_ROOM.doorLocation;
    }

    private static final List<Position> NERVES = Arrays.asList(
            new Position(1784, 6414),
            new Position(1758, 6414),
            new Position(1751, 6436)

    );

    /**
     * Handles Dung Rooms
     */

    public enum DungRooms {
        STARTING_ROOM("starting_room", 1, 0, new Location(1788, 6450)),
        HALLWAY_ROOM("killerwatts", 1, 0, new Location(1770, 6459)),
        ORK_ROOM("ork", 1, 0, new Location(1745, 6456)),
        PASSAGEWAY_S("passages", 1, 0, new Location(1789, 6405)),
        MINING_ROOM("miningr", 1, 0, new Location(1734, 6415)),
        FLOORBOSS("floorboss", 1, 0, new Location(1783, 6422)),
        ;

        private final Location doorLocation;
        private final int path;
        private final int way;
        private final String roomName;

        DungRooms(String name, int path1, int way1, Location door) {
            doorLocation = door;
            roomName = name;
            path = path1;
            way = way1;

        }

        public Location getDoor() {
            return doorLocation;
        }
        public int getPath() {
            return path;
        }
        public int getWay() {
            return way;
        }
        public String getRoomName() {
            return roomName;
        }
    }

    /**
     * starts the dung
     */

    GlobalObject gameObject; //TODO RENAME AFTER WE FIND AN OBJECT

    public void startDung(List<Player> players, boolean party) {
        if (DungConstants.currentDungHeight >= 100) {
            DungConstants.currentDungHeight = 4;
        }
        currentHeight = DungConstants.currentDungHeight;

        List<Integer> possibleBrain = Arrays.asList(49430, 49433);
        correctKey = possibleBrain.get(new Random().nextInt(possibleBrain.size()));
        List<Integer> possibleKey = Arrays.asList(49425, 49420, 49423);
        correctKey = possibleKey.get(new Random().nextInt(possibleKey.size()));
        List<Integer> possibleObjects = Arrays.asList(49440, 49441, 49442, 49443);
        correctGather = possibleObjects.get(new Random().nextInt(possibleObjects.size()));
            // Randomly select one of the pre-designated coordinates
            Position spawnPosition = NERVES.get(new Random().nextInt(NERVES.size()));
            // Spawn the object at the selected coordinates
            GlobalObject choppableObject = new GlobalObject(49366, spawnPosition.getX(), spawnPosition.getY(), currentHeight, 0, 10);
            Server.getGlobalObjects().add(choppableObject);


        /** TODO UPDATE THE GAME OBJECT
         *
        objectTask = new Object();
        gameObject = new GlobalObject(0000, 1111, 1111, currentHeight);
        gameObject.getRegionProvider().get(3232,5749).setClipToZero(3232,5749,currentHeight);
        gameObject.getRegionProvider().get(3232,5750).setClipToZero(3232,5750,currentHeight);
        gameObject.getRegionProvider().get(3233,5750).setClipToZero(3233,5750,currentHeight);
        gameObject.getRegionProvider().get(3233,5749).setClipToZero(3233,5749,currentHeight);
         **/
        DungConstants.currentDungHeight += 4;
        path = 1;
        way = 0;
        for (DungRooms room : DungRooms.values()) {
            if (room.getWay() == way) {
                roomNames.add(room.getRoomName());
                roomPaths.add(room.getDoor());
            }
        }
        for (Player lobbyPlayer : players) {
            if (!party) {
                if (lobbyPlayer == null) continue;
                if (!lobbyPlayer.getPosition().inDungLobby()) {
                    lobbyPlayer.sendMessage("You were not in the lobby and have been removed from the dungeon!");
                continue;
                }
            }
            lobbyPlayer.getPA().closeAllWindows();
            dungPlayers.put(lobbyPlayer.getLoginNameLower(), 0);
            activeDungRoom.put(lobbyPlayer.getLoginNameLower(), 0);
            lobbyPlayer.setDungInstance(this);
            lobbyPlayer.getPA().movePlayer(getStartLocation().getX(), getStartLocation().getY(), currentHeight);
            lobbyPlayer.sendMessage("@red@The Dungeon has started get ready!");
        }
        DungConstants.dungGames.add(this);
    }

    public void leaveDung(Player player) {
        if (System.currentTimeMillis() - player.infernoLeaveTimer < 15000) {
            player.sendMessage("You cannot leave yet, try again in a couple seconds.");
        return;
        }
        else if (player.getMode().isGroupWildyman()) {
            player.getPA().movePlayerUnconditionally(3442, 3835, 0);
            return;
        }
        player.sendMessage("@red@You have left the Dungeon!");
        player.getPA().movePlayerUnconditionally(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0);
        player.setDungInstance(null);
        removePlayer(player);
        player.specRestore = 120;
        player.specAmount = 10.0;
        player.setRunEnergy(100, true);
        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
        player.getPA().refreshSkill(Player.playerPrayer);
        player.getHealth().removeAllStatuses();
        player.getHealth().reset();
        player.getPA().refreshSkill(5);
        checkInstances();
    }

    public static class DungRank {
        private final Player player;
        private int rank;
        private final int damage;

        DungRank(Player player, int damage) {
            this.player = player;
            this.damage = damage;
        }

        @Override
        public String toString() {
            return "DungRank{" +
                    "players=" + (player == null ? null : player.getDisplayName()) +
                    ", rank = " + rank +
                    ", damage =" + damage +
                    '}';
        }
    }

    public static List<DungRank> buildRankList(List<DungRank> ranks) {
        ranks.sort(Comparator.comparingInt(it -> it.damage));
        ranks = Lists.reverse(ranks);
        for (int index = 0; index < ranks.size(); index++) {
            ranks.get(index).rank = index + 1;
        }
        return ranks;
    }

    final int Boss = 1901; //TODO

    public void handleMobDeath(Player killer, int npcType) {
        checkInstances();
        mobCount -= 1;
        /*switch(npcType) {
            case 470://TODO BOSS
                getPlayers().forEach(player -> {
                    //TODO add Task

                    String[] topDamageDealers = getTopDamageDealers();
                    if (topDamageDealers.length > 0) {
                        player.sendMessage("@red@Top Contributors");

                        for (String tdd : topDamageDealers) {
                            player.sendMessage("@pur@" + tdd + " : " + dungPlayers.get(tdd));
                        }
                    }
                    /** TODO ADD ACHIEVEMENTS
                     Achievements.increase(player, AchievementType.Dungeoneering, 1);
                     **/
                    //player.setDungLoot(true);

                    /*int uniqueBudget = dungPlayers.get(player.getLoginNameLower());
                    for (int i = 0; i < 2; i++) {
                        if (uniqueBudget <= 0)
                            break;

                        int pointsToUse = Math.min(35000, uniqueBudget);
                        double chance = (double) pointsToUse / 8675 / 100;
                        System.out.println("Dung Points = " + pointsToUse + ", Chance = " + chance + ", uniqueBudget = " + uniqueBudget);
                        uniqueBudget -= pointsToUse;
                        if (ThreadLocalRandom.current().nextDouble() < chance) {
                            Player luckiest = getPlayerToReceiveUnique(getPlayers());
                            GameItem item = rollUnique();
                            luckiest.getDungRewards().add(item);
                            dungPlayers.remove(luckiest.getLoginNameLower());
//                            luckiest.getCollectionLog().handleDrop(luckiest, 7554, item.getId(), item.getAmount(), true); TODO CHANGE NPC ID, AND EDIT REWARDS BEFORE!

                        }
                    }*/
                    //Regular Drops
                   /* getPlayers().stream().filter(p -> p.getDungRewards().isEmpty()).forEach(p -> {
                        int playerPoints = Math.max(131071, dungPlayers.get(player.getLoginNameLower()));
                        if (playerPoints == 0)
                            return;
                        for (int i = 0; i < 2; i++) {
                            GameItem rolled = rollRegular();
                            int amount = rolled.getAmount();
                            if (amount > 1) {
                                amount = Misc.random((amount / 2), amount);
                            }
                            if (!rolled.getDef().isStackable() && !rolled.getDef().isNoted())
                                rolled.setId(rolled.getDef().getNotedItemIfAvailable());
                            p.getDungRewards().add(new GameItem(rolled.getId(), amount));
                        }
                    });
                });

                /*CycleEventHandler.getSingleton().addEvent(objectTask, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        gameObject.getRegionProvider().get(gameObject.getX(), gameObject.getY()).removeObject(gameObject.getObjectId(), gameObject.getX(), gameObject.getY(), currentHeight, 10, 0);
                        gameObject.setId(-1);
                        Server.getGlobalObjects().add(new GlobalObject(gameObject.getObjectId(), gameObject.getX(), gameObject.getY(), currentHeight, 0, 10));
                        container.stop();
                    }
                }, 2);
                return;
        }*/
                switch (npcType) {
                    case 3:
                        orkKilled = true;
                        GlobalObject teleporter = new GlobalObject(49450, 1742, 6440, currentHeight, 0, 10);
                        Server.getGlobalObjects().add(teleporter);
                }
                if (killer != null) {
                    int randomPoints = Misc.random(100, 500) * 3;
                    int newPoints = addPoints(killer, randomPoints);

                    killer.sendMessage("@red@You receive "+ randomPoints +" points from killing this monster.");
                    killer.sendMessage("@red@You now have "+ newPoints +" points.");
                }
                if (mobCount <= 0) {
                    getPlayers().stream().forEach(player ->
                            player.sendMessage("@red@It seems this room has been cleared!"));
                    roomSpawned = false;
                } else {
                    getPlayers().stream().forEach(player ->
                            player.sendMessage("@red@ there are still " + mobCount + " enemies left in the room!"));
        }

    }

    public void spawnDungNpc(int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence, boolean attackPlayer) {
        int[] stats = getScaledStats(HP, attack, defence, getPlayers().size());
        attack = stats[0];
        defence = stats[1];
        HP = stats[2];
        NPC npc = NPCSpawning.spawn(npcType, x, y, heightLevel, WalkingType, maxHit, attackPlayer,
                NpcStats.builder().setAttackLevel(attack).setHitpoints(HP).setDefenceLevel(defence).createNpcStats());
        npc.setDungInstance(this);
        npc.getBehaviour().setRespawn(false);
    }

    public static int[] getScaledStats(int HP, int attack, int defence, int groupSize) {
        int modifier = 0;
        int attackScale = 1;
        int defenceScale = 1;
        int baseMod = 100;
        int baseLowMod = 35;
        if (groupSize > 1) {
            if (HP < 200) {
                baseMod = 10;
            }
            modifier = (baseMod + (groupSize * (int) (HP * .15))); //GROUPSIZE MODIFER
            attackScale = (baseLowMod + (groupSize * 10));
            defenceScale = (baseLowMod + (groupSize * 10));
          }
        defence = (defence + defenceScale);
        HP = (HP + modifier);
        attack = (attack + attackScale);
        return new int[] { attack, defence, HP };
    }



    public String[] getTopDamageDealers() {
        // Sort the players by damage in descending order
        Map<String, Integer> sortedPlayers = dungPlayers.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        // Get the top 3 players or fewer if there are less than 3
        int topCount = Math.min(3, sortedPlayers.size());
        String[] topPlayers = new String[topCount];

        int index = 0;
        for (Map.Entry<String, Integer> entry : sortedPlayers.entrySet()) {
            topPlayers[index] = entry.getKey();
            index++;

            if (index >= topCount) {
                break;
            }
        }

        return topPlayers;
    }

    public static final int KILLERWATT_HP = 150;
    public static final int KILLERWATT_ATTACK = 200;
    public static final int KILLERWATT_DEFENCE = 100;

    public static final int VASA_HP = 400;
    public static final int VASA_ATTACK = 250;
    public static final int VASA_DEFENCE = 130;

    public static final int OLM_HP = 500;
    public static final int OLM_ATTACK = 272;
    public static final int OLM_DEFENCE = 350;

    /**
     * Spawn npcs for current dung room
     * @param currentDungRoom the room
     */

    public void spawnNpcs(int currentDungRoom) {
        int height = currentHeight;
        System.out.println("Raid Height = " + height);
        if (currentRoom >= roomNames.size()) {
            System.out.println("No more rooms to spawn NPCs in.");
            return;
        }
        switch(roomNames.get(currentDungRoom)) {
            case "killerwatts":
                if (killerWatt) {
                    return;
                }
                spawnDungNpc(470, 1769, 6456, height, 1, KILLERWATT_HP, 25, KILLERWATT_ATTACK, KILLERWATT_DEFENCE, true);
                spawnDungNpc(470, 1766, 6455, height, 1, KILLERWATT_HP, 25, KILLERWATT_ATTACK, KILLERWATT_DEFENCE, true);
                spawnDungNpc(470, 1762, 6450, height, 1, KILLERWATT_HP, 25, KILLERWATT_ATTACK, KILLERWATT_DEFENCE, true);
                spawnDungNpc(470, 1759, 6445, height, 1, KILLERWATT_HP, 25, KILLERWATT_ATTACK, KILLERWATT_DEFENCE, true);
                spawnDungNpc(470, 1750, 6459, height, 1, KILLERWATT_HP, 25, KILLERWATT_ATTACK, KILLERWATT_DEFENCE, true);
                killerWatt = true;
                mobCount += 5;
                break;
            case "ork":
                if (orkGreen) {
                    return;
                }
                spawnDungNpc(3, 1747, 6447, height, 1, 550, 45, 450, 230, true);
                orkGreen = true;
                mobCount += 1;
                break;
            case "passages": //TODO REPLACE WITH NEW NPC IDS PASSAGE SOUTH AND ADD MORE NPCS
                if (passagesNpcs) {
                    return;
                }
                spawnDungNpc(472, 1785, 6408, height, 1, KILLERWATT_HP, 25, KILLERWATT_ATTACK, KILLERWATT_DEFENCE, true);
                passagesNpcs = true;
                mobCount += 1;
                break;
            case "miningr": //TODO REPLACE WITH NEW NPC IDS GATHERING AND ADD MORE NPCS
                if (miningRoomNpcs) {
                    return;
                }
                spawnDungNpc(474, 1749, 6418, height, 1, KILLERWATT_HP, 25, KILLERWATT_ATTACK, KILLERWATT_DEFENCE, true);
                miningRoomNpcs = true;
                mobCount += 1;
                break;
            default:
                roomSpawned = false;
                break;
        }
    }

    /**
     *
     * Handles Object Clicking
     */

    public boolean handleObjectClick(Player player, int objectId, int x, int y) {
        player.objectDistance = 3;
        switch (objectId) {

            /**Chopping & mining
             *
             */

            case 49440:
            case 49441:
            case 49442:
            case 49443:
                if (mobCount > 0) {
                    player.sendMessage("@red@You must defeat all current enemies in the room before gathering!");
                    return true;
                }
                if (player.getItems().playerHasItem(BLUEW)) {
                    player.sendMessage("@red@You already have the blue water!");
                    return false;
                }
                    if (objectId == correctGather) {
                        player.getItems().addItem(BLUEW, 1);

                        player.sendMessage("@blu@You mine succesfully gather blue water!");
                    } else {
                        player.appendDamage(player, 25, Hitmark.ZULCANO_SHIELD);
                        player.sendMessage("@red@The axon explodes and deals you damage!");
                    }
                break;

            case 49366:
                if (mobCount > 0) {
                    player.sendMessage("@red@You must defeat all current enemies in the room before gathering!");
                    return true;
                }
                if (!player.getItems().playerHasItem(AXE)) {
                    player.sendMessage("@red@You need an axe to chop down this tree!");
                    return true;
                    }
                    player.startAnimation(879);
                    Server.getGlobalObjects().add(new GlobalObject(49363, 1768, 6425, currentHeight, 0, 10));
                    player.getItems().addItem(SKULL, 1); // Assuming LOG is the ID for the log
                    player.sendMessage("@blu@You chop down the nerve and find a skull!");

                break;

            case 49438:
                if (player.getItems().playerHasItem(AXE)) {
                    player.sendMessage("@red@You already have a axe to use!");
                    return false;
                }
                player.sendMessage("@blu@you find a axe in the crate!");
                // Add the key to the player's inventory
                player.getItems().addItem(AXE, 1);
                break;
            /**
             * Start of keys/items needed for doors
             */

            case 49420:
            case 49423:
            case 49425:
                if (player.getItems().playerHasItem(FROZEN_KEY)) {
                    player.sendMessage("@red@You already have the Frozen Key!");
                    return false;
                }
                if (objectId == correctKey) {
                    player.getItems().addItem(FROZEN_KEY, 1);
                    player.sendMessage("@blu@You find a Frozen Key!");
                } else {
                    player.appendDamage(2 + Misc.random(12), Hitmark.DAWNBRINGER);
                    player.sendMessage("@red@You hear an explosion and get damaged!");
                }
                break;
            case 49433:
            case 49430:
                if (player.getItems().playerHasItem(BRAIN)) {
                    player.sendMessage("@red@You already have a brain to use!");
                    return false;
                }
                if (objectId == correctKey) {
                    player.sendMessage("@blu@eww you find a brain in the chest!");
                    // Add the key to the player's inventory
                    player.getItems().addItem(BRAIN, 1);
                } else {
                    player.appendDamage(2 + Misc.random(12), Hitmark.DAWNBRINGER);
                    player.sendMessage("@red@You hear an explosion and get damaged!");
                }
                break;

                    /**
                     *End
                     */
            /**
             *
             * Teleporters
             */
            case 49414:
                if (!player.getItems().playerHasItem(BLUEW)) {
                    player.sendMessage("@red@You need the blue water to use this teleporter!");
                }
                if (player.objectX == 1730 && player.objectY == 6434) {
                    player.objectDistance = 3;
                    player.getPA().spellTeleport(1788, 6418, currentHeight, false);
                    nextRoom(player);
                }
                break;
            case 49413:
                if (!player.getItems().playerHasItem(BLUEW)) {
                    player.sendMessage("@red@You need the blue water to use this teleporter!");
                }
                if (player.objectX == 1789 && player.objectY == 6417) {
                    player.objectDistance = 3;
                    player.getPA().spellTeleport(1731, 6434, currentHeight, false);
                    nextRoom(player);
                }
                break;
            case 49411:
                if (player.objectX == 1782 && player.objectY == 6420) {
                    player.objectDistance = 3;
                    nextRoom(player);
                }
                break;
            /*case 49411:
                if (player.) {
                    if (roomNames.get(getRoomForPlayer(player)).equalsIgnoreCase("chest") && !chestRoomDoorOpen) {
                        player.sendMessage("This passage is blocked you must find the key from the chest to advance!");
                    } else {
                        player.objectDistance = 3;
                        nextRoom(player);
                    }
                    return true;
                }*/

            case 49419:
                    if (player.objectX == 1771 && player.objectY == 6459) {
                        if (!player.getItems().playerHasItem(FROZEN_KEY)) {
                            player.sendMessage("You need the frozen key to get through this passageway!");
                            return true;
                        }
                    if (roomNames.get(getRoomForPlayer(player)).equalsIgnoreCase("chest") && !chestRoomDoorOpen) {
                        player.sendMessage("This passage is blocked you must find the key from the chest to advance!");
                    } else {
                        player.getItems().deleteItem(FROZEN_KEY, 1);
                        player.objectDistance = 3;
                        nextRoom(player);
                    }
                }
                    break;
            case 49412:
                if (player.objectX == 1744 && player.objectY == 6457) {
                    if (!player.getItems().playerHasItem(BRAIN)) {
                        player.sendMessage("You need a brain to open this door!");
                        return true;
                    }
                    if (roomNames.get(getRoomForPlayer(player)).equalsIgnoreCase("chest") && !chestRoomDoorOpen) {
                        player.sendMessage("This passage is blocked you must find the key from the chest to advance!");
                    } else {
                        player.objectDistance = 3;
                        nextRoom(player);
                    }
                }
                break;
            case 49410:
                if (!player.getItems().playerHasItem(SKULL)) {
                    player.sendMessage("You need a skull to go through this passage!");
                } else {
                    player.objectDistance = 3;
                    nextRoom(player);
                }
            case 49450:
                if (player.objectX == 1742 && player.objectY == 6440) {
                        player.objectDistance = 3;
                        nextRoom(player);
                }
                break;
        }
        return false;
    }

    /**
     * Goes to the next room
     */

    public void nextRoom(Player player) {
        player.objectDistance = 3;
        if (activeDungRoom.getOrDefault(player.getLoginNameLower(), 0) == currentRoom && mobCount > 0) {
            player.objectDistance = 3;
            player.sendMessage("You must defeat all current enemies in the room before moving on!");
            return;
        }

        if (!roomSpawned) {
            player.objectDistance = 3;
            currentRoom += 1;
            roomSpawned = true;
            spawnNpcs(currentRoom);
        }
        int playerRoom = activeDungRoom.getOrDefault(player.getLoginNameLower(), 0) + 1;
        if (playerRoom >= roomPaths.size()) {
            player.sendMessage("You can't go this way");
            return;
        }
        player.getPA().movePlayer(roomPaths.get(playerRoom).getX(), roomPaths.get(playerRoom).getY(), player.getHeight());
        activeDungRoom.put(player.getLoginNameLower(), playerRoom);
    }

    public static void damage(Player player, int damage) {
        int current = getDamage(player);
        player.getAttributes().setInt(DUNG_DAMAGE_ATTRIBUTE_KEY, current + damage);
    }
    public static int getDamage(Player player) {
        return player.getAttributes().getInt(DUNG_DAMAGE_ATTRIBUTE_KEY, 0);
    }
    private static void resetDamage(Player player) {
        player.getAttributes().removeInt(DUNG_DAMAGE_ATTRIBUTE_KEY);
    }


    private static LootTable uniqueTable = new LootTable()
            .addTable(1,
                    new LootItem(21034, 1, 20), // dexterous scroll
                    new LootItem(21079, 1, 20), // arcane scroll
                    new LootItem(24466, 1, 4),  // Twisted Horns
                    new LootItem(21000, 1, 3),  // twisted buckler
                    new LootItem(21012, 1, 3),  // dragon hunter crossbow
                    new LootItem(21015, 1, 2),  // dinh's bulwark
                    new LootItem(21018, 1, 2),  // ancestral hat
                    new LootItem(21021, 1, 2),  // ancestral top
                    new LootItem(21024, 1, 2),  // ancestral bottom
                    new LootItem(20784, 1,2),   // dragon claws
                    new LootItem(21003, 1,1),   // elder maul
                    new LootItem(21043, 1, 1),  // kodai insignia
                    new LootItem(20997, 1, 1),  // twisted bow
                    new LootItem(25910, 1, 2)   // twisted horn
            );

    private static LootTable regularTable = new LootTable() // regular table. the "amount" here is the number used to determine the amount given to players based on how many points they have, for example 1 soul rune per 20 points
            .addTable(1,
                    new LootItem(560, 20, 1),       // death rune
                    new LootItem(565, 16, 1),       // blood rune
                    new LootItem(566, 10, 1),       // soul rune
                    new LootItem(892, 7, 1),        // rune arrow
                    new LootItem(11212, 70, 1),     // dragon arrow

                    new LootItem(3050, 185, 1),     // grimy toadflax
                    new LootItem(208, 400, 1),      // grimy ranarr weed
                    new LootItem(210, 98, 1),       // grimy irit
                    new LootItem(212, 185, 1),      // grimy avantoe
                    new LootItem(214, 205, 1),      // grimy kwuarm
                    new LootItem(3052, 500, 1),     // grimy snapdragon
                    new LootItem(216, 200, 1),      // grimy cadantine
                    new LootItem(2486, 150, 1),     // grimy lantadyme
                    new LootItem(218, 106, 1),      // grimy dwarf weed
                    new LootItem(220, 428, 1),      // grimy torstol

                    new LootItem(7937, 200, 100),   // pure essence
                    new LootItem(443, 20, 1),       // silver ore
                    new LootItem(454, 20, 1),       // coal
                    new LootItem(445, 45, 1),       // gold ore
                    new LootItem(448, 45, 1),       // mithril ore
                    new LootItem(450, 100, 1),      // adamantite ore
                    new LootItem(452, 750, 1),      // runite ore

                    new LootItem(1624, 100, 1),     // uncut sapphire
                    new LootItem(1622, 170, 1),     // uncut emerald
                    new LootItem(1620, 125, 1),     // uncut ruby
                    new LootItem(1618, 260, 1),     // uncut diamond

                    new LootItem(8781, 100, 10),    // teak plank
                    new LootItem(8783, 240, 10),    // mahogany plank

                    new LootItem(21047, 131071, 1), // torn prayer scroll
                    new LootItem(21027, 131071, 1)  // dark relic
            );

}

