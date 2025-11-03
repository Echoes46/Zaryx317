package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.net.login.LoginRequestLimit;

public class GetLoginLimit extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
       player.sendMessage("Login rate limit is set to {}", "" + LoginRequestLimit.MAX_LOGINS_PER_TICK);
    }
}
