package io.zaryx.util.discord.impl;

import io.zaryx.content.boosts.BoostType;
import io.zaryx.content.boosts.Booster;
import io.zaryx.content.boosts.Boosts;
import io.zaryx.content.events.monsterhunt.CrystalTree;
import io.zaryx.content.events.monsterhunt.ShootingStars;
import io.zaryx.content.worldevent.WorldEventContainer;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * /currentevents
 * Shows the status of ongoing in-game events.
 */
public class CurrentEvents implements SlashHandler {

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!"currentevents".equals(event.getName())) return;

        EmbedBuilder eventEmbed = new EmbedBuilder();
        eventEmbed.setTitle("Current In-Game Events");
        eventEmbed.setColor(new Color(0x4F86FF));

        // Koranian Event Boss
//        addEventInfo(eventEmbed, "Koranian Event Boss",
//                KoranianEventBossHandler.getCurrentLocation(), "Rogue's Castle (52)");

        // Event Boss (Glod)
//        addEventInfo(eventEmbed, "Event Boss",
//                EventBossHandler.getCurrentLocation(), "has spawned ::Glod");

        // Galvek Event Boss
//        addEventInfo(eventEmbed, "Galvek Event Boss",
//                GalvekEventBossHandler.getCurrentLocation(), "has spawned ::Galvek");

        // Shooting Star
        addEventInfo(eventEmbed, "Shooting Star",
                ShootingStars.getLocation(), null); // shows "Spawned at <location>" if non-null

        // Crystal Tree
        addEventInfo(eventEmbed, "Crystal Tree",
                CrystalTree.getLocation(), null); // shows "Spawned at <location>" if non-null

        // World Events (container-driven)
        try {
            List<String> statuses = WorldEventContainer.getInstance().getWorldEventStatuses();
            if (statuses == null || statuses.isEmpty()) {
                eventEmbed.addField("World Events", "No world events scheduled.", false);
            } else {
                String body = statuses.stream()
                        .map(this::stripColorTags)
                        .collect(Collectors.joining("\n"));
                if (body.length() > 3800) body = body.substring(0, 3800) + "\n…";
                eventEmbed.addField("World Events", "```" + body + "```", false);
            }
        } catch (Throwable t) {
            eventEmbed.addField("World Events", "Status unavailable.", false);
        }

        // Revenant Event Boss
//        addEventInfo(eventEmbed, "Revenant Event Boss",
//                RevenantEventBossHandler.getCurrentLocation(), "Rev Caves (41)");

        // (Optional) Boosts section — uncomment if you want them shown
        // addBoostsInformation(eventEmbed);

        event.replyEmbeds(eventEmbed.build()).setEphemeral(true).queue();
    }

    /**
     * Adds a field describing an event's status.
     * - If currentLocation == null ⇒ "Not spawned"
     * - If spawnedMessage != null ⇒ use that text
     * - Else if currentLocation is a String ⇒ "Spawned at <location>"
     * - Else ⇒ "Spawned"
     */
    private void addEventInfo(EmbedBuilder eb, String name, Object currentLocation, String spawnedMessage) {
        if (currentLocation == null) {
            eb.addField(name, "Not spawned", true);
            return;
        }
        String text;
        if (spawnedMessage != null) {
            text = spawnedMessage;
        } else if (currentLocation instanceof String) {
            String loc = ((String) currentLocation).isBlank() ? "Unknown" : (String) currentLocation;
            text = "Spawned at " + loc;
        } else {
            text = "Spawned";
        }
        eb.addField(name, text, true);
    }

    @SuppressWarnings("unused")
    private void addBoostsInformation(EmbedBuilder eb) {
        List<? extends Booster<?>> expBoosts = Boosts.getBoostsOfType(null, null, BoostType.EXPERIENCE);
        for (Booster<?> boost : expBoosts) {
            eb.addField("Experience Boost", boost.getDescription(), true);
        }

        List<? extends Booster<?>> genericBoosts = Boosts.getBoostsOfType(null, null, BoostType.GENERIC);
        for (Booster<?> boost : genericBoosts) {
            eb.addField("Generic Boost", boost.getDescription(), true);
        }
    }

    private String stripColorTags(String s) {
        if (s == null) return "Unknown";
        // Clean up in-game color tags for Discord
        return s.replace("@gre@", "")
                .replace("@red@", "")
                .replace("<col=", "")
                .replace(">", "");
    }
}
