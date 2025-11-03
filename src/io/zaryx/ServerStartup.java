package io.zaryx;

import io.zaryx.annotate.Init;
import io.zaryx.annotate.PostInit;
import io.zaryx.content.WeaponGames.WGManager;
import io.zaryx.content.battlepass.Rewards;
import io.zaryx.content.boosts.Boosts;
import io.zaryx.content.bosses.godwars.GodwarsEquipment;
import io.zaryx.content.bosses.godwars.GodwarsNPCs;
import io.zaryx.content.bosses.kratos.SeldaehNpc;
import io.zaryx.content.bosses.nightmare.NightmareStatusNPC;
import io.zaryx.content.bosses.sarachnis.SarachnisNpc;
import io.zaryx.content.collection_log.CollectionLog;
import io.zaryx.content.combat.stats.TrackedMonster;
import io.zaryx.content.commands.CommandManager;
import io.zaryx.content.commands.all.Freerank;
import io.zaryx.content.dailyrewards.DailyRewardContainer;
import io.zaryx.content.dailyrewards.DailyRewardsRecords;
import io.zaryx.content.donationrewards.DonationReward;
import io.zaryx.content.event.eventcalendar.EventCalendar;
import io.zaryx.content.event.eventcalendar.EventCalendarWinnerSelect;
import io.zaryx.content.events.monsterhunt.MonsterHunt;
import io.zaryx.content.fireofexchange.FireOfExchangeBurnPrice;
//import io.zaryx.content.minigames.newMinigames.squidGames.SquidGames;
import io.zaryx.content.preset.PresetManager;
import io.zaryx.content.referral.ReferralCode;
import io.zaryx.content.skills.runecrafting.ouriana.ZamorakGuardian;
import io.zaryx.content.teamBounties.BountySystem;
import io.zaryx.content.tournaments.TourneyManager;
import io.zaryx.content.trails.TreasureTrailsRewards;
import io.zaryx.content.vote_panel.VotePanelManager;
import io.zaryx.content.wogw.Wogw;
import io.zaryx.content.worldevent.WorldEventContainer;
import io.zaryx.model.Npcs;
import io.zaryx.model.collisionmap.ObjectDef;
import io.zaryx.model.collisionmap.Region;
import io.zaryx.model.collisionmap.doors.DoorDefinition;
import io.zaryx.model.cycleevent.impl.BonusApplianceEvent;
import io.zaryx.model.definitions.*;
import io.zaryx.model.entity.npc.NPCRelationship;
import io.zaryx.model.entity.npc.NpcSpawnLoader;
import io.zaryx.model.entity.npc.NpcSpawnLoaderOSRS;
import io.zaryx.model.entity.npc.actions.CustomActions;
import io.zaryx.model.entity.npc.stats.NpcCombatDefinition;
import io.zaryx.model.entity.player.PlayerFactory;
import io.zaryx.model.entity.player.save.PlayerSave;
import io.zaryx.model.entity.player.save.backup.PlayerSaveBackup;
import io.zaryx.model.lobby.LobbyManager;
import io.zaryx.model.world.ShopHandler;
import io.zaryx.objects.Doors;
import io.zaryx.objects.DoubleDoors;
import io.zaryx.objects.ForceDoors;
import io.zaryx.punishments.PunishmentCycleEvent;
import io.zaryx.util.Reflection;
import io.zaryx.util.discord.DiscordIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStartup {

    private static final Logger logger = LoggerFactory.getLogger(ServerStartup.class);

    static void load() throws Exception {
        Reflection.getMethodsAnnotatedWith(Init.class).forEach(method -> {
            try {
                method.invoke(null);
            } catch (Exception e) {
                logger.error("Error loading @Init annotated method[{}] inside class[{}]", method, method.getClass(), e);
                e.printStackTrace();
                System.exit(1);
            }
        });

        Freerank.loadUsedIps();
        System.out.println("Reading Free Ranks");
        Rewards.init();
        DonationReward.load();
        PlayerSave.loadPlayerSaveEntries();
        EventCalendarWinnerSelect.getInstance().init();
        TrackedMonster.init();
        Boosts.init();
        ItemDef.load( );
        ShopDef.load();
        ShopHandler.load();
        NpcStats.load();
        ItemStats.load();
        NpcDef.load();
        // Npc Combat Definition must be above npc load
        NpcCombatDefinition.load();
        Server.npcHandler.init();
        NPCRelationship.setup();
        EventCalendar.verifyCalendar();
        Server.getPunishments().initialize();
        //Server.getEventHandler().submit(new DidYouKnowEvent());
        Server.getEventHandler().submit(new BonusApplianceEvent());
        Server.getEventHandler().submit(new PunishmentCycleEvent(Server.getPunishments(), 50));
//        Server.getEventHandler().submit(new UpdateQuestTab());
        //Server.getEventHandler().submit(new LeaderboardUpdateEvent());
        Wogw.init();
//        AOESystem.getSingleton().loadAOEDATA();
        //PollTab.init();
        DoorDefinition.load();
        GodwarsEquipment.load();
        GodwarsNPCs.load();
        LobbyManager.initializeLobbies();
        VotePanelManager.init();
        TourneyManager.initialiseSingleton();
        TourneyManager.getSingleton().init();
        //SquidGames.initialiseSingleton();
        WGManager.initialiseSingleton();
        Server.getDropManager().read();
        TreasureTrailsRewards.load();
        AnimationLength.startup();
        PresetManager.getSingleton().init();
        ObjectDef.loadConfig();
        ItemDefinitionLoader.init();
        CollectionLog.init();
        Region.load();
        Server.getGlobalObjects().loadGlobalObjectFile();
        DiscordIntegration.loadConnectedAccounts();
        Doors.getSingleton().load();
        DoubleDoors.getSingleton().load();
        // Keep this below region load and object loading
        NpcSpawnLoader.load();
        NpcSpawnLoaderOSRS.initOsrsSpawns();
        MonsterHunt.spawnNPC();
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        CommandManager.initializeCommands();
        NightmareStatusNPC.init();

        CustomActions.loadActions();
        //Halloween.initHalloween();

        if (Server.isDebug()) {
            PlayerFactory.createTestPlayers();
        }
        ReferralCode.load();
        DailyRewardContainer.load();
        DailyRewardsRecords.load();
        WorldEventContainer.getInstance().initialise();
        FireOfExchangeBurnPrice.init();
        Server.getLogging().schedule();

        //ItemCollection.IO.init("offlinerewards");

        ForceDoors.Init();
        BountySystem.loadActiveBounties();
//        Server.loadSqlNetwork();
//        Server.loadRealmSqlNetwork();
        //io.zaryx.sql.ingamestore.Configuration.loadConfiguration();

        ZamorakGuardian.spawn();
        new SarachnisNpc(Npcs.SARACHNIS, SarachnisNpc.SPAWN_POSITION);
        new SeldaehNpc(Npcs.SELDAEH, SeldaehNpc.SPAWN_POSITION);

        if (Server.isPublic()) {
            PlayerSaveBackup.start(Configuration.PLAYER_SAVE_TIMER_MILLIS, Configuration.PLAYER_SAVE_BACKUP_EVERY_X_SAVE_TICKS);
        }

        Reflection.getMethodsAnnotatedWith(PostInit.class).forEach(method -> {
            try {
                method.invoke(null);
            } catch (Exception e) {
                logger.error("Error loading @PostInit annotated method[{}] inside class[{}]", method, method.getClass(), e);
                e.printStackTrace();
                System.exit(1);
            }
        });


    }

}
