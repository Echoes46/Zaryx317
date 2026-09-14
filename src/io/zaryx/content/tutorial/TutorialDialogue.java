package io.zaryx.content.tutorial;

import io.zaryx.Configuration;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.content.items.Starter;
import io.zaryx.model.entity.player.*;
import io.zaryx.model.entity.player.mode.ExpMode;
import io.zaryx.model.entity.player.mode.ModeType;
import io.zaryx.model.entity.player.mode.group.ExpModeType;
import io.zaryx.model.entity.player.mode.group.GroupIronman;
import io.zaryx.model.entity.player.mode.wildygroup.GroupWildyMan;

import java.util.function.Consumer;

public class TutorialDialogue extends DialogueBuilder {

    public static final int TUTORIAL_NPC = 5525;
    private final boolean repeat;
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
    }

    public TutorialDialogue(Player player, boolean repeat, boolean tutorial) {
        super(player);

        this.repeat = repeat;
        setNpcId(TUTORIAL_NPC);
        if (tutorial) {
            for (HomeTour.Stop stop : HomeTour.STOPS) {
                npc(stop.position(), stop.text());
            }
        }
        npc(new Position(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y),
                "You're back at the centre of " + Configuration.SERVER_NAME + " home.",
                "Check the Activity Board for hourly, daily and weekly tasks.",
                "Join ::discord for news and help. Enjoy your adventure!");
        if (!repeat) {
            npc("Be sure to @blu@set an account pin with ::pin@bla@!", "@blu@You will gain one hour of bonus xp scrolls!",
                    "You only have to enter it when you login", "on a different computer.");
            npc("You have the option to play as one of our <col=" + Right.IRONMAN + "><img=12></img>Iron Man</col>," , " modes or one of our, <col=" + Right.GROUP_WILDYMAN
                    + "><img=94></img> WildyMan</col> modes that is wild only", "Choose from the following interface.");
            exit(p -> p.getModeSelection().openInterface());
        }
    }

    @Override
    public void end() {
        super.end();
        // Replaying the tour must not reopen account setup or leave movement locked.
        if (repeat) {
            setInTutorial(getPlayer(), false);
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
