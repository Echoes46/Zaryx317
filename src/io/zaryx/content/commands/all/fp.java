package io.zaryx.content.commands.all;

import com.google.common.collect.Lists;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

import java.util.List;
import java.util.stream.Collectors;


public class fp extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        c.getPA().spellTeleport(3363, 3294, 0, false);

        List<String> lines = Lists.newArrayList();
        lines.add("Welcome to Gambling on Zaryx.");
        lines.add("We would like you warn you that when you start gambling,");
        lines.add("THIS IS AT YOUR OWN RISK!");
        lines.add("Our staff here at Zaryx are not responsible,");
        lines.add("for you losing any items.");
        lines.add("Please be responsible when gambling,");
        lines.add("as we want you to have fun and enjoy your time here.");

        c.getPA().openQuestInterface("FlowerPoker Gambling", lines.stream().limit(149).collect(Collectors.toList()));
    }

}
