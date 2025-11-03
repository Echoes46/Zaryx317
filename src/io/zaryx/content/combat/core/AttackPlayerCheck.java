package io.zaryx.content.combat.core;

import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.WeaponGames.WGManager;
import io.zaryx.content.tournaments.TourneyManager;
import io.zaryx.model.CombatType;
import io.zaryx.model.Items;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.ClientGameTimer;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Right;
import io.zaryx.model.items.ItemAssistant;
import io.zaryx.model.multiplayersession.MultiplayerSessionType;
import io.zaryx.model.multiplayersession.duel.DuelSession;
import io.zaryx.model.multiplayersession.duel.DuelSessionRules;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static io.zaryx.model.entity.player.packets.AttackPlayer.ARMORWILD;
import static io.zaryx.model.entity.player.packets.AttackPlayer.WEAPONWILD;

public class AttackPlayerCheck {

    private static void sendCheckMessage(Player c, boolean sendMessages, String message) {
        if (sendMessages) {
            c.sendMessage(message);
        }
    }

    public void announceWornArmor(Player player) {
        for (int armorItem : ARMORWILD) {
            if (player.getItems().isWearingItem(armorItem)) {  // Check if the player is wearing this armor
                player.sendMessage("@blu@You cannot use @red@" + ItemAssistant.getItemName(armorItem) + " @blu@ in the wild");
                break; // Exit the loop once we've found and announced a worn item
            }
        }
    }

