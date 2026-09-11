package io.zaryx.content.taskmaster;
import java.util.function.BooleanSupplier;

/** Runs on the game thread. Persist sees both the items and the claimed flag. */
public final class TaskClaim {
    public enum Result { CLAIMED, NOT_READY, NO_SPACE, SAVE_FAILED }
    private TaskClaim() { }
    public static Result claim(TaskMasterKills task,BooleanSupplier grant,Runnable rollback,BooleanSupplier persist) {
        if(task==null || !task.complete() || task.getClaimedReward()) return Result.NOT_READY;
        try {
            if(!grant.getAsBoolean()) { rollback.run(); return Result.NO_SPACE; }
            task.setClaimedReward(true);
            if(!persist.getAsBoolean()) { task.setClaimedReward(false); rollback.run(); return Result.SAVE_FAILED; }
            return Result.CLAIMED;
        } catch(RuntimeException e) {
            task.setClaimedReward(false); rollback.run(); throw e;
        }
    }
}
