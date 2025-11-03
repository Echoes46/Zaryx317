package io.zaryx.content.commands.all;
import io.zaryx.content.commands.Command;
import io.zaryx.content.skills.summoning.Summoning;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

public class Summon extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");
        if (args.length != 1) {
            throw new IllegalArgumentException();
        }
        int npcId = Integer.parseInt(args[0]);
        Summoning.summonFamiliar(player, npcId);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Summon a familiar by NPC ID");
    }
}
