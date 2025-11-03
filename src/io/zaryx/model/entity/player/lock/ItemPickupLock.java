package io.zaryx.model.entity.player.lock;

import io.zaryx.model.entity.player.Player;

public class ItemPickupLock implements PlayerLock {
    @Override
    public boolean cannotLogout(Player player) {
        return true;
    }

    @Override
    public boolean cannotInteract(Player player) {
        return true;
    }

    @Override
    public boolean cannotClickItem(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean cannotTeleport(Player player) {
        return true;
    }
}