    public static boolean check(Player c, Entity targetEntity, boolean sendMessages) {
        Player o = targetEntity.asPlayer();
        if (o == null || c.getIndex() == o.getIndex() || c.equals(o)) {
            return false;
        }

        if (o.getBankPin().requiresUnlock()) {
            sendCheckMessage(c, sendMessages, "You cannot do this while a player is in lock-down.");
            return false;
        }

        if (o.isInvisible()) {
            sendCheckMessage(c, sendMessages, "You cannot attack another player whilst they are invisible.");
            return false;
        }
        if (o.isNpc) {
            sendCheckMessage(c, sendMessages, "You cannot attack another player in this form.");
            return false;
        }

        if (c.hasOverloadBoost) {
            if (Boundary.isIn(c, Boundary.DUEL_ARENA) || Boundary.isIn(c, Boundary.WILDERNESS)) {
                c.getPotions().resetOverload();
                c.getPA().sendGameTimer(ClientGameTimer.OVERLOAD, TimeUnit.MINUTES, 0);
            }
        }
        if (Boundary.isIn(c, Boundary.DUEL_ARENA)) {
            if (Boundary.isIn(o, Boundary.DUEL_ARENA)) {
                DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
                if (Objects.isNull(session)) {
                    sendCheckMessage(c, sendMessages, "You cannot attack this player.");
                    return false;
                }
                if (!session.getPlayers().containsAll(Arrays.asList(o, c))) {
                    sendCheckMessage(c, sendMessages, "This player is not your opponent.");
                    return false;
                }
                if (!Boundary.isInSameBoundary(c, session.getOther(c), Boundary.DUEL_ARENA)) {
                    sendCheckMessage(c, sendMessages, "You cannot attack a player if you're not in the same arena.");
                    c.getPA().movePlayer(session.getArenaBoundary().getMinimumX(), session.getArenaBoundary().getMinimumX(), 0);
                    return false;
                }
                if (!session.isAttackingOperationable()) {
                    sendCheckMessage(c, sendMessages, "You cannot attack your opponent yet.");
                    return false;
                }

                if (c.attacking.getCombatType() == CombatType.MAGE && session.getRules().contains(DuelSessionRules.Rule.NO_MAGE)) {
                    sendCheckMessage(c, sendMessages, "You cannot use mage in this duel.");
                    return false;
                } else if (c.attacking.getCombatType() == CombatType.MELEE && session.getRules().contains(DuelSessionRules.Rule.NO_MELEE)) {
                    sendCheckMessage(c, sendMessages, "You cannot use melee in this duel.");
                    return false;
                } else if (c.attacking.getCombatType() == CombatType.RANGE && session.getRules().contains(DuelSessionRules.Rule.NO_RANGE)) {
                    sendCheckMessage(c, sendMessages, "You cannot use ranged in this duel.");
                    return false;
                }
                return true;
            } else {
                sendCheckMessage(c, sendMessages, "You cannot attack a player outside of the duel arena.");
                return false;
            }
        }

        if (TourneyManager.getSingleton().isInArena(c)) {
            if (!c.canAttack) {
                sendCheckMessage(c, sendMessages, "You need to wait for the tournament to start before fighting!");
                return false;
            }
            if (c.tournamentTarget == null) {
                c.sendMessage("You can only attack players who are your target!");
                return false;
            }

            if (!c.tournamentTarget.equals(o)) {
                c.sendMessage("You can only attack your target! which is " + c.tournamentTarget.getDisplayName());
                return false;
            }
            //if (c.firstTournamentHit == 0) {
            //   c.tournamentActivityTime = 200;
            //    c.firstTournamentHit = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3);
            //   TourneyManager.resetCombatVariables(c, true, false);
            //    return true;
            // }
            return true;
        }

        if (WGManager.getSingleton().isInLobbyBounds(c) || WGManager.getSingleton().isInArena(c)) {
            if (!c.canAttack) {
                sendCheckMessage(c, sendMessages, "You need to wait for the WeaponGames to start before fighting!");
                return false;
            }
            return true;
        }

        if (c.inPits) {
            if (o.inPits) {
                return true;
            }
            return false;
        }

        if (!o.getPosition().inWild() && !c.getItems().isWearingItem(Items.SNOWBALL, 3) && !TourneyManager.getSingleton().isInArena(c) && !WGManager.getSingleton().isInArena(c)) {
            sendCheckMessage(c, sendMessages, "That player is not in the wilderness.");
            return false;
        }

        if (c.getPosition().inWild() && c.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33216)) {
            c.sendMessage("@red@You can not attack in wild with trickster attuned.");
            return false;
        }
        if (c.getPosition().inWild() && c.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33216)) {
            c.sendMessage("@red@You can not attack in wild with AOE Relic attuned.");
            return false;
        }
        for (int armorItem : ARMORWILD) {
            if (c.getItems().isWearingItem(armorItem)) {  // Check if the player has the item equipped or in the inventory
                c.sendMessage("@blu@You can not use @red@" + ItemAssistant.getItemName(armorItem) + " @blu@ in the wild");
                return false;  // Stop further checks and return false
            }
        }
        for (int wepItem : WEAPONWILD) {
            if (c.getItems().isWearingItem(wepItem)) {  // Check if the player has the item equipped or in the inventory
                c.sendMessage("@blu@You can not use @red@" + ItemAssistant.getItemName(wepItem) + " @blu@ in the wild");
                return false;  // Stop further checks and return false
            }
        }

        if (c.hasGuthixRestBoost) { // Check if the player has the item equipped or in the inventory
            c.sendMessage("@blu@You can not attack players with the @redGuthix rest @blue@boost active");
            return false;  // Stop further checks and return false
        }


                if (!c.getPosition().inWild() && !c.getItems().isWearingItem(Items.SNOWBALL, 3) && !TourneyManager.getSingleton().isInArena(c) && !WGManager.getSingleton().isInArena(c)) {
                    sendCheckMessage(c, sendMessages, "You are not in the wilderness.");
                    return false;
                }

                if (Configuration.COMBAT_LEVEL_DIFFERENCE && !c.getItems().isWearingItem(10501, 3)) {
                    if (c.getPosition().inWild()) {
                        int combatDif1 = c.getCombatLevelDifference(o);
                        if ((combatDif1 > c.wildLevel || combatDif1 > o.wildLevel)) {
                            sendCheckMessage(c, sendMessages, "Your combat level difference is too great to attack that player here.");
                            return false;
                        }
                    } else {
                        int myCB = c.combatLevel;
                        int pCB = o.combatLevel;
                        if ((myCB > pCB + 12) || (myCB < pCB - 12)) {
                            sendCheckMessage(c, sendMessages, "You can only fight players in your combat range!");
                            return false;
                        }
                    }
                }

                if (Configuration.SINGLE_AND_MULTI_ZONES) {
                    if (!o.getPosition().inMulti()) { // single combat zones
                        if ((o.underAttackByPlayer != c.getIndex() && o.underAttackByPlayer != 0 || o.underAttackByNpc > 0)) {
                            sendCheckMessage(c, sendMessages, "That player is already in combat.");
                            return false;
                        }
                        if (o.getIndex() != c.underAttackByPlayer && c.underAttackByPlayer != 0 || c.underAttackByNpc > 0) {
                            sendCheckMessage(c, sendMessages, "You are already in combat.");
                            return false;
                        }
                    }
                }

                if (c.connectedFrom.equals(o.connectedFrom) && !c.getRights().isOrInherits(Right.MODERATOR)
                        && !Server.isDebug()
                        && (!Boundary.isIn(c, Boundary.OUTLAST) || !Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)
                        || !Boundary.isIn(c, Boundary.FOREST_OUTLAST)
                        || !Boundary.isIn(c, Boundary.WG_Boundary)
                        || !Boundary.isIn(c, Boundary.SNOW_OUTLAST)
                        || !Boundary.isIn(c, Boundary.ROCK_OUTLAST)
                        || !Boundary.isIn(c, Boundary.FALLY_OUTLAST)
                        || !Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
                        || !Boundary.isIn(c, Boundary.SWAMP_OUTLAST))) {
                    sendCheckMessage(c, sendMessages, "You cannot attack same ip address.");
                    return false;
                }


                if (o.lastDefend != null && o.getPosition().inMulti()) {
                    Player def = (Player) o.lastDefend.get();
                    if (def != null && c != def && c.getIpAddress().equalsIgnoreCase(def.getIpAddress()) && (System.currentTimeMillis() - o.lastDefendTime) < 10_000) {
                        c.sendMessage("You can only attack " + o.getDisplayName() + " on one account.");
                        return false;
                    }
                }
                return true;
            }

        }

