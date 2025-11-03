package io.zaryx.util.logging.player;

import java.util.List;
import java.util.Set;

import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;
import io.zaryx.util.logging.PlayerLog;

public class DeathItemsLost extends PlayerLog {

    private final List<GameItem> lost;
    private final Position position;


    public DeathItemsLost(Player player, List<GameItem> lost) {
        super(player);
        this.lost = lost;
        this.position = player.getPosition();
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("unsafe_death", "item_lost");
    }

    @Override
    public String getLoggedMessage() {
        return Misc.replaceBracketsWithArguments("{} dropped {}", position, lost);
    }
}
