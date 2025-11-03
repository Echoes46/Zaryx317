package io.zaryx.content.minigames.dz;

import com.google.common.collect.Lists;
import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.prestige.PrestigePerks;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.definitions.NpcDef;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bloody_Battle {

    private final Player player;

    public Bloody_Battle(Player player) {
        this.player = player;
    }

    public void spawn(NPC npc) {
        final int[][] type = Wave.MAIN;
        if(player.getBloody_wave() >= type.length && player.getBloody_wave() > 0
                && Boundary.isIn(player, Boundary.DONATOR_ZONE_BLOODY)) {
            stop();
            return;
        }

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer event) {
                if(player == null) {
                    event.stop();
                    return;
                }

                if(!Boundary.isIn(player, Boundary.DONATOR_ZONE_BLOODY)) {
                    player.setBloody_wave(0);
                    event.stop();
                    return;
                }

                if(player.getBloody_wave() >= type.length && player.getBloody_wave() > 0) {
                    onStopped();
                    event.stop();
                    return;
                }

                if (player.getBloody_wave_kills() <= 0) {
                    setKillsRemaining(type[player.getBloody_wave()].length);
                    player.getPA().sendString(35428, "Bloody Wave: " + (player.getBloody_wave() + 1) + ", Kills Remaining : " + player.getBloody_wave_kills());

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < type[player.getBloody_wave()].length; i++) {
                        if (!sb.toString().contains(NpcDef.forId(type[player.getBloody_wave()][i]).getName())) {
                            sb.append(NpcDef.forId(type[player.getBloody_wave()][i]).getName()).append(", ");
                        }
                    }

                    player.getPA().sendString(35429, "Next Wave: " + sb.toString());
                    if (player.getBloody_wave() == 20 && Misc.isLucky(20) || player.getBloody_wave() == 40 && Misc.isLucky(30) || player.getBloody_wave() == 60 && Misc.isLucky(45)) {
                        giveExtraLoot();
                    }

                    //System.out.println(player.getDisplayName() + " //has advanced a wave in the minigame!");

                    for (int i = 0; i < getKillsRemaining(); i++) {
                        int npcType = type[player.getBloody_wave()][i];
                        int x = 2037 + Misc.random(-4, 4);
                        int y = 4529 + Misc.random(-3, 3);
                        NPC n = NPCHandler.getNpc(npcType);
                        int maxhit = new NPCHandler().getMaxHit(player, n);
                        NPCSpawning.spawnNpc(player, npcType, x, y, player.getIndex() * 4, 1, maxhit, true, false);
                    }
                }
                event.stop();
            }

            @Override
            public void onStopped() {


            }
        }, 3);
    }

    public void leaveGame() {
        reward();
        player.sendMessage("You have left the Bloody Battle.");
        player.getPA().movePlayer(2613, 3874, 0);
        player.setBloody_wave(0);
        player.setBloody_wave_kills(0);
        killAllSpawns();
    }

    public void create() {
        player.getPA().removeAllWindows();
        player.getPA().movePlayer(2044, 4529, player.getIndex() * 4);
        player.sendMessage("Welcome to the Bloody Battle. Your first wave will start soon.", 255);
        player.setBloody_wave(0);
        spawn(null);
    }

    public void stop() {
        reward();
        player.getPA().movePlayer(2613, 3874, 0);
        player.getDH().sendStatement("Congratulations for finishing the Bloody Battle!");
        player.setBloody_wave(0);
        giveExtraLoot();
    }

    public void handleDeath() {
        reward();
        player.getPA().movePlayer(2613, 3874, 0);
        player.getDH().sendStatement("Unfortunately you died on wave " + player.getBloody_wave() + ". Better luck next time.");
        player.nextChat = 0;
        player.setBloody_wave(0);
        killAllSpawns();
    }


    public void killAllSpawns() {
        for (int i = 0; i < NPCHandler.npcs.length; i++) {
            NPC npc = NPCHandler.npcs[i];
            if (npc != null) {
                if (NPCHandler.isSpawnedBy(player, npc)) {
                    npc.unregister();
                }
            }
        }
    }

    public void reward() {
        int amt = getPoints(player.getBloody_wave());
        if (Misc.isLucky(5)) {
            amt += 150;
        }
        if (PrestigePerks.hasRelic(player, PrestigePerks.BLOODY_MINIGAME)) {
            amt *= 2;
        }
        player.setBloody_points(player.getBloody_points() + amt);
        player.sendMessage("You now have a total of " + (player.getBloody_points()) + " Bloody Points.");
    }

    public int getKillsRemaining() {
        return player.getBloody_wave_kills();
    }

    public void setKillsRemaining(int remaining) {
        player.setBloody_wave_kills(remaining);
    }

    public int getPoints(int wave) {
        if (wave >= 20) {
            return wave * 6;
        }
        return wave * 3;
    }

    public void giveExtraLoot() {
        List<GameItem> rewards = getRandomItem();
        for (GameItem item : rewards) {
            player.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
            player.sendMessage("You've earned a reward in Bloody Minigame, " +item.getDef().getName() + "!");
        }

    }

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {

        items.put(LootRarity.COMMON, //50% chance
                Arrays.asList(

                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(8167),//foundry mystery chest
                        new GameItem(27112),//barrows gloves
                        new GameItem(2577),//ranger boots
                        new GameItem(6920),//infinity boots
                        new GameItem(11840),//dragon boots
                        new GameItem(6199),//m box
                        new GameItem(4447),
                        new GameItem(2528),
                        new GameItem(24365),
                        new GameItem(11230, 200),
                        new GameItem(21948, 100),
                        new GameItem(2996, 100),
                        new GameItem(6803,1),
                        new GameItem(6799, 1),
                        new GameItem(26365, 1),
                        new GameItem(6828),
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(8167),//foundry mystery chest
                        new GameItem(27112),//barrows gloves
                        new GameItem(2577),//ranger boots
                        new GameItem(6920),//infinity boots
                        new GameItem(11840),//dragon boots
                        new GameItem(6199),//m box
                        new GameItem(4447),
                        new GameItem(2528),
                        new GameItem(24365),
                        new GameItem(11230, 200),
                        new GameItem(21948, 100),
                        new GameItem(2996, 100),
                        new GameItem(6803,1),
                        new GameItem(6799, 1),
                        new GameItem(26365, 1),
                        new GameItem(6828),
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(8167),//foundry mystery chest
                        new GameItem(27112),//barrows gloves
                        new GameItem(2577),//ranger boots
                        new GameItem(6920),//infinity boots
                        new GameItem(11840),//dragon boots
                        new GameItem(6199),//m box
                        new GameItem(4447),
                        new GameItem(2528),
                        new GameItem(24365),
                        new GameItem(11230, 200),
                        new GameItem(21948, 100),
                        new GameItem(2996, 100),
                        new GameItem(6803,1),
                        new GameItem(6799, 1),
                        new GameItem(26365, 1),
                        new GameItem(6828),
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(8167),//foundry mystery chest
                        new GameItem(27112),//barrows gloves
                        new GameItem(2577),//ranger boots
                        new GameItem(6920),//infinity boots
                        new GameItem(11840),//dragon boots
                        new GameItem(6199),//m box
                        new GameItem(4447),
                        new GameItem(2528),
                        new GameItem(24365),
                        new GameItem(11230, 200),
                        new GameItem(21948, 100),
                        new GameItem(2996, 100),
                        new GameItem(6803,1),
                        new GameItem(6799, 1),
                        new GameItem(26365, 1),
                        new GameItem(6828),
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(696, 1),//10k foundry points
                        new GameItem(8167),//foundry mystery chest
                        new GameItem(27112),//barrows gloves
                        new GameItem(2577),//ranger boots
                        new GameItem(6920),//infinity boots
                        new GameItem(11840),//dragon boots
                        new GameItem(6199),//m box
                        new GameItem(4447),
                        new GameItem(2528),
                        new GameItem(24365),
                        new GameItem(11230, 200),
                        new GameItem(21948, 100),
                        new GameItem(2996, 100),
                        new GameItem(6803,1),
                        new GameItem(6799, 1),
                        new GameItem(26365, 1),
                        new GameItem(6828),
                        new GameItem(2841),
                        new GameItem(6199),
                        new GameItem(13307, 1000),
                        new GameItem(23933, 3),
                        new GameItem(28417),
                        new GameItem(11828),
                        new GameItem(11826),
                        new GameItem(11830),
                        new GameItem(6640),
                        new GameItem(6650),
                        new GameItem(11836),
                        new GameItem(11832),
                        new GameItem(11834),
                        new GameItem(25959),
                        new GameItem(28419),
                        new GameItem(26545),
                        new GameItem(23336),
                        new GameItem(23339),
                        new GameItem(6805),
                        new GameItem(13346),
                        new GameItem(696, 10),
                        new GameItem(696, 10),
                        new GameItem(696, 10),
                        new GameItem(696, 10),
                        new GameItem(696, 10),
                        new GameItem(696, 10),
                        new GameItem(696, 10),
                        new GameItem(12588),
                        new GameItem(956)//Cannon piece

                ));

    }

    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    public List<GameItem> getRandomItem() {
        List<GameItem> rewards = Lists.newArrayList();

        rewards.add(Misc.getRandomItem(getLoot().get(LootRarity.COMMON)));

        return rewards;
    }
}
