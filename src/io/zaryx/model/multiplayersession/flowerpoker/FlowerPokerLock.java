package io.zaryx.model.multiplayersession.flowerpoker;

import io.zaryx.model.Items;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.lock.CompleteLock;

public class FlowerPokerLock extends CompleteLock {
    @Override
    public boolean cannotClickItem(Player player, int itemId) {
        if (itemId == Items.MITHRIL_SEEDS)
            return false;
        return true;
    }
}
