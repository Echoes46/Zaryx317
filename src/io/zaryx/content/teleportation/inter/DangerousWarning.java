package io.zaryx.content.teleportation.inter;

import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;

public class DangerousWarning {

    public static void show(Player player, Position pos) {
        if (!player.getPosition().inWild()) {
            player.start(new DialogueBuilder(player).statement("@red@This teleport is DANGEROUS, are you sure?")
                    .option(
                            new DialogueOption("Yes, teleport to the dangerous area.", plr -> handleTeleport(plr, pos, false)),
                            new DialogueOption("No, stay here.", plr -> player.getPA().closeAllWindows())
                    )
            );
        } else {
            handleTeleport(player, pos, false);
        }
    }

    public static void handleTeleport(Player player, Position position, boolean warning) {
        if (warning) {
            show(player, position);
            return;
        }

        player.getPA().closeAllWindows();
        player.getAttributes().remove("dangerous_tele");
        player.getPA().startTeleport(position.getX(), position.getY(), position.getHeight(), "modern", false);
    }
}
