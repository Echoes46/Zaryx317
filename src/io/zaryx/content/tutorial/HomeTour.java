package io.zaryx.content.tutorial;

import io.zaryx.Configuration;
import io.zaryx.model.entity.player.Position;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Viewing tiles beside the current home services, from home_area.json/global_objects.cfg. */
final class HomeTour {
    private HomeTour() { }

    static final class Stop {
        final String name;
        final int x, y;
        private final String[] text;
        Stop(String name, int x, int y, String... text) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.text = text.clone();
        }
        Position position() { return new Position(x, y, 0); }
        String[] text() { return text.clone(); }
    }

    static final List<Stop> STOPS = Collections.unmodifiableList(Arrays.asList(
        new Stop("Welcome", Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y,
            "Welcome to " + Configuration.SERVER_NAME + "!", "Let's take a quick tour of our home services.",
            "Click Continue to visit each area."),
        new Stop("Daily rewards", 3092, 3510,
            "Daily rewards are available from the NPC beside us.",
            "Check your rewards regularly as you play."),
        new Stop("Companions", 3106, 3515,
            "The Pet Collector is here at the north side of home.",
            "Use ::pet to browse companions, perks and levels.",
            "Each companion type levels separately on your account."),
        new Stop("Reward chests", 3120, 3516,
            "This is the reward chest area.",
            "Bring the matching keys to open their chests.",
            "Use ::chestrewards to browse chest rewards."),
        new Stop("Shops", 3107, 3504,
            "Visit the shopkeepers here to browse supplies and gear.",
            "Other nearby traders offer points and specialist shops.",
            "Some shops depend on your account's game mode."),
        new Stop("Slayer", 3104, 3493,
            "The Slayer masters are along this side of home.",
            "Choose a suitable master to receive your first task.",
            "Complete tasks to earn Slayer experience and points."),
        new Stop("Skilling and deposits", 3122, 3494,
            "The furnace and anvils are in this skilling area.",
            "The nearby bank deposit box lets you store items.",
            "Bring your materials when you are ready to train."),
        new Stop("Prayer altar", 3108, 3492,
            "Use the prayer altar here to restore Prayer points.",
            "Visit before heading back into combat."),
        new Stop("Equipment upgrades", 3092, 3483,
            "Use the upgrade machine beside us to view recipes.",
            "Check the required items, cost and success chance",
            "before attempting an upgrade."),
        new Stop("Spellbooks", 3086, 3483,
            "The occult altar lets you change your spellbook.",
            "Choose the spells you need before your next trip."),
        new Stop("Restoration pool", 3085, 3492,
            "Use this pool to restore your combat resources.",
            "It restores health, Prayer, run energy and special.",
            "The pool has a short cooldown between uses."),
        new Stop("Trading post", 3089, 3502,
            "The traders around this area run the Trading Post.",
            "Use it to buy and sell items with other players.",
            "Restricted game modes may not use player trading."),
        new Stop("Tournaments", 3077, 3488,
            "This portal is the entrance for Outlast tournaments.",
            "Watch the event announcements for the next round.",
            "Join when registration is open."),
        new Stop("Perk Paradise", 3101, 3488,
            "Use the nearby portal to enter Perk Paradise.",
            "Defeat avatars there for a chance at perk relics.",
            "Use a relic in your inventory to attune its perk.")
    ));
}
