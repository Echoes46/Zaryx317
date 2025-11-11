package io.zaryx.content.combat.death;

import com.google.common.collect.Lists;
import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.WeaponGames.WGManager;
import io.zaryx.content.bosses.nightmare.NightmareConstants;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.content.combat.melee.MeleeData;
import io.zaryx.content.combat.pvp.PkpRewards;
import io.zaryx.content.commands.helper.vboss;
import io.zaryx.content.items.PvpWeapons;
import io.zaryx.content.itemskeptondeath.ItemsLostOnDeath;
import io.zaryx.content.itemskeptondeath.ItemsLostOnDeathList;
import io.zaryx.content.minigames.pest_control.PestControl;
import io.zaryx.content.minigames.pk_arena.Highpkarena;
import io.zaryx.content.minigames.pk_arena.Lowpkarena;
import io.zaryx.content.minigames.raids.Raids;
import io.zaryx.content.teamBounties.BountySystem;
import io.zaryx.content.tournaments.TourneyManager;
import io.zaryx.model.Items;
import io.zaryx.model.SoundType;
import io.zaryx.model.collisionmap.doors.Location;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.npc.pets.PetHandler;
import io.zaryx.model.entity.player.*;
import io.zaryx.model.entity.player.mode.Mode;
import io.zaryx.model.entity.player.mode.ModeType;
import io.zaryx.model.entity.player.save.PlayerSave;
import io.zaryx.model.entity.thrall.ThrallSystem;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.items.GroundItem;
import io.zaryx.model.multiplayersession.MultiplayerSession;
import io.zaryx.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.zaryx.model.multiplayersession.MultiplayerSessionStage;
import io.zaryx.model.multiplayersession.MultiplayerSessionType;
import io.zaryx.model.multiplayersession.duel.DuelSession;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.DiscordBot;
import io.zaryx.util.logging.player.DeathItemsHeld;
import io.zaryx.util.logging.player.DeathItemsKept;
import io.zaryx.util.logging.player.DeathItemsLost;
import io.zaryx.util.logging.player.DeathLog;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PlayerDeath {

    private static void beforeDeath(Player c) {
        TourneyManager.setFog(c, false, 0);
        c.getPA().sendFrame126(":quicks:off", -1);
        c.getItems().setEquipmentUpdateTypes();
        c.getPA().requestUpdates();
        c.respawnTimer = 15;
        c.isDead = false;
        c.graceSum = 0;
        c.freezeTimer = 1;
        c.recoilHits = 0;
        c.totalHunllefDamage = 0;
        c.tradeResetNeeded = true;
        c.setSpellId(-1);
        c.attacking.reset();
        c.getPA().resetAutocast();
        c.getPotions().resetInfPrayer();
        c.usingMagic = false;
    }

    private static void afterDeath(Player c) {
        c.playerStandIndex = 808;
        c.playerWalkIndex = 819;
        c.playerRunIndex = 824;
        PlayerSave.saveGame(c);
        c.getPA().requestUpdates();
        c.getPA().removeAllWindows();
        c.getPA().closeAllWindows();
        c.getPA().resetFollowers();
        c.getPotions().resetInfPrayer();
        c.getItems().addSpecialBar(c.playerEquipment[Player.playerWeapon]);
        c.specAmount = 10;
        c.attackTimer = 10;
        c.respawnTimer = 15;
        c.lastVeng = 0;
        c.recoilHits = 0;
        c.graceSum = 0;
        c.freezeTimer = 1;
        c.vengOn = false;
        c.isDead = false;
        c.tradeResetNeeded = true;
    }

    public static void applyDead(Player c) {
        beforeDeath(c);
        c.getPA().sendSound(513, SoundType.SOUND);
        MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.TRADE);
        if (session != null && Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
            c.sendMessage("You have declined the trade.");
            session.getOther(c).sendMessage(c.getDisplayName() + " has declined the trade.");
            session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
            return;
        }

        DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
        if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
            duelSession = null;
        }

        if (c.getSlayer().superiorSpawned) {
            c.getSlayer().superiorSpawned = false;
        }

        // Determine the killer
        Entity killer = c.calculateKiller();
        Player playerKiller = null;

        if (killer != null && killer.isPlayer()) {
            playerKiller = (Player) killer;
        }
        if (playerKiller != null && BountySystem.hasActiveBounty(c.getLoginName())) {
            if (playerKiller.getIpAddress().equals(c.getIpAddress())) {
                playerKiller.sendMessage("@red@You cannot claim a bounty on a player with the same IP address as yours.");
                return;
            }

            int rewardAfterTax = BountySystem.getActiveBountyReward(c.getLoginName()) - (BountySystem.getActiveBountyReward(c.getLoginName()) * 15 / 100);

            playerKiller.getItems().addItem(13307, rewardAfterTax);
            playerKiller.getItems().addItem(13306, 1);
            int randomAmount = Misc.random(1_000_000, 5_000_000);
            playerKiller.getItems().addItem(995, randomAmount);

            BountySystem.broadcastBountyClaim(playerKiller, c.getLoginName(), rewardAfterTax);

            BountySystem.claimBounty(playerKiller, c.getLoginName());
        }

        if (c.getRights().isOrInherits(Right.EVENT_MAN)) {
            if (Boundary.isIn(c, Boundary.DUEL_ARENA) || Boundary.isIn(c, Boundary.FIGHT_CAVE)
                    || c.getPosition().inClanWarsSafe() || Boundary.isIn(c, Boundary.INFERNO)
                    || c.getInstance() != null
                    || c.getPosition().inHomeAreaSafe()
                    || Boundary.isIn(c, NightmareConstants.BOUNDARY)
                    || Boundary.isIn(c, Boundary.OUTLAST_AREA)
                    || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)
                    || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
                    || Boundary.isIn(c, Boundary.FOREST_OUTLAST)
                    || Boundary.isIn(c, Boundary.SNOW_OUTLAST)
                    || Boundary.isIn(c, Boundary.ROCK_OUTLAST)
                    || Boundary.isIn(c, Boundary.BOUNTY_HUNTER_OUTLAST)
                    || Boundary.isIn(c, Boundary.FALLY_OUTLAST)
                    || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
                    || Boundary.isIn(c, Boundary.HESPORI)
                    || Boundary.isIn(c, Boundary.HESPORI_ENTRANCE)
                    || Boundary.isIn(c, Boundary.HESPORI_EXIT)
                    || Boundary.isIn(c, Boundary.SWAMP_OUTLAST)
                    || Boundary.isIn(c, Boundary.PEST_CONTROL_AREA)
                    || Boundary.isIn(c, Boundary.WG_Boundary)
                    || Boundary.isIn(c, Boundary.RAIDS)
                    || Boundary.isIn(c, Boundary.DUNGROOMS)
                    || Boundary.isIn(c, Boundary.OLM)
                    || Boundary.isIn(c, Boundary.RAID_MAIN)
                    || Boundary.isIn(c, Boundary.XERIC)
                    || Boundary.isIn(c, Boundary.VOTE_BOSS)
                    || Boundary.isIn(c, Boundary.NEX)
                    || Boundary.isIn(c, Boundary.DONATOR_ZONE_BLOODY)
                    || Boundary.isIn(c, Boundary.DONATOR_ZONE_BOSS)) {
                return;
            }
            PlayerHandler.executeGlobalMessage("@red@News: @blu@" + c.getDisplayNameFormatted()
                    + " @pur@has just died, with a skill total of " + c.totalLevel + "!");
        }

        if (c.getMode().isHardcoreIronman()) {
            if (Boundary.isIn(c, Boundary.DUEL_ARENA) || Boundary.isIn(c, Boundary.FIGHT_CAVE)
                    || c.getPosition().inClanWarsSafe() || Boundary.isIn(c, Boundary.INFERNO)
                    || c.getInstance() != null
                    || Boundary.isIn(c, NightmareConstants.BOUNDARY)
                    || c.getPosition().inHomeAreaSafe()
                    || Boundary.isIn(c, Boundary.OUTLAST_AREA)
                    || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)
                    || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
                    || Boundary.isIn(c, Boundary.FOREST_OUTLAST)
                    || Boundary.isIn(c, Boundary.SNOW_OUTLAST)
                    || Boundary.isIn(c, Boundary.ROCK_OUTLAST)
                    || Boundary.isIn(c, Boundary.BOUNTY_HUNTER_OUTLAST)
                    || Boundary.isIn(c, Boundary.FALLY_OUTLAST)
                    || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
                    || Boundary.isIn(c, Boundary.SWAMP_OUTLAST)
                    || Boundary.isIn(c, Boundary.PEST_CONTROL_AREA)
                    || Boundary.isIn(c, Boundary.HESPORI)
                    || Boundary.isIn(c, Boundary.HESPORI_ENTRANCE)
                    || Boundary.isIn(c, Boundary.HESPORI_EXIT)
                    || Boundary.isIn(c, Boundary.WG_Boundary)
                    || Boundary.isIn(c, Boundary.RAIDS)
                    || Boundary.isIn(c, Boundary.DUNGROOMS)
                    || Boundary.isIn(c, Boundary.OLM)
                    || Boundary.isIn(c, Boundary.RAID_MAIN)
                    || Boundary.isIn(c, Boundary.XERIC)
                    || Boundary.isIn(c, Boundary.OUTLAST)
                    || Boundary.isIn(c, Boundary.VOTE_BOSS)
                    || Boundary.isIn(c, Boundary.NEX)
                    || Boundary.isIn(c, Boundary.DONATOR_ZONE_BLOODY)
                    || Boundary.isIn(c, Boundary.DONATOR_ZONE_BOSS)) {
                return;
            }

            if (!Configuration.DISABLE_HC_LOSS_ON_DEATH) {
                if (c.totalLevel > 500) {
                    PlayerHandler.executeGlobalMessage("@red@News: @blu@" + c.getDisplayNameFormatted()
                            + " @pur@has just died in hardcore ironman mode, with a skill total of " + c.totalLevel + "!");
                }

                if (c.getMode().getType() == ModeType.HC_IRON_MAN) {
                    c.getRights().remove(Right.HC_IRONMAN);
                    c.setMode(Mode.forType(ModeType.IRON_MAN));
                    c.getRights().setPrimary(Right.IRONMAN);
                    c.sendMessage("You are now a normal Ironman.");
                } else if (c.getMode().getType() == ModeType.ROGUE_HARDCORE_IRONMAN) {
                    c.getRights().remove(Right.ROGUE_HARDCORE_IRONMAN);
                    c.setMode(Mode.forType(ModeType.ROGUE_IRONMAN));
                    c.getRights().setPrimary(Right.ROGUE_IRONMAN);
                    c.sendMessage("You are now a rogue Ironman.");
                } else {
                    throw new IllegalStateException("Not a hardcore: " + c.getMode());
                }

                PlayerSave.saveGame(c);
            }
        }

        if (Objects.isNull(duelSession)) {
            if (killer != null) {
                if (c.wildLevel > 0 && c.underAttackByPlayer > 0) {
                    killer = PlayerHandler.players[c.underAttackByPlayer];
                    c.setKiller(killer);
                    Player playerKillerInWild = (Player) killer;
                    c.killerId = killer.getIndex();
                    c.sendMessage("Oh dear you are dead!");
                    killer.asPlayer().sendMessage("You killed " + c.getDisplayName());
                } else {
                    c.setKiller(killer);
                    if (killer instanceof Player) {
                        Player playerKillerInDuel = (Player) killer;
                        c.killerId = killer.getIndex();
                    }
                    c.sendMessage("Oh dear you are dead!");
                }
            }
        }

        if (c.prayerActive[CombatPrayer.WRATH] && !Boundary.isIn(c, Boundary.OUTLAST_AREA)) {
            c.gfx0(437);

            List<Entity> possibleTargets = Lists.newArrayList();

            if (c.getPosition().inMulti()) {
                Arrays.stream(PlayerHandler.players).filter(Objects::nonNull).forEach(p -> {
                    if (p != c && p.getPosition().withinDistance(c.getPosition(), 1))
                        possibleTargets.add(p);
                });
                Arrays.stream(NPCHandler.npcs).filter(Objects::nonNull).forEach(n -> {
                    // Size check for NPCs like Corp, etc.
                    if (n.getPosition().withinDistance(c.getPosition(), n.getSize()))
                        possibleTargets.add(n);
                });
            }

            Entity killerInWrath = c.getKiller();

            if (killerInWrath != null) {
                if (!possibleTargets.contains(killerInWrath))
                    possibleTargets.add(killerInWrath);
            }

            possibleTargets.forEach(e -> {
                if (possibleTargets.isEmpty())
                    return;
                double howMuchDamage = (c.getPA().getLevelForXP(c.playerXP[5]) * 2.5);
                e.appendDamage(Misc.random(1, (int) howMuchDamage), Hitmark.HIT);
            });
        }

        if (c.prayerActive[CombatPrayer.RETRIBUTION] && !Boundary.isIn(c, Boundary.OUTLAST_AREA)) {
            c.gfx0(437);

            List<Entity> possibleTargets = Lists.newArrayList();

            if (c.getPosition().inMulti()) {
                Arrays.stream(PlayerHandler.players).filter(Objects::nonNull).forEach(p -> {
                    if (p != c && p.getPosition().withinDistance(c.getPosition(), 1))
                        possibleTargets.add(p);
                });
                Arrays.stream(NPCHandler.npcs).filter(Objects::nonNull).forEach(n -> {
                    // Size check for NPCs like Corp, etc.
                    if (n.getPosition().withinDistance(c.getPosition(), n.getSize()))
                        possibleTargets.add(n);
                });
            }

            Entity killerInRetribution = c.getKiller();

            if (killerInRetribution != null) {
                if (!possibleTargets.contains(killerInRetribution))
                    possibleTargets.add(killerInRetribution);
            }

            possibleTargets.forEach(e -> {
                if (possibleTargets.isEmpty())
                    return;
                e.appendDamage(Misc.random(1, Misc.random(1, (c.playerLevel[5] / 4))), Hitmark.HIT);
            });
        }

        afterDeath(c);
    }



    /**
     * Handles what happens after a player death
     */
    public static void giveLife(Player c) {
        // Set the visual masks of a player

        c.isDead = false;
        c.totalHunllefDamage = 0;
        c.faceUpdate(-1);
        c.freezeTimer = 1;
        c.isAnimatedArmourSpawned = false;
        c.setTektonDamageCounter(0);
        if (c.getGlodDamageCounter() >= 80 || c.getIceQueenDamageCounter() >= 80) {
            c.setGlodDamageCounter(79);
            c.setIceQueenDamageCounter(79);
        }
        if (c.hasFollower) {
            if (c.petSummonId > 0) {
                PetHandler.Pets pet = PetHandler.forItem(c.petSummonId);
                if (pet != null) {
                    PetHandler.spawn(c, pet, true, false);
                }
            }
        }
        if (c.hasThrall) {
            if (c.ThrallSummonId > 0) {
                ThrallSystem thrall = ThrallSystem.forThrall(c.ThrallSummonId);
                if (thrall != null) {
                    ThrallSystem.spawnThrall(c, thrall);
                }
            }
        }
        c.getQuestTab().updateInformationTab();
        c.getPA().stopSkilling();
        Arrays.fill(c.activeMageArena2BossId, 0);

        handleAreaBasedDeath(c);

        MeleeData.setWeaponAnimations(c);
        c.getItems().setEquipmentUpdateTypes();
        CombatPrayer.resetPrayers(c);
        for (int i = 0; i < 20; i++) {
            c.playerLevel[i] = c.getPA().getLevelForXP(c.playerXP[i]);
            c.getPA().refreshSkill(i);
        }
        c.startAnimation(65535);
        PlayerSave.saveGame(c);
        c.resetOnDeath();
    }

    private static boolean checkArea(Player c) {
        if (Boundary.isIn(c, Boundary.DUEL_ARENA) || Boundary.isIn(c, Boundary.FIGHT_CAVE)
                || c.getPosition().inClanWarsSafe() || Boundary.isIn(c, Boundary.INFERNO)
                || c.getInstance() != null
                || Boundary.isIn(c, NightmareConstants.BOUNDARY)
                || c.getPosition().inHomeAreaSafe()
                || Boundary.isIn(c, Boundary.OUTLAST_AREA)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)
                || Boundary.isIn(c, Boundary.FOREST_OUTLAST)
                || Boundary.isIn(c, Boundary.SNOW_OUTLAST)
                || Boundary.isIn(c, Boundary.ROCK_OUTLAST)
                || Boundary.isIn(c, Boundary.BOUNTY_HUNTER_OUTLAST)
                || Boundary.isIn(c, Boundary.FALLY_OUTLAST)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
                || Boundary.isIn(c, Boundary.SWAMP_OUTLAST)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
                || Boundary.isIn(c, Boundary.PEST_CONTROL_AREA)
                || Boundary.isIn(c, Boundary.WG_Boundary)
                || Boundary.isIn(c, Boundary.RAIDS)
                || Boundary.isIn(c, Boundary.DUNGROOMS)
                || Boundary.isIn(c, Boundary.OLM)
                || Boundary.isIn(c, Boundary.RAID_MAIN)
                || Boundary.isIn(c, Boundary.XERIC)
                || Boundary.isIn(c, Boundary.OUTLAST)
                || Boundary.isIn(c, Boundary.TOB_VERZIK)
                || Boundary.isIn(c, Boundary.VOTE_BOSS)
                || Boundary.isIn(c, Boundary.NEX)
                ||Boundary.isIn(c, Boundary.DONATOR_ZONE_BLOODY)
                ||Boundary.isIn(c, Boundary.DONATOR_ZONE_BOSS)) { // TODO: Other areas.
            return true;
        }
        return false;
    }

    private static void handleAreaBasedDeath(Player c) {
        if (c.getInstance() != null && c.getInstance().handleDeath(c))
            return;
        c.removeFromInstance();

        CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.AFKZone);

        Server.getLogging().write(new DeathLog(c));

        if (!Configuration.DISABLE_HC_LOSS_ON_DEATH) {
            if (c.getMode().isHardcoreIronman() && !checkArea(c)) {

                if (c.totalLevel > 500) {
                    PlayerHandler.executeGlobalMessage("@red@News: @blu@" + c.getDisplayNameFormatted()
                            + " @pur@has just died in hardcore ironman mode, with a skill total of " + c.totalLevel
                            + "!");
                }

                if (c.getMode().getType() == ModeType.HC_IRON_MAN) {
                    c.getRights().remove(Right.HC_IRONMAN);
                    c.setMode(Mode.forType(ModeType.IRON_MAN));
                    c.getRights().setPrimary(Right.IRONMAN);
                    c.sendMessage("You are now a normal Ironman.");
                } else if (c.getMode().getType() == ModeType.ROGUE_HARDCORE_IRONMAN) {
                    c.getRights().remove(Right.ROGUE_HARDCORE_IRONMAN);
                    c.setMode(Mode.forType(ModeType.ROGUE_IRONMAN));
                    c.getRights().setPrimary(Right.ROGUE_IRONMAN);
                    c.sendMessage("You are now a rogue Ironman.");
               /* } else if (c.getMode().getType() == ModeType.WILDYMAN) {
                    c.getRights().remove(Right.WILDYMAN);
                    c.setMode(Mode.forType(ModeType.STANDARD));
                    c.getRights().setPrimary(Right.PLAYER);
                    c.sendMessage("You are now a wildyman.");*/
                } else {
                    throw new IllegalStateException("Not a hardcore: " + c.getMode());
                }

                PlayerSave.saveGame(c);
            }
        }

        if (TourneyManager.getSingleton().isInArena(c)) {
            Entity tourneyKiller = c.calculateTourneyKiller();

            c.outlastDeaths++;

            TourneyManager.getSingleton().handleDeath(c.getLoginName(), false);
            TourneyManager.getSingleton().handleKill(tourneyKiller);

            onRespawn(c);
            return;
        } else if (TourneyManager.getSingleton().isInLobbyBounds(c)) {
            TourneyManager.getSingleton().leaveLobby(c, false);

            onRespawn(c);
            return;
        }

        if (WGManager.getSingleton().isInArena(c)) {
            Entity wgKiller = c.calculateWGKiller();

            c.WGDeaths++;
            WGManager.getSingleton().handleDeath(c.getLoginName(), false);
            WGManager.getSingleton().handleKill(wgKiller.asPlayer(), c);

            onRespawn(c);
            return;
        } else if (WGManager.getSingleton().isInLobbyBounds(c)) {
            WGManager.getSingleton().leaveLobby(c, false);

            onRespawn(c);
            return;
        }



        if (Boundary.isIn(c, Boundary.PEST_CONTROL_AREA)) {
            c.getPA().movePlayer(2657, 2639, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, PestControl.GAME_BOUNDARY)) {
            c.getPA().movePlayer(2656 + Misc.random(2), 2614 - Misc.random(3), 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, vboss.BOUNDARY)) {
            c.getPA().movePlayer(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0);


        } else if (Boundary.isIn(c, Boundary.ZULRAH)) {
            c.getPA().movePlayer(2202, 3056, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.CERBERUS_BOSSROOMS)) {
            c.getPA().movePlayer(1309, 1250, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.DONATOR_ZONE_BLOODY)) {
            c.getBloody_battle().handleDeath();

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.DONATOR_ZONE_BOSS)) {
            c.getPA().movePlayer(1967, 5365,0);
            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.SKOTIZO_BOSSROOM)) {
            c.getPA().movePlayer(1665, 10045, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.DUEL_ARENA)) {
            DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
            if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERATION) {
                duelSession.finish(MultiplayerSessionFinalizeType.GIVE_ITEMS);

                onRespawn(c);
                return;
            }
        } else if (Boundary.isIn(c, Boundary.HYDRA_BOSS_ROOM)) {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
            c.getFightCave().handleDeath();

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.INFERNO) && c.getInferno() != null) {
            c.getInferno().handleDeath();

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.XERIC)) {
            if (c.getXeric().getXericTeam().size() > 1) {
                c.getXeric().HandleDeath(c, true);
            } else {
                c.getXeric().leaveGame(c, true);
            }
            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.OLM)) {
            Raids raidInstance = c.getRaidsInstance();
            if (raidInstance != null) {
                Location olmWait = raidInstance.getOlmWaitLocation();
                c.getPA().movePlayer(olmWait.getX(), olmWait.getY(), raidInstance.currentHeight);
                raidInstance.resetOlmRoom(c);

                onRespawn(c);
                return;
            }
        } else if (Boundary.isIn(c, Boundary.RAIDS)) {
            Raids raidInstance = c.getRaidsInstance();
            if (raidInstance != null) {
                Location startRoom = raidInstance.getStartLocation();
                c.getPA().movePlayer(startRoom.getX(), startRoom.getY(), raidInstance.currentHeight);
                raidInstance.resetRoom(c);

                onRespawn(c);
                return;
            }

            // TODO might have to add dungeoneering here
        } else if (Highpkarena.getState(c) != null) {
            Highpkarena.handleDeath(c);

            onRespawn(c);
            return;
        } else if (Lowpkarena.getState(c) != null) {
            Lowpkarena.handleDeath(c);

            onRespawn(c);
            return;
        } else if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
            c.getPA().movePlayer(c.absX, 4759, 0);

            onRespawn(c);
            return;

        } else if (c.getPosition().inHomeAreaSafe()) {
            c.getPA().movePlayer(2093, 5972, 0);
            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.SAFEPKSAFE)) {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.OUTLAST)) {
            c.getPA().movePlayer(new Coordinate(3077, 3491));

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.MIMIC_LAIR)) {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.GODWARS_BOSSROOMS)) {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.CORPOREAL_BEAST_LAIR)) {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.HUNLLEF_BOSS_ROOM)) {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.OBOR_AREA)) {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.BRYOPHYTA_ROOM)) {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, Boundary.KRAKEN_BOSS_ROOM)) {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);

            onRespawn(c);
            return;
        } else if (Boundary.isIn(c, NightmareConstants.BOUNDARY)) {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);

            onRespawn(c);
            return;
        } else if (c.getInstance() != null) {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);
            onRespawn(c);
            return;
        }

        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(" [ PLAYER DEATH ] ");
            embed.setColor(Color.BLUE);
            embed.setTimestamp(Instant.now());
            embed.addField(c.getDisplayName() + " has just died.", "\u200B", false);
            DiscordBot.INSTANCE.sendStaffLogs(embed.build());
        }

        if (c.wildLevel > 0) {
            Entity killer = c.getKiller();
            Player playerKiller = killer != null && killer.isPlayer() ? killer.asPlayer() : null;

            ItemsLostOnDeathList itemsLostOnDeathList = ItemsLostOnDeath.generateModified(c);

            Server.getLogging().write(new DeathItemsHeld(c, c.getItems().getInventoryItems(), c.getItems().getEquipmentItems()),
                    new DeathItemsKept(c, itemsLostOnDeathList.getKept()),
                    new DeathItemsLost(c, itemsLostOnDeathList.getLost()));

            /*if (itemsLostOnDeathList.getKept().size() > 0) {
                Discord.writeDeathHandler("[DeathLog-Kept] \n" + itemsLostOnDeathList.getKept().toString()
                        .replace("GameItem{","").replace("{", "").replace("}, ", "\n")
                        .replace("]", "").replace("[","").replace("}",""));
            }

            if (itemsLostOnDeathList.getLost().size() > 0) {
                Discord.writeDeathHandler("[DeathLog-Dropped] \n" + itemsLostOnDeathList.getLost().toString()
                        .replace("GameItem{","").replace("{", "").replace("}, ", "\n")
                        .replace("]", "").replace("[","").replace("}",""));
            }*/

            c.getItems().deleteAllItems();
            List<GameItem> lostItems = itemsLostOnDeathList.getLost();

            for (GameItem lostItem : lostItems) {
                if (PvpWeapons.isPvpWeaponCharged(lostItem.getId())) {
                    int charges = c.getPvpWeapons().getCrawsBowCharges();
                    GroundItem item = new GroundItem(21820,  c.absX, c.absY, c.heightLevel, charges, Misc.toCycles(1, TimeUnit.MINUTES), c.getLoginNameLower());
                    c.getItems().createGroundItem(item);
                    c.getPvpWeapons().setCrawsBowCharges(0);
                    lostItem.setId(PvpWeapons.UnchargeOnDeath(lostItem.getId()));
                }
            }

            // Drop untradeable cash for killer and put in lost property shop for victim
            List<GameItem> untradeables = lostItems.stream().filter(it -> !it.getDef().isTradable()).collect(Collectors.toList());

            // Drop untradeable coins for killer, otherwise drop nothing
            if (playerKiller != null) {
                int coins = untradeables.stream().mapToInt(it -> it.getDef().getShopValue()).sum();

                if (coins > 0)
                    lostItems.add(new GameItem(Items.COINS, coins));
            }

            lostItems.removeAll(untradeables);
            untradeables.forEach(item -> c.getPerduLostPropertyShop().add(c, item));

            if (playerKiller != null) {
                dropItemsForKiller(c, playerKiller, new GameItem(Items.BONES));
            }

            lostItems.forEach(item -> dropItemsForKiller(c, playerKiller, item));

            if (playerKiller != null)
                PkpRewards.award(c, playerKiller);

            for (GameItem item : itemsLostOnDeathList.getKept()) {
                if (c.getItems().hasRoomInInventory(item.getId(), item.getAmount())) {
                    c.getItems().addItem(item.getId(), item.getAmount());
                } else {
                    c.getItems().sendItemToAnyTab(item.getId(), item.getAmount());
                }
            }
        }

        if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN))) {
            c.getPA().movePlayer(3448, 3837, 0);
        } else {
            c.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);
        }
        onRespawn(c);

    }

    private static void dropItemsForKiller(Player killed, Player killer, GameItem item) {
        if (killer != null) {
            // Removes the PvP HP overlay from the killers screen when the target dies
            if (killed.equals(killer.getTargeted())) {
                killer.setTargeted(null);
                killer.getPA().sendEntityTarget(0, killed);
            }
            boolean takeItems = killer.getMode().isItemScavengingPermitted();

            if (!takeItems) {
                Server.itemHandler.createUnownedGroundItem(item, killed.getPosition());
            } else {
                Server.itemHandler.createGroundItem(killer, item, killed.getPosition());
            }
        } else {
            Server.itemHandler.createGroundItem(killed, item, killed.getPosition(), Misc.toCycles(3, TimeUnit.MINUTES));
        }
    }

    private static void onRespawn(Player c) {
        c.isSkulled = false;
        c.skullTimer = 0;
        c.attackedPlayers.clear();
        c.getPA().removeAllWindows();
        c.getPA().closeAllWindows();
        c.resetDamageTaken();
        c.setKiller(null);
        c.killerId = 0;
    }

}
