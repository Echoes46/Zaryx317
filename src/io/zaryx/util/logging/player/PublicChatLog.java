package io.zaryx.util.logging.player;

import java.util.Set;

import io.zaryx.model.entity.player.Player;
import io.zaryx.util.logging.PlayerLog;

public class PublicChatLog extends PlayerLog {

    private final String message;

    public PublicChatLog(Player player, String message) {
        super(player);
        this.message = message;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("chat_public", "chat_all");
    }

    @Override
    public String getLoggedMessage() {
        return message;
    }
}
