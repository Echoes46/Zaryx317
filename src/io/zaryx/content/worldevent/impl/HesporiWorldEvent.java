package io.zaryx.content.worldevent.impl;

import java.awt.*;
import java.util.List;

import io.zaryx.content.bosses.hespori.Hespori;
import io.zaryx.content.bosses.hespori.HesporiSpawner;
import io.zaryx.content.commands.Command;
import io.zaryx.content.commands.all.Worldevent;
import io.zaryx.content.worldevent.WorldEvent;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.entity.player.broadcasts.Broadcast;
import io.zaryx.util.discord.DiscordBot;
import io.zaryx.util.discord.DiscordChannelType;
import net.dv8tion.jda.api.EmbedBuilder;

public class HesporiWorldEvent implements WorldEvent {
    @Override
    public void init() {
        HesporiSpawner.spawnNPC();
    }

    @Override
    public void dispose() {
        if (!isEventCompleted()) {
            Hespori.rewardPlayers(false);
        }
    }

    @Override
    public boolean isEventCompleted() {
        return !HesporiSpawner.isSpawned();
    }

    @Override
    public String getCurrentStatus() {
        return "World Event: @gre@Hespori";
    }

    @Override
    public String getEventName() {
        return "Hespori";
    }

    @Override
    public String getStartDescription() {
        return "spawns";
    }

    @Override
    public Class<? extends Command> getTeleportCommand() {
        return Worldevent.class;
    }

    @Override
    public void announce(List<Player> players) {
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("[ WORLD EVENT: HESPORI ]");
            embed.setImage("https://oldschool.runescape.wiki/images/thumb/Hespori.png/150px-Hespori.png?83c72");
            embed.setColor(Color.GREEN);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField("Hespori world boss has spawned, use ::worldevent to fight!", "\u200B", false);
            DiscordBot.INSTANCE.sendWorldEvent(embed.build());
            //DiscordBot.INSTANCE.sendMessage(DiscordChannelType.WORLD_EVENTS, "@everyone");
        }
        new Broadcast("<img=11> Hespori world boss has spawned, use ::worldevent to fight!").addTeleport(new Position(2457, 3553, 0)).copyMessageToChatbox().submit();
    }
}
