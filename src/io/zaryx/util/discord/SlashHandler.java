package io.zaryx.util.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashHandler {
    void handle(SlashCommandInteractionEvent e) throws Exception;
}
