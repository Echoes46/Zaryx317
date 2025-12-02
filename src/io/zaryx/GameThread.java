package io.zaryx;

import io.zaryx.content.QuestTab;
import io.zaryx.content.TriviaBot;
import io.zaryx.content.battlepass.Pass;
import io.zaryx.content.bosses.DonorBoss;
import io.zaryx.content.bosses.DonorBoss2;
import io.zaryx.content.bosses.DonorBoss3;
import io.zaryx.content.bosses.wintertodt.Wintertodt;
import io.zaryx.content.events.monsterhunt.ShootingStars;
import io.zaryx.content.events.monsterhunt.CrystalTree;
import io.zaryx.content.instances.InstanceHeight;
import io.zaryx.content.minigames.blastfurnance.BlastFurnace;
import io.zaryx.content.wilderness.ActiveVolcano;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.net.ChannelHandler;
import io.zaryx.net.login.RS2LoginProtocol;
import io.zaryx.sql.dailytracker.DailyDataTracker;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.DiscordIntegration;
import io.zaryx.util.task.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class GameThread extends Thread {

    public static final String THREAD_NAME = "GameThread";
    public static final int PRIORITY = 9;
    private static final Logger logger = LoggerFactory.getLogger(GameThread.class);
    private final List<Consumer<GameThread>> tickables = new CopyOnWriteArrayList<>();
    private final Runnable startup;
    private long totalCycleTime = 0;

    public GameThread(Runnable startup) {
        setName(THREAD_NAME);
        setPriority(PRIORITY);
        this.startup = startup;
        setTickables();
    }

    private void setTickables() {
        tickables.add(i -> Server.itemHandler.process());
        tickables.add(i -> Server.npcHandler.process());
        tickables.add(i -> Server.playerHandler.process());
        tickables.add(i -> Server.shopHandler.process());
        tickables.add(i -> CrystalTree.Tick());
        tickables.add(i -> DiscordIntegration.givePoints());
        tickables.add(i -> ActiveVolcano.Tick());
        tickables.add(i -> ShootingStars.Tick());
        tickables.add(i -> Server.getGlobalObjects().pulse());
        tickables.add(i -> CycleEventHandler.getSingleton().process());
        tickables.add(i -> Server.getEventHandler().process());
        tickables.add(i -> Wintertodt.pulse());
        tickables.add(i -> DonorBoss.tick());
        tickables.add(i -> DonorBoss2.tick());
        tickables.add(i -> DonorBoss3.tick());
        tickables.add(i -> Pass.tick());
        tickables.add(i -> BlastFurnace.process());
        tickables.add(i -> TaskManager.sequence());
        tickables.add(i -> QuestTab.Tick());
        tickables.add(i -> DailyDataTracker.newDay());
        tickables.add(i -> Server.tickCount++);
    }

    private void tick() {
        for (Consumer<GameThread> tickable : tickables) {
            try {
                tickable.accept(this);
                TriviaBot.sequence();
            } catch (Exception e) {
                logger.error("Error caught in GameThread, should be caught up the chain and handled.", e);
            }
        }

        if (Server.getTickCount() % 50 == 0) {
            StringJoiner joiner = new StringJoiner(", ");
            joiner.add("runtime=" + Misc.cyclesToTime(Server.getTickCount()));
            joiner.add("connections=" + ChannelHandler.getActiveConnections());
            joiner.add("players=" + PlayerHandler.getPlayers().size());
            joiner.add("uniques=" + PlayerHandler.getUniquePlayerCount());
            joiner.add("npcs=" + NPCHandler.nonNullStream().count());
            joiner.add("reserved-heights=" + InstanceHeight.getReservedCount());
            joiner.add("average-cycle-time=" + (totalCycleTime / Server.getTickCount()) + "ms");
            joiner.add("handshakes-per-tick=" + (RS2LoginProtocol.getHandshakeRequests() / Server.getTickCount()));

            long totalMemory = Runtime.getRuntime().totalMemory();
            long usedMemory = totalMemory - Runtime.getRuntime().freeMemory();
            joiner.add("memory=" + Misc.formatMemory(usedMemory) + "/" + Misc.formatMemory(totalMemory));

            logger.info("Status [" + joiner.toString() + "]");
        }
    }

    @Override
    public void run() {
        startup.run();
        while (!Thread.interrupted()) {
            long time = System.currentTimeMillis();
            try {
                tick();
            } catch (Exception e) {
                logger.error("An error occurred while running the game thread tickables.", e);
            }
            long pastTime = System.currentTimeMillis() - time;
            totalCycleTime += pastTime;
            if (pastTime < 600) {
                try {
                    Thread.sleep(600 - pastTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Game thread interrupted during sleep.", e);
                }
            } else {
                logger.error("Game thread took " + Misc.insertCommas(String.valueOf(pastTime)) + "ms to process!");
            }
        }
    }
}
