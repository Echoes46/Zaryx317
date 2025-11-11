package io.zaryx.content.tutorial;

import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.content.items.Starter;
import io.zaryx.model.entity.player.*;
import io.zaryx.model.entity.player.mode.ExpMode;
import io.zaryx.model.entity.player.mode.ModeType;
import io.zaryx.model.entity.player.mode.group.ExpModeType;
import io.zaryx.model.entity.player.mode.group.GroupIronman;
import io.zaryx.model.entity.player.mode.wildygroup.GroupWildyMan;
import io.zaryx.util.discord.DiscordBot;
import io.zaryx.util.discord.DiscordChannelType;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.function.Consumer;

public class TutorialDialogue extends DialogueBuilder {

    public static final int TUTORIAL_NPC = 5525;
    private static final String IN_TUTORIAL_KEY = "in_tutorial";
    private static final DialogueOption[] XP_RATES = {
            new DialogueOption("25x Combat / 15x Skilling", p -> chosenXpRate(p, ExpModeType.TwentyFiveTimes)),
            new DialogueOption("10x Combat / 10x Skilling", p -> chosenXpRate(p, ExpModeType.TenTimes)),
            new DialogueOption("5x Combat / 5x Skilling (+7% dr)", p -> chosenXpRate(p, ExpModeType.FiveTimes)),
            new DialogueOption("1x Combat / 1x Skilling (+10% dr)", p -> chosenXpRate(p, ExpModeType.OneTimes))
    };

    public static boolean inTutorial(Player player) {
        return player.getAttributes().getBoolean(IN_TUTORIAL_KEY);
    }

    private static void setInTutorial(Player player, boolean inTutorial) {
        player.getAttributes().setBoolean(IN_TUTORIAL_KEY, inTutorial);
        if (inTutorial) {
            player.setMovementState(new PlayerMovementStateBuilder().setLocked(true).createPlayerMovementState());
        } else {
            player.setMovementState(PlayerMovementState.getDefault());
        }
    }

    public static void selectedMode(Player player, ModeType mode) {
        Consumer<Player> chooseExpRate = p -> chooseExperienceRate(player);

        player.start(new DialogueBuilder(player)
                .setNpcId(TUTORIAL_NPC)
                .npc("You've chosen " + mode.getFormattedName() + ", sound right?")
                .option(new DialogueOption("Yes, play " +mode.getFormattedName() + " mode.", chooseExpRate),
                        new DialogueOption("No, pick another game mode.", p -> p.getModeSelection().openInterface()))
        );
    }

