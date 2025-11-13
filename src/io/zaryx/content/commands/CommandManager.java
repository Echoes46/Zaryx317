package io.zaryx.content.commands;

import com.google.common.collect.Lists;
import io.zaryx.Server;
import io.zaryx.content.TriviaBot;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.content.battlepass.Pass;
import io.zaryx.content.bots.BasicBot;
import io.zaryx.content.commands.admin.dboss;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.content.tournaments.TourneyManager;
import io.zaryx.model.Graphic;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Right;
import io.zaryx.model.entity.player.mode.ExpMode;
import io.zaryx.model.entity.player.mode.group.ExpModeType;
import io.zaryx.model.items.bank.BankItem;
import io.zaryx.model.items.bank.BankTab;
import io.zaryx.util.Misc;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;

public class CommandManager {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CommandManager.class.getName());
    public static final Map<String, Command> COMMAND_MAP = new TreeMap<>();
    public static final List<CommandPackage> COMMAND_PACKAGES = Lists.newArrayList(new CommandPackage("admin", Right.ADMINISTRATOR), new CommandPackage("owner", Right.STAFF_MANAGER), new CommandPackage("owner", Right.GAME_DEVELOPER), new CommandPackage("moderator", Right.MODERATOR), new CommandPackage("helper", Right.HELPER), new CommandPackage("donator", Right.Donator), new CommandPackage("all", Right.PLAYER));

    private static boolean hasRightsRequirement(Player c, Right rightsRequired) {
        if (rightsRequired == Right.Donator && c.getRights().hasStaffPosition()) {
            return true;
        }
        return c.getRights().isOrInherits(rightsRequired);
    }

    public static void execute(Player c, String playerCommand) {
        for (CommandPackage commandPackage : COMMAND_PACKAGES) {
            if (hasRightsRequirement(c, commandPackage.getRight()) && executeCommand(c, playerCommand, commandPackage.getPackagePath())) {
                return;
            }
        }
    }

    public static CommandPackage getPackage(Command command) {
        for (CommandPackage commandPackage : COMMAND_PACKAGES) {
            if (command.getClass().getPackageName().contains(commandPackage.getPackagePath())) {
                return commandPackage;
            }
        }
        return null;
    }

    private static String getPackageName(String packagePath) {
        String[] split = packagePath.split("\\.");
        return split[split.length - 2];
    }

    public static List<Command> getCommands(Player player, String... skips) {
        return COMMAND_MAP.entrySet().stream().filter(entry -> {
            for (CommandPackage commandPackage : COMMAND_PACKAGES) {
                if (getPackageName(entry.getKey().toLowerCase()).contains(commandPackage.getPackagePath())) {
                    if (Arrays.stream(skips).anyMatch(skip -> commandPackage.getPackagePath().toLowerCase().contains(skip))) {
                        continue;
                    }
                    if (hasRightsRequirement(player, commandPackage.getRight())) {
                        return true;
                    }
                }
            }
            return false;
        }).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public static boolean executeCommand(Player c, String playerCommand, String commandPackage) {
        if (playerCommand == null) {
            return true;
        }
        String commandName = Misc.findCommand(playerCommand);
        String commandInput = Misc.findInput(playerCommand);
        String className;
        if (commandName.length() <= 0) {
            return true;
        } else if (commandName.length() == 1) {
            className = commandName.toUpperCase();
        } else {
            className = Character.toUpperCase(commandName.charAt(0)) + commandName.substring(1).toLowerCase();
        }

        if(c.isInTradingPost()) {
            c.setSidebarInterface(3, 3213);
            c.inTradingPost = false;
            c.clickDelay = System.currentTimeMillis();
        }

        boolean outlast = TourneyManager.getSingleton().isInArenaBounds(c) || TourneyManager.getSingleton().isInLobbyBounds(c);

        if (outlast && c.getRights().isNot(Right.ADMINISTRATOR)) {
            c.sendMessage("You cannot use commands when in the tournament arena");
            return true;
        }


        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("discordbot")) {
//            Discord.init();
            c.sendMessage("Discord bot initialized!");
            return true;
        }

        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("changexp")) {
            String[] args = commandInput.split("-");
            try {
                String playerName = args[0];

                ExpMode expMode = null;
                if (args[1].equalsIgnoreCase("1")) {
                    expMode = new ExpMode(ExpModeType.OneTimes);
                } else if (args[1].equalsIgnoreCase("5")) {
                    expMode = new ExpMode(ExpModeType.FiveTimes);
                } else if (args[1].equalsIgnoreCase("10")) {
                    expMode = new ExpMode(ExpModeType.TenTimes);
                } else if (args[1].equalsIgnoreCase("25")) {
                    expMode = new ExpMode(ExpModeType.TwentyFiveTimes);
                }

                Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

                if (optionalPlayer.isPresent()) {
                    Player c2 = optionalPlayer.get();

                    c2.setExpMode(expMode);
                    c2.sendErrorMessage("Your expMode has been changed to " + expMode.getType().getFormattedName());
                    c.sendMessage("You have changed " + c2.getDisplayName() + "'s expMode to " +expMode.getType().getFormattedName());
                }
            } catch (Exception e) {
                c.sendErrorMessage("Format invalid ::changexp-name-(1,5,10,25)");
            }
            return true;
        }

        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("wealthcheck")) {
            List<String> lines = Lists.newArrayList();
            long coins = 0;
            long nomad = 0;
            long plat = 0;

            for (Player player : PlayerHandler.getPlayers()) {
                if (player != null && !player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
                    coins += player.getItems().getInventoryCount(995);
                    plat += player.getItems().getInventoryCount(13204);
                    nomad += player.getItems().getInventoryCount(33237);
                    nomad += (player.getItems().getInventoryCount(691) * 10_000L);
                    nomad += (player.getItems().getInventoryCount(692) * 25_000L);
                    nomad += (player.getItems().getInventoryCount(693) * 50_000L);
                    nomad += (player.getItems().getInventoryCount(696) * 250_000L);

                    for (BankTab bankTab : player.getBank().getBankTab()) {
                        for (BankItem item : bankTab.getItems()) {
                            if (item.getId() == 995) {
                                coins += item.getAmount();
                            }
                            if (item.getId() == 13204) {
                                plat += item.getAmount();
                            }
                            if (item.getId() == 33237) {
                                nomad += (item.getAmount());
                            }
                            if (item.getId() == 691) {
                                nomad += (item.getAmount() * 10_000L);
                            }

                            if (item.getId() == 692) {
                                nomad += (item.getAmount() * 25_000L);
                            }

                            if (item.getId() == 693) {
                                nomad += (item.getAmount() * 50_000L);
                            }

                            if (item.getId() == 696) {
                                nomad += (item.getAmount() * 250_000L);
                            }
                        }
                    }
                    nomad += player.foundryPoints;

                    lines.add("User: " +player.getDisplayName() + "coins: " + Misc.formatCoins(coins) +", plat: " + Misc.formatCoins(plat) + ", nomad: " + Misc.formatCoins(nomad));
                }
            }

            c.getPA().openQuestInterface("Wealth checker", lines.stream().limit(149).collect(Collectors.toList()));
            return true;
        }

        if (commandName.equalsIgnoreCase("titles")) {
            c.getTitles().display();
            return true;
        }

        if (commandName.equalsIgnoreCase("debugme") && c.getRights().isOrInherits(Right.GAME_DEVELOPER)) {
            if (c.debugMessage) {
                c.debugMessage = false;
                c.sendMessage("Debug Messages Disabled.");
            } else {
                c.debugMessage = true;
                c.sendMessage("Debug Messages Enabled.");
            }
            return true;
        }

        if (playerCommand.startsWith("answer")) {
                String triviaAnswer = playerCommand.substring(7);//7
                if (TriviaBot.acceptingQuestion()) {
                    TriviaBot.attemptAnswer(c, triviaAnswer);
                    return true;
                } else {
                    c.sendMessage("No Question has been asked yet!");
                }
            }



//        if (playerCommand.startsWith("freerank")) {
//            c.freeranks = true;
//            return true;
//            }


        if (commandName.equalsIgnoreCase("sms")) {
            c.getPA().showInterface(24960);
            c.sendErrorMessage("We only accept US Phone Numbers!");
            c.sendErrorMessage("All phone numbers but start with +1!");
            c.sendErrorMessage("Phone Number Example: 123-456-7890 (+11234567890)");
            return true;
        }

        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("resetWeeklyDono")) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

            if (optionalPlayer.isPresent()) {
                Player c2 = optionalPlayer.get();

                c2.setWeeklyDonated(0);
                c2.sendErrorMessage(c.getDisplayName() + " has just reset your weekly donations, make sure to relog!");
                c.sendErrorMessage("You have just reset " + c2.getDisplayName() + "'s weekly donations!");
            }
            return true;
        }

        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("giveweeklydono")) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

            if (optionalPlayer.isPresent()) {
                Player c2 = optionalPlayer.get();

                c2.getDonationRewards().increaseDonationAmount(50);
                c2.sendErrorMessage(c.getDisplayName() + " has just increased your weekly donations!");
                c.sendErrorMessage("You have just increased " + c2.getDisplayName() + "'s weekly donations!");
            }
            return true;
        }
        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("setmax")) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

            if (optionalPlayer.isPresent()) {
                Player c2 = optionalPlayer.get();

                for (int i = 0; i < 24; i++) {
                    c2.playerLevel[i] = 99;
                    c2.playerXP[i] = c2.getPA().getXPForLevel(99) + 1;
                    c2.getPA().refreshSkill(i);
                    c2.getPA().setSkillLevel(i, c2.playerLevel[i], c2.playerXP[i]);
                    c2.getPA().levelUp(i);
                }
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("daalkor")) {
            c.getPA().sendURL("https://www.youtube.com/@daalkor");
            return true;
        }

        if (commandName.equalsIgnoreCase("donocred")) {
            c.start(new DialogueBuilder(c).option("Would you like to convert your donor credits to points?", new DialogueOption("Yes", p -> {
                int amt = p.getItems().getInventoryCount(33251);
                if (amt <= 0) {
                    p.getPA().closeAllWindows();
                    p.sendErrorMessage("You don't have any donor credits!");
                    return;
                }
                p.getPA().closeAllWindows();
                p.getPA().sendEnterAmount("How many would you like to convert?", (plr, amount) -> {
                    int total_am = p.getItems().getInventoryCount(33251);

                    if (amount > total_am) {
                        amount = total_am;
                    }

                    plr.donatorPoints += amount;
                    plr.getItems().deleteItem2(33251, amount);
                });

            }), new DialogueOption("No thank you.", p->p.getPA().closeAllWindows())));
            return true;
        }



        if (commandName.equalsIgnoreCase("achievescan") && c.getRights().isOrInherits(Right.MODERATOR)) {
            List<String> lines = Lists.newArrayList();
            PlayerHandler.getPlayers().stream().filter(Objects::nonNull).forEach(it -> {
                int counter = 0;
                for (Achievements.Achievement value : Achievements.Achievement.values()) {
                    if (it.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                        counter++;
                    }
                }
                lines.add(it.getDisplayName() + " completed achievements : " + counter+ " / " + Achievements.Achievement.values().length);
            });
            c.getPA().openQuestInterface("Accounts completed achievements", lines.stream().limit(149).collect(Collectors.toList()));
            return true;
        }

        if (commandName.equalsIgnoreCase("bp")) {
            Pass.openInterface(c);
            c.getQuesting().handleHelpTabActionButton(669);
            return true;
        }

        if (commandName.equalsIgnoreCase("fuckit") && c.getRights().isOrInherits(Right.GAME_DEVELOPER)) {
            BasicBot.startBots();
            return true;
        }

        if (commandName.equalsIgnoreCase("donoboss") && c.getRights().isOrInherits(Right.MODERATOR)) {
            dboss.spawnBoss();
            return true;
        }

        if (commandName.equalsIgnoreCase("perk")) {
            c.getPA().startTeleport(3363, 9640, 0,"modern", false);
            return true;
        }

        try {
            String path = "io.zaryx.content.commands." + commandPackage + "." + className;
            if (COMMAND_MAP.get(path.toLowerCase()) != null) {
                COMMAND_MAP.get(path.toLowerCase()).execute(c, commandName, commandInput);
                return true;
            }
            return false;
        } catch (Exception e) {
            c.sendMessage("Error while executing the following command: " + playerCommand);
            e.printStackTrace();
            return true;
        }
    }

    private static void initialize(String path) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> commandClass = Class.forName(path);
        Object instance = commandClass.newInstance();
        if (instance instanceof Command) {
            Command command = (Command) instance;
            COMMAND_MAP.putIfAbsent(path.toLowerCase(), command);
            log.fine(String.format("Added command [path=%s] [command=%s]", path, command.toString()));
        }
    }

    public static void initializeCommands() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        if (Server.isDebug() || Server.isTest()) { // Important that this doesn't get removed
            COMMAND_PACKAGES.add(new CommandPackage("test", Right.PLAYER));
        }
        Reflections reflections = new Reflections("io.zaryx.content.commands");
        for (Class<? extends Command> clazz : reflections.getSubTypesOf(Command.class)) {
            initialize(clazz.getName());
        }
        log.info("Loaded " + COMMAND_MAP.size() + " commands.");
    }
}
