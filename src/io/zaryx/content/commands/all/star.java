package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.content.events.monsterhunt.ShootingStars;
import io.zaryx.content.wildwarning.WildWarning;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.Mode;
import io.zaryx.model.entity.player.mode.ModeType;

import java.util.Optional;

public class star extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.wildLevel > 0) {
            return;
        }
        if (!ShootingStars.progress) {
            player.sendMessage("There is no star active.");
            return;
        }

        if (!ShootingStars.ACTIVE.starSpawn.inWild() && player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || !ShootingStars.ACTIVE.starSpawn.inWild() && player.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN))) {
            player.sendMessage("You cannot teleport to a star outside the wilderness.");
            return;
        }

        if (player.jailEnd > 1) {
            player.forcedChat("I'm trying to teleport away!");
            player.sendMessage("You are still jailed!");
            return;
        }

        if (ShootingStars.ACTIVE.starSpawn.inWild()) {
           WildWarning.sendWildWarning(player, p -> {
               p.getPA().movePlayer(ShootingStars.ACTIVE.starSpawn.getX()-2,ShootingStars.ACTIVE.starSpawn.getY(),0);
           });
        } else {
            player.getPA().movePlayer(ShootingStars.ACTIVE.starSpawn.getX()-2,ShootingStars.ACTIVE.starSpawn.getY(),0);
        }

    }
    @Override
    public Optional<String> getDescription() {
        return Optional.of("Teleport's to you the Shooting Star.");
    }
}