    private static void chosenXpRate(Player player, ExpModeType mode) {
        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("You've chosen the " + mode.getFormattedName() + " experience rate.", "Sound right?")
                .option(new DialogueOption("Yes, use " + mode.getFormattedName() + " experience rate.", p -> finish(p, mode)),
                        new DialogueOption("No.", TutorialDialogue::chooseExperienceRate)));
    }

    private static void chooseExperienceRate(Player player) {
        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("Select which experience type you want to use.").option(XP_RATES));
    }

    public static void finish(Player player, ExpModeType modeType) {
        switch (modeType) {
            case TwentyFiveTimes:
                player.setExpMode(new ExpMode(ExpModeType.TwentyFiveTimes));
                break;
            case TenTimes:
                player.setExpMode(new ExpMode(ExpModeType.TenTimes));
                break;
            case FiveTimes:
                player.setExpMode(new ExpMode(ExpModeType.FiveTimes));
                break;
            case OneTimes:
                player.setExpMode(new ExpMode(ExpModeType.OneTimes));
                break;
        }

        player.getPA().requestUpdates();
        setInTutorial(player, false);
        Starter.addStarter(player);
        player.setCompletedTutorial(true);

        if (player.getRights().contains(Right.GROUP_IRONMAN)) {
            GroupIronman.moveToFormingLocation(player);
            return;
        }

        if (player.getRights().contains(Right.GROUP_WILDYMAN)) {
            GroupWildyMan.moveToFormingLocation(player);
            return;
        }

        if (player.getRights().contains(Right.WILDYMAN)) {
            player.moveTo(new Position(3446,3837,0));
        }

        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("Enjoy your stay on " + Configuration.SERVER_NAME + "!"));
        PlayerHandler.executeGlobalMessage("[@blu@New Player@bla@] " + player.getDisplayNameFormatted() + " @bla@has logged in! Welcome!");
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(" NEW PLAYER WELCOME ");
            embed.setColor(Color.GREEN);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField("Player: ", player.getDisplayName() + " has has logged in! Welcome!", false);
            DiscordBot.INSTANCE.writePlayersOnline(embed.build());
        }
    }

    public TutorialDialogue(Player player, boolean repeat, boolean tutorial) {
        super(player);

        setNpcId(TUTORIAL_NPC);
        if (!Server.isTest() && tutorial) {
            npc(new Position(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y), "Welcome to " + Configuration.SERVER_NAME + "!", "Here is our home area!", "Don't forgot to join our ::discord.");
            npc(new Position(2083, 5987), "Here you can find all the shops needed", "when you first start out! You can buy combat gear,", "foods and pots, or show off your fashion skills!");
            npc(new Position(2104, 6003), "Receive your daily login rewards here, by speaking to Aretha!");
            npc(new Position(2063, 5991), "Here is the vote chest.", "After voting for all sites 10 times you get a @blu@vote key@bla@!", "Check out '@red@::chestrewards@bla@' to see what you can get!");
            npc(new Position(2069, 5991), "Here is the corrupt chest.", "There are 2 Wildy bosses that can be killed for keys!", "Check out '@red@::chestrewards@bla@' to see what you can get!");
            npc(new Position(2066, 5993), "Here is the crystal chest.", "Here you can use your crystal keys for loot!", "Check out '@red@::chestrewards@bla@' to see what you can get!");
            npc(new Position(2063, 5984), "Here is the Brimstone chest.", "This is where you can use your brimstone keys!!", "Check out '@red@::chestrewards@bla@' to see what you can get!");
            npc(new Position(2066, 5982), "Here is the Hespori chest.", "This is where you can use your hespori keys!!", "Check out '@red@::chestrewards@bla@' to see what you can get!");
            npc(new Position(2064, 6004), "This is the Outlast Portal.", "Anybody can join, any level or game mode!", "Use the Quest Tab to see when the next", "event will happen!");
            npc(new Position(2034, 5988), "This is where you can plant seeds after defeating", "the world boss Hespori, which is displayed in your quest tab.");
            npc(new Position(2062, 5964), "Here you can get Slayer tasks and spend points on rewards!");
            npc(new Position(2087, 6009), "If you decide to be a restricted game mode", "you can use the shops here!", "Including a UIM Storage chest!");
            npc(new Position(2105, 6005), "This is the banking area", "you can also access the vote shop!", "as well as the altars!");
            npc(new Position(2047, 5964), "Here we have the Upgrade Table,", "Nomad is able to,", "Dissolve items for points.");
            npc(new Position(2056, 5984), "Speaking with the Mage of Zamorak", "You can teleport to the abyss", "or even the essence mines!");
            npc(new Position(2093, 5999), "This is the Discord Integration", "Here you can sync your player with your", "Discord account!");
            npc(new Position(2045, 6005), "Change your character style here!", "And don't forget you can use the teleporter", "at home to open the Teleport Menu!");
            npc(new Position(2096, 6004), "We Hope you enjoy your stay at Zaryx!", "Also Don't forget to use", "::vote for a reward!");

        }
        if (!repeat) {
            npc("Be sure to @blu@set an account pin with ::pin@bla@!", "@blu@You will gain one hour of bonus xp scrolls!",
                    "You only have to enter it when you login", "on a different computer.");
            npc("You have the option to play as one of our <col=" + Right.IRONMAN + "><img=12></img>Iron Man</col>," , " modes or one of our, <col=" + Right.GROUP_WILDYMAN
                            + "><img=94></img> WildyMan</col> modes that is wild only", "Choose from the following interface.");
            exit(p -> p.getModeSelection().openInterface());
        }
    }

    @Override
    public void initialise() {
        setInTutorial(getPlayer(), true);
        super.initialise();
    }

    private void npc(Position teleport, String...text) {
        npc(text).action(player -> player.moveTo(teleport));
    }
}
