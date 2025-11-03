package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;


public class scrap extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        c.getShops().openShop(600);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens scrap shop");
    }
}
