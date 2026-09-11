package io.zaryx.content.taskmaster;
import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import java.time.*;
import java.lang.reflect.Field;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskMasterTest {
    private interface Check { void run(Player p) throws Exception; }
    private void player(Check check) throws Exception {
        Field f=Server.class.getDeclaredField("configuration"); f.setAccessible(true);
        Object previous=f.get(null); f.set(null,ServerConfiguration.getDefault());
        try { check.run(new Player(null)); } finally { f.set(null,previous); }
    }
    private TaskMasterKills task(int count,int progress,LocalDateTime deadline) {
        return new TaskMasterKills(count,progress,new GameItem[]{new GameItem(995,100)},TaskDifficulty.EASY,TaskType.COMBAT,false,deadline,"Rock crab");
    }
    @Test void cyclesRotateSeparatelyAndCompletedRewardsSurviveExpiry() throws Exception {
        player(p -> {
            LocalDateTime now=LocalDateTime.now(); TaskMaster board=p.getTaskMaster(); board.refresh(now);
            assertEquals(4,board.taskMasterKillsList.size());
            assertEquals(now.plusHours(1),board.task(0).getLocalDateTime());
            assertEquals(now.plusDays(1),board.task(2).getLocalDateTime());
            assertEquals(now.plusWeeks(1),board.task(3).getLocalDateTime());
            TaskMasterKills weekly=board.task(3), daily=board.task(2), ready=board.task(0);
            ready.setAmountKilled(ready.getAmountToKill());
            board.refresh(now.plusHours(2));
            assertSame(ready,board.task(0)); assertSame(daily,board.task(2)); assertSame(weekly,board.task(3));
            ready.setClaimedReward(true); board.refresh(now.plusHours(2)); assertNotSame(ready,board.task(0));
            board.refresh(now.plusDays(2)); assertSame(weekly,board.task(3)); assertNotSame(daily,board.task(2));
        });
    }
    @Test void savedRewardsProgressPinAndScheduleRoundTrip() throws Exception {
        player(p -> {
            TaskMaster board=p.getTaskMaster(); board.refresh(LocalDateTime.now());
            board.task(0).incrementAmountKilled(4); board.task(0).setPinned(true);
            board.task(1).setClaimedReward(true);
            String saved=board.encode(); TaskMaster loaded=new TaskMaster(p); loaded.decode(saved);
            assertEquals(saved,loaded.encode());
            loaded.refresh(LocalDateTime.now()); assertEquals(saved,loaded.encode());
            assertTrue(loaded.task(0).isPinned()); assertEquals(4,loaded.task(0).getAmountKilled());
            assertTrue(loaded.task(1).getClaimedReward()); assertTrue(loaded.task(3).isWeeklyChallenge());
        });
    }
    @Test void progressIsCappedAndExpiredOrClaimedTasksCannotAdvance() {
        TaskMasterKills t=task(10,0,LocalDateTime.now().plusDays(1));
        t.incrementAmountKilled(-1); assertEquals(0,t.getAmountKilled());
        t.incrementAmountKilled(Integer.MAX_VALUE); assertEquals(10,t.getAmountKilled());
        assertTrue(t.announceCompletion()); assertFalse(t.announceCompletion());
        t=task(10,2,LocalDateTime.now().minusSeconds(1)); t.incrementAmountKilled(5); assertEquals(2,t.getAmountKilled());
        t=task(10,2,LocalDateTime.now().plusDays(1)); t.setClaimedReward(true); t.incrementAmountKilled(5); assertEquals(2,t.getAmountKilled());
    }
    @Test void npcMatchingRejectsUnrelatedAndDoubleCountedKills() {
        assertTrue(TaskMaster.matchesNpc("Rock crab","Rock crab"));
        assertTrue(TaskMaster.matchesNpc("Barrows","Dharok the Wretched"));
        assertTrue(TaskMaster.matchesNpc("Dagannoth","Dagannoth Rex"));
        assertFalse(TaskMaster.matchesNpc("Dagannoth","Crazy archaeologist"));
        assertFalse(TaskMaster.matchesNpc("Nightmare","Nightmare"));
        assertFalse(TaskMaster.matchesNpc("Chambers","Chambers"));
        assertFalse(TaskMaster.matchesNpc("Rock crab",""));
    }
    @Test void difficultyPoolsAreBoundedAndRespectMode() {
        for(TaskDifficulty d:TaskDifficulty.values()) for(int slot=0;slot<4;slot++) {
            assertFalse(TaskMaster.candidates(slot,d,false).isEmpty(),slot+" "+d);
            for(Tasks t:TaskMaster.candidates(slot,d,true)) { assertTrue(t.wildy); assertTrue(TaskMaster.effectiveDifficulty(t).ordinal()<=d.ordinal()); }
        }
        assertFalse(TaskMaster.candidates(2,TaskDifficulty.EASY,false).contains(Tasks.INFERNAL));
        assertEquals(TaskDifficulty.ELITE,TaskMaster.effectiveDifficulty(Tasks.INFERNAL));
    }
    @Test void claimIsAtomicAndDuplicateClaimsDoNotGrantAgain() {
        TaskMasterKills t=task(10,10,LocalDateTime.now().plusDays(1)); int[] inventory={0}; int[] saves={0};
        assertEquals(TaskClaim.Result.CLAIMED,TaskClaim.claim(t,()->{inventory[0]+=100;return true;},()->inventory[0]=0,()->{assertTrue(t.getClaimedReward());assertEquals(100,inventory[0]);saves[0]++;return true;}));
        assertEquals(TaskClaim.Result.NOT_READY,TaskClaim.claim(t,()->{fail("Duplicate grant");return true;},()->fail("Duplicate rollback"),()->false));
        assertEquals(100,inventory[0]); assertEquals(1,saves[0]);
    }
    @Test void partialInventoryAndFailedSavesRollBackAndRemainClaimable() {
        TaskMasterKills t=task(10,10,LocalDateTime.now().minusDays(1)); int[] inventory={7};
        assertEquals(TaskClaim.Result.NO_SPACE,TaskClaim.claim(t,()->{inventory[0]=20;return false;},()->inventory[0]=7,()->{fail("Must not save partial rewards");return false;}));
        assertEquals(7,inventory[0]); assertFalse(t.getClaimedReward());
        assertEquals(TaskClaim.Result.SAVE_FAILED,TaskClaim.claim(t,()->{inventory[0]=100;return true;},()->inventory[0]=7,()->false));
        assertEquals(7,inventory[0]); assertFalse(t.getClaimedReward());
        assertEquals(TaskClaim.Result.CLAIMED,TaskClaim.claim(t,()->true,()->{},()->true));
    }
    @Test void deadlinesNeverDisplayNegativeAndTravelUsesExistingEntry() {
        LocalDateTime now=LocalDateTime.now();
        assertEquals("0d 0h 0m",TaskMaster.timeRemaining(now.minusDays(1),now));
        assertEquals("7d 0h 0m",TaskMaster.timeRemaining(now.plusWeeks(1),now));
        int[] destination=TaskTravel.destination(task(10,0,now.plusHours(1)));
        assertNotNull(destination); assertEquals(0,destination[0]);
    }
    @Test void legacyDailyIsNotMistakenForWeeklyAndMainSaveWins() throws Exception {
        player(p -> {
            String old="{\"amountToKill\":20,\"amountKilled\":20,\"claimedReward\":true,\"taskDifficulty\":\"EASY\",\"taskType\":\"COMBAT\",\"weekly\":true,\"localDateTime\":\"2099-01-01T00:00:00\",\"desc\":\"General Graardor\"}";
            TaskMaster board=p.getTaskMaster(); board.importLegacy(old); board.refresh(LocalDateTime.now());
            assertTrue(board.task(2).getClaimedReward()); assertEquals("General Graardor",board.task(2).getDesc());
            assertFalse(board.task(2).isWeeklyChallenge()); assertTrue(board.task(3).isWeeklyChallenge());
            assertEquals(2,board.task(2).getItems().length);
            String saved=board.encode(); TaskMaster loaded=new TaskMaster(p); loaded.decode(saved); loaded.importLegacy("[]");
            assertEquals(saved,loaded.encode());
        });
    }
    @Test void newPlayersDoNotReceiveHighLevelSkillingTasks() throws Exception {
        player(p -> {
            assertFalse(TaskMaster.meetsSkillRequirement(p,Tasks.MAGIC_TREES_2));
            assertFalse(TaskMaster.meetsSkillRequirement(p,Tasks.PRAYER_POT));
            assertTrue(TaskMaster.meetsSkillRequirement(p,Tasks.BURN_LOGS));
            p.playerXP[8]=200000000;
            assertTrue(TaskMaster.meetsSkillRequirement(p,Tasks.MAGIC_TREES_2));
        });
    }
}
