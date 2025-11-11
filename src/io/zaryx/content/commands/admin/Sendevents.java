package io.zaryx.content.commands.admin;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.content.events.monsterhunt.CrystalTree;
import io.zaryx.content.events.monsterhunt.ShootingStars;
import io.zaryx.content.skills.firemake.Burner;
import io.zaryx.content.wilderness.ActiveVolcano;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.DiscordBot;
import io.zaryx.util.discord.DiscordChannelType;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class Sendevents extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (DiscordBot.INSTANCE == null) {
            player.sendMessage("Discord bot is not connected!");
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("🌟 [ SKILLING EVENTS ] 🌟");
        embed.setColor(Color.YELLOW);
        embed.setTimestamp(java.time.Instant.now());
        embed.setImage("https://oldschool.runescape.wiki/images/thumb/Fireworks.gif/300px-Fireworks.gif?405a6");
        if (ShootingStars.progress) {
            embed.addField("☄️ Shooting Star", "Star has landed! Use `::star`\n**Location:** " + ShootingStars.getLocation(), false);
        } else {
            embed.addField("☄️ Shooting Star", "Inactive", false);
        }
        if (CrystalTree.progress) {
            embed.addField("🌳 Glistening Tree", "Tree has sprouted! Use `::tree`\n**Location:** " + CrystalTree.getLocation(), false);
        } else {
            embed.addField("🌳 Glistening Tree", "Inactive", false);
        }
        if (ActiveVolcano.progress) {
            embed.addField("🌋 Active Volcano", "Volcano is erupting! Use `::volcano` ", false);
        } else {
            embed.addField("🌋 Active Volcano", "Inactive", false);
        }
        if (Burner.totalLogsAdded > 5000) {
            embed.addField("🔥 Bonfire Event", "1.5x experience is active!", false);
        } else {
            embed.addField("🔥 Bonfire Event", "Logs added: **" + Burner.totalLogsAdded + "/5000**", false);
        }
        DiscordBot.INSTANCE.sendSkillingEvents(embed.build());
        player.sendMessage("Active events have been sent to Discord.");
    }
}