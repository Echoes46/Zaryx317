package io.zaryx.content;

import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.activityboss.Groot;
import io.zaryx.content.boosts.BoostType;
import io.zaryx.content.boosts.Booster;
import io.zaryx.content.boosts.Boosts;
import io.zaryx.content.collection_log.CollectionLog;
import io.zaryx.content.combat.stats.MonsterKillLog;
import io.zaryx.content.commands.all.Voted;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.content.events.monsterhunt.CrystalTree;
import io.zaryx.content.events.monsterhunt.ShootingStars;
import io.zaryx.content.item.lootable.LootableInterface;
import io.zaryx.content.seasons.Christmas;
import io.zaryx.content.seasons.ChristmasBoss;
import io.zaryx.content.seasons.Halloween;
import io.zaryx.content.wilderness.ActiveVolcano;
import io.zaryx.content.worldevent.WorldEventContainer;
import io.zaryx.content.worldevent.WorldEventInformation;
import io.zaryx.model.entity.player.*;
import io.zaryx.util.Misc;
import io.zaryx.content.wogw.Wogw;
import io.zaryx.util.discord.DiscordBot;
import io.zaryx.util.discord.DiscordChannelType;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.zaryx.content.skills.firemake.Burner.totalLogsAdded;
import static io.zaryx.content.wogw.Wogw.*;

public class QuestTab {

    public enum Tab {
        INFORMATION(50_417),
        COIN(50_419),
        DIARY(50_421),
        DONATOR(50_423)
        ;

        private final int buttonId;

        Tab(int buttonId) {
            this.buttonId = buttonId;
        }

        public int getConfigValue() {
            return ordinal();
        }
    }

    public enum CoinTab {
        COLLECTION_LOG,
        MONSTER_KILL_LOG,
        DROP_TABLE,
        LOOT_TABLES,
        WORLD_EVENTS,
        PRESETS,
        DONATOR_BENEFITS,
        TITLES,
        COMMUNITY_GUIDES,
        VOTE_PAGE,
        ONLINE_STORE,
        FORUMS,
        RULES,
        CALL_FOR_HELP
    }

    private static final int[] COIN_TAB_BUTTONS = {74107, 74112, 74117, 74122, 74127, 74132, 74137, 74142, 74147, 74152, 74157, 74162, 74167, 74172};

    public static final int INTERFACE_ID = 50414;
    private static final int CONFIG_ID = 1355;

    public static void updateAllQuestTabs() {
        Arrays.stream(PlayerHandler.players).forEach(player -> {
            if (player != null) {
                player.getQuestTab().updateInformationTab();
            }
        });
    }

    private final Player player;

    public QuestTab(Player player) {
        this.player = player;
    }

    public void openTab(Tab tab) {
        player.getPA().sendConfig(CONFIG_ID, tab.getConfigValue());
    }

    public boolean handleActionButton(int buttonId) {
        for (Tab tab : Tab.values()) {
            if (buttonId == tab.buttonId) {
                openTab(tab);
                return true;
            }
        }

        return false;
    }

    public List<Integer> getLines() {
        return Arrays.asList(10301,10302,10303,10304,10305,10306,10307,10308,10309,10310,
                10311,10312,10313,10314,10315,10316,10317,10318,10319,10320,
                10321,10322,10323,10324,10325,10326,10327,10328,10329,10330,
                10331,10332,10333,10334,10335,10336,10337,10338,10339,10340,
                10341,10342,10343,10344,10345,10346,10347,10348,10349,10350,
                10351,10352,10353,10354,10355,10356,10357,10358,10359,10360);
//        return IntStream.range(10292, 10410).boxed().collect(Collectors.toList());
    }

    public static long delay = 0;

    public static void Tick() {
        if (delay < System.currentTimeMillis()) {
        PlayerHandler.nonNullStream().forEach(player -> player.getQuestTab().updateInformationTab());
        delay = (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));
        }
    }

    /**
     * Testiong View
     */
    public void updateInformationTab() {
        List<Integer> lines = getLines();
        int index = 0;

        // Server Information
        player.getPA().sendFrame126(" <img=10>@or1@ Server Information", lines.get(index++));

//        if (player.getRights().contains(Right.GAME_DEVELOPER)) {
//            player.getPA().sendFrame126("@or1@- Players: @gre@" + ((int) (PlayerHandler.getPlayerCount() * 1.3)) + " (u" + PlayerHandler.getUniquePlayerCount() + ") (m" + Configuration.PLAYERMODIFIER + ")", lines.get(index++));
//        } else {
        player.getPA().sendFrame126("@or1@ - @whi@Players online: @gre@" + ((int) (PlayerHandler.getPlayerCount() * 1.3)), lines.get(index++));
//        }

        if (Halloween.isHalloween() && Halloween.BoostActive) {
            player.getPA().sendFrame126("@whi@ - @red@Cauldron: @gre@" + Halloween.getActiveBoost(), lines.get(index++));
        }
        if (Halloween.isHalloween() && !Halloween.BoostActive) {
            player.getPA().sendFrame126("@or1@- @red@Cauldron Candies Left:" + (Halloween.pulseBoost > 5_000_000 ? "1" : (Misc.formatCoins(5_000_000 - Halloween.pulseBoost))), lines.get(index++));
        }

        if (Christmas.isChristmas()) {
            player.getPA().sendFrame126("@or1@- @red@Xmas: @gre@" + Halloween.getActiveBoost(), lines.get(index++));
            player.getPA().sendFrame126("@or1@- Snowman Spawns in: @gre@" + ChristmasBoss.KillCount, lines.get(index++));
        }

        // player.getPA().sendFrame126("@or1@- AFK Goblin spawns in: @gre@" + (AfkBoss.GoblinActive ? "ACTIVE" : AfkBoss.getGoblinSpawnAmount()), lines.get(index++));

        player.getPA().sendFrame126("@or1@ - @whi@Vote Boss Counter: @gre@" + (20 - Voted.totalVotes), lines.get(index++));

        player.getPA().sendFrame126("@or1@ - @whi@Durial Counter: @gre@" + (50 - Voted.durialVotes), lines.get(index++));

        if (Groot.alive) {
            player.getPA().sendFrame126("@or1@ - @gre@Groot is alive! @whi@(::groot)", lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@ - @whi@Groot Counter: @gre@" + (Groot.ActivityPoints) + " points.", lines.get(index++));
        }

        List<String> events = WorldEventContainer.getInstance().getWorldEventStatuses();
        for (String event : events) {
            player.getPA().sendFrame126("@or1@ - @whi@" + event, lines.get(index++));
        }
        player.getPA().sendFrame126("", lines.get(index++));
        player.getPA().sendFrame126("<img=63> @or1@Skilling Events", lines.get(index++));
        if (ShootingStars.progress) {
            player.getPA().sendFrame126("@or1@ - @whi@Star has landed, @gre@(::star)", lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@ - @whi@Shooting Star: @red@Inactive", lines.get(index++));
        }
        if (CrystalTree.progress) {
            player.getPA().sendFrame126("@or1@ - @whi@Tree has sprouted, @gre@(::tree)", lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@ - @whi@Glistening Tree: @red@Inactive", lines.get(index++));
        }
        if (ActiveVolcano.progress) {
            player.getPA().sendFrame126("@or1@ - @whi@Volcano Erupting, @gre@(::volcano)", lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@ - @whi@Volcano: @red@Inactive", lines.get(index++));
        }
        if (totalLogsAdded > 5000) {
            player.getPA().sendFrame126("@or1@ - @gre@1.5x experience is active!", lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@ - @whi@Bonfire Counter: @gre@" + (totalLogsAdded) + "/5000", lines.get(index++));
        }
        player.getPA().sendFrame126("", lines.get(index++));
        player.getPA().sendFrame126(" @cr24@ @or1@Slayer Information", lines.get(index++));

        if (player.getSlayer().getTask().isEmpty()) {
            player.getPA().sendFrame126("@or1@ - @whi@Slayer Task: @red@ None", lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@ - @whi@Slayer Task: @gre@" + player.getSlayer().getTaskAmount() + " " + player.getSlayer().getTask().get().getFormattedName() + "s", lines.get(index++));
        }
        player.getPA().sendFrame126("@or1@ - @whi@Slayer points: @gre@" + player.getSlayer().getPoints(), lines.get(index++));


        player.getPA().sendFrame126("", lines.get(index++));
        player.getPA().sendFrame126(" @cr23@ @or1@Daily Boost", lines.get(index++));

        index = addBoostsInformation(lines, index);

        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            player.getPA().sendFrame126("@or1@ - @whi@2x Groot Activity Points!", lines.get(index++));
        }

        player.getPA().sendFrame126("", lines.get(index++));
        player.getPA().sendFrame126(" @cr49@ @or1@Well Boosts", lines.get(index++));
        if (EXPERIENCE_TIMER > 0) {
            player.getPA().sendFrame126("@or1@ - @whi@XP Timer: @gre@"  + Misc.cyclesToDottedTime((int) Wogw.EXPERIENCE_TIMER), lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@ - @whi@XP Timer: @red@Inactive", lines.get(index++));
        }
        if (PC_POINTS_TIMER > 0) {
            player.getPA().sendFrame126("@or1@ - @whi@PC Timer: @gre@"  + Misc.cyclesToDottedTime((int) Wogw.PC_POINTS_TIMER), lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@ - @whi@PC Timer: @red@Inactive", lines.get(index++));
        }
        if (_20_PERCENT_DROP_RATE_TIMER > 0) {
            player.getPA().sendFrame126("@or1@ - @whi@DR Timer: @gre@"  + Misc.cyclesToDottedTime((int) Wogw._20_PERCENT_DROP_RATE_TIMER), lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@ - @whi@DR Timer: @red@Inactive", lines.get(index++));
        }

        player.getPA().sendFrame126("", lines.get(index++));
        player.getPA().sendFrame126(" @cr60@ @or1@Player Boost", lines.get(index++));

        if (player.bonusXpTime > 0) {
            player.getPA().sendFrame126("@or1@ - @whi@XP Scroll Timer: @gre@"  + Misc.cyclesToDottedTime((int) player.xpScrollTicks), lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@ - @whi@XP Scroll Timer: @red@Inactive", lines.get(index++));
        }

        if (player.bonusXpTime > 0) {
            player.getPA().sendFrame126("@or1@ - @whi@XP Scroll Timer: @gre@"  + Misc.cyclesToDottedTime((int) player.xpScrollTicks), lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@ - @whi@XP Scroll Timer: @red@Inactive", lines.get(index++));
        }



        while (index < lines.size()) {
            player.getPA().sendString("", lines.get(index++));
        }
    }

    private void points(int points, String name, int lineId) {
        if (name.equalsIgnoreCase("Upgrade Points")) {
            player.getPA().sendFrame126("@or1@- " + name + " [@gre@" + Misc.formatCoins(player.foundryPoints) + "@or1@]", lineId);
        } else if (name.equalsIgnoreCase("AOE Points")) {
            player.getPA().sendFrame126("@or1@- " + name + " [@gre@" + Misc.formatCoins(player.instanceCurrency) + "@or1@]", lineId);
        } else {
            player.getPA().sendFrame126("@or1@- " + name + " [@gre@" + points + "@or1@]", lineId);
        }

    }

    private int addBoostsInformation(List<Integer> lines, int index) {
        List<? extends Booster<?>> boosts = Boosts.getBoostsOfType(player, null, BoostType.EXPERIENCE);
        if (!boosts.isEmpty()) {
            player.getPA().sendFrame126("<col=00c0ff> " + boosts.get(0).getDescription(), lines.get(index++));
        }

        boosts = Boosts.getBoostsOfType(player, null, BoostType.GENERIC);
        for (Booster<?> boost : boosts) {
            player.getPA().sendFrame126("<col=00c0ff> " + boost.getDescription(), lines.get(index++));
        }

        return index;
    }

    /**
     * Handles all actions within the help tab
     */
    public boolean handleHelpTabActionButton(int button) {
        for (int index = 0; index < COIN_TAB_BUTTONS.length; index++) {
            if (button == COIN_TAB_BUTTONS[index]) {
                CoinTab coinTab = CoinTab.values()[index];
                player.getQuesting().handleHelpTabActionButton(button);
                switch(coinTab) {
                    case WORLD_EVENTS:
                        WorldEventInformation.openInformationInterface(player);
                        return true;
                    case COLLECTION_LOG:
                        CollectionLog group = player.getGroupIronmanCollectionLog();
                        if (group != null) {
                            new DialogueBuilder(player).option(
                                    new DialogueOption("Personal", plr -> player.getCollectionLog().openInterface(plr)),
                                    new DialogueOption("Group", group::openInterface)
                            ).send();
                            return true;
                        } else {
                            CollectionLog group2 = player.getGroupWildymanCollectionLog();
                            if (group2 != null) {
                                new DialogueBuilder(player).option(
                                        new DialogueOption("Personal", plr -> player.getCollectionLog().openInterface(plr)),
                                        new DialogueOption("Group", group::openInterface)
                                ).send();
                                return true;
                            }
                        }
                        player.getCollectionLog().openInterface(player);
                        return true;
                    case MONSTER_KILL_LOG:
                        MonsterKillLog.openInterface(player);
                        return true;
                    case DROP_TABLE:
                        Server.getDropManager().openDefault(player);
                        return true;
                    case PRESETS:
/*                        Area[] areas = {
                            new SquareArea(3066, 3521, 3135, 3456),
                        };
                        if (Arrays.stream(areas).anyMatch(area -> area.inside(player))) {
                            PresetManager.getSingleton().open(player);
                			player.inPresets = true;
                        } else {
                            player.sendMessage("You must be in Edgeville to open presets.");
                        }*/
                        player.getTaskMaster().showInterface();
                        return true;
                    case DONATOR_BENEFITS:
                        player.getPA().sendFrame126(Configuration.DONATOR_BENEFITS_LINK, 12000);
                        return true;
                    case TITLES:
                        player.getTitles().display();
                        return true;
                    case COMMUNITY_GUIDES:
                        player.getPA().sendFrame126(Configuration.GUIDES_LINK, 12000);
                        return true;
                    case VOTE_PAGE:
                        player.getPA().sendFrame126(Configuration.VOTE_LINK, 12000);
                        return true;
                    case ONLINE_STORE:
                        player.getPA().sendFrame126(Configuration.STORE_LINK, 12000);
                        return true;
                    case FORUMS:
                        player.getPA().sendFrame126(Configuration.WEBSITE, 12000);
                        return true;
                    case RULES:
                        player.getPA().sendFrame126(Configuration.RULES_LINK, 12000);
                        return true;
                    case LOOT_TABLES:
                        LootableInterface.openInterface(player);
                        return true;
                    case CALL_FOR_HELP:
                        List<Player> staff = PlayerHandler.nonNullStream().filter(Objects::nonNull).filter(p -> p.getRights().isOrInherits(Right.HELPER))
                                .collect(Collectors.toList());
                        player.sendMessage("@red@You can also type ::help to report something.");
                        if (staff.size() > 0) {
                            String message = "@blu@[Help] " + player.getDisplayName()
                                    + " needs help, PM or TELEPORT and help them.";
                            if (DiscordBot.INSTANCE != null) {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setTitle(" [ PLAYER HELP ] ");
                                embed.setColor(Color.BLUE);
                                embed.setTimestamp(java.time.Instant.now());
                                embed.addField("Player: ", player.getDisplayName() + " needs help. - " + message, false);
                                DiscordBot.INSTANCE.sendStaffLogs(embed.build());
                                DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
                            }
                            PlayerHandler.sendMessage(message, staff);
                        } else {
                            player.sendMessage("@red@You've activated the help command but there are no staff-members online.");
                            player.sendMessage("@red@Please try contacting a staff on the forums and discord and they will respond ASAP.");
                            player.sendMessage("@red@You can also type ::help to report something.");
                        }
                        return true;
                    default:
                        return false;
                }
            }
        }

        return false;
    }
}
