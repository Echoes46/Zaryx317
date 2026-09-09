package io.zaryx.model.entity.player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.zaryx.model.entity.player.save.PlayerSave;

public class PlayerSaveExecutor {

    private static final ExecutorService executor = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setNameFormat("player-save-%d").build());
    private final Player player;
    private Future<?> saveFuture;
    private PlayerSave.Snapshot snapshot;
    private long nextRetry;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PlayerSaveExecutor.class);

    public PlayerSaveExecutor(Player player) {
        this.player = player;
    }

    public void request() {
        Preconditions.checkState(saveFuture == null, "Already requested.");
        // Capture on the requesting (game) thread; the worker only writes immutable bytes.
        if (!player.saveCharacter) {
            saveFuture = java.util.concurrent.CompletableFuture.completedFuture(null);
            return;
        }
        snapshot = PlayerSave.captureSnapshot(player);
        submit();
    }

    private void submit() {
        saveFuture = executor.submit(() -> {
            if (!PlayerSave.writeSnapshot(snapshot)) {
                logger.error("Player save failed for {}", player.getLoginName());
                throw new IllegalStateException("Could not save player " + player.getLoginName());
            }
        });
    }

    public boolean finished() {
        if (saveFuture == null || !saveFuture.isDone()) return false;
        try {
            saveFuture.get();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (java.util.concurrent.ExecutionException e) {
            if (System.currentTimeMillis() >= nextRetry) {
                logger.error("Save failed; retaining {} in logout queue and retrying", player.getLoginName(), e.getCause());
                nextRetry = System.currentTimeMillis() + 5000;
                if (snapshot == null) snapshot = PlayerSave.captureSnapshot(player);
                submit();
            }
        }
        return false;
    }

    public Player getPlayer() {
        return player;
    }
}
