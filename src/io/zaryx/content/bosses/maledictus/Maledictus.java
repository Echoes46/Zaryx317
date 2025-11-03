package io.zaryx.content.bosses.maledictus;

import io.zaryx.Configuration;
import io.zaryx.content.achievement.AchievementType;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.content.bosses.hydra.CombatProjectile;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.scripts.CombatantNpc;
import io.zaryx.content.event.eventcalendar.EventChallenge;
import io.zaryx.content.events.monsterhunt.MonsterHunt;
import io.zaryx.content.leaderboards.LeaderboardType;
import io.zaryx.content.leaderboards.LeaderboardUtils;
import io.zaryx.model.CombatType;
import io.zaryx.model.Graphic;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.EntityReference;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.*;
import io.zaryx.util.Misc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static io.zaryx.Server.npcHandler;
import static io.zaryx.content.bosses.maledictus.MagicAttackStyle.*;
import static io.zaryx.model.CombatType.MAGE;
import static io.zaryx.model.CombatType.RANGE;

public class Maledictus extends CombatantNpc {

    private static final CombatProjectile STANDARD_ATTACK_PROJECTILE = new CombatProjectile(2004, 50, 25, 4, 50, 0, 50);
    private static final CombatProjectile BLOOD_ATTACK_PROJECTILE = new CombatProjectile(374, 50, 25, 4, 50, 0, 50);
    private static final CombatProjectile ICE_ATTACK_PROJECTILE = new CombatProjectile(2012, 50, 25, 4, 50, 0, 50);
    private static final CombatProjectile SMOKE_ATTACK_PROJECTILE = new CombatProjectile(2010, 50, 25, 4, 50, 0, 50);

    private static final List<CombatProjectile> combatProjectiles = Arrays.asList(
            STANDARD_ATTACK_PROJECTILE,
            BLOOD_ATTACK_PROJECTILE,
            ICE_ATTACK_PROJECTILE,
            SMOKE_ATTACK_PROJECTILE
    );

    private static void attackPlayer(NPC maledictus) {
        Player target = getTarget(maledictus);
        if (target.getPosition().getAbsDistance(maledictus.getPosition().getCenterPosition(maledictus.getSize())) <= 2) {
            // Melee attack
            maledictus.startAnimation(9277);
            int dmg = Misc.random(120);
            if (target.protectingMelee()) {
                dmg /= 2;
            }
            if (successfulHit(maledictus, target, CombatType.MELEE)) {
                target.appendDamage(dmg, dmg > 0 ? Hitmark.HIT : Hitmark.MISS);
            }
            return;
        }
        // Build a projectile to fire to that location
        CombatProjectile randomProjectile = Misc.random(combatProjectiles);
        CombatProjectile projectile = Objects.nonNull(randomProjectile) ? randomProjectile : STANDARD_ATTACK_PROJECTILE;
        // build the end Graphic
        Graphic graphic = getProjectileEndTargetGraphic(projectile);
        // define the attackStyle
        MagicAttackStyle attackStyle = getAttackStyleFromProjectile(projectile);
        // start the attack animation
        int animation = 9277;
        if (attackStyle == ICE) {
            animation = 9278;
        } else if (attackStyle == BLOOD) {
            animation = 9279;
        } else if (attackStyle == SMOKE) {
            animation = 9282;
        }
        maledictus.startAnimation(animation);
        // TODO: send projectile from npc to target
        sendCombatProjectile(maledictus, projectile);
        // kick off the event to delay a hit
        CycleEventHandler.getSingleton().addEvent(target,
                getMaledictusAttackEvent(maledictus, attackStyle, graphic),
                maledictus.getCombatDefinition().getAttackSpeed());
    }

    public static void rewardPlayers() {
        MonsterHunt.monsterKilled = System.currentTimeMillis();
        MonsterHunt.spawned = false;
        PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.WILDERNESS))
                .forEach(p -> {
                    if (p.getMaledictusDamageCounter() >= 80) {
                        p.sendMessage("@blu@You receive a @red@key@blu@ for doing enough damage to the boss!");
                        p.getItems().addItemUnderAnyCircumstance(6792, 2);
                        if (p.hasFollower && (p.petSummonId == 30123)) {
                            if (Misc.random(100) < 25) {
                                p.getItems().addItemUnderAnyCircumstance(6792, 2);
                                p.sendMessage("Your pet provided 2 extra keys!");
                            }
                        }
                        if ((Configuration.DOUBLE_DROPS_TIMER > 0 || Configuration.DOUBLE_DROPS)) {
                            p.getItems().addItemUnderAnyCircumstance(6792, 2);
                            p.sendMessage("[WOGW] Double drops is activated and you received 2 extra keys!");
                        }
                        p.getEventCalendar().progress(EventChallenge.OBTAIN_X_WILDY_EVENT_KEYS);
                        LeaderboardUtils.addCount(LeaderboardType.WILDY_EVENTS, p, 1);
                        Achievements.increase(p, AchievementType.WILDY_EVENT, 1);
                        p.setMaledictusDamageCounter(0);
                    } else {
                        p.sendMessage("@blu@You didn't do enough damage to the boss to receive a reward.");
                        p.setMaledictusDamageCounter(0);
                    }

                });
    }

    private static CycleEvent getMaledictusAttackEvent(NPC maledictus, MagicAttackStyle attackStyle, Graphic graphic) {
        return new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                Player target = getTarget(maledictus);
                boolean isMagicAttack = Misc.random(1) == 0;
                int damage = Misc.random(72);
                if (isMagicAttack && target.protectingMagic() && getTarget(maledictus) != null) {
                    damage /= 2;
                }
                if (!isMagicAttack && target.protectingRange() && getTarget(maledictus) != null) {
                    damage /= 2;
                }

                if (successfulHit(maledictus, target, isMagicAttack ? MAGE : RANGE) && damage > 0) {
                    switch (attackStyle) {
                        case ICE:
                            if (target.isFreezable() && target.freezeDelay <= 0 && target.freezeTimer <= 0) {
                                int delay = Misc.random(15, 30);
                                target.frozenBy = EntityReference.getReference(target);
                                target.freezeDelay = delay;
                                target.freezeTimer = delay;
                                target.resetWalkingQueue();
                                target.sendMessage("You have been frozen.");
                                target.getPA().sendGameTimer(ClientGameTimer.FREEZE, TimeUnit.MILLISECONDS, 600 * delay);
                            }
                            break;
                        case BLOOD:
                            int healDamage = damage / 2;
                            maledictus.appendHeal(healDamage, Hitmark.HEAL_PURPLE);
                            target.sendMessage("The Maledictus drains your health for his own...");
                            break;
                        default:
                            break;
                    }
                    target.startGraphic(graphic);
                    target.appendDamage(damage, Hitmark.HIT);
                }
                container.stop();
            }
        };
    }

    private static Graphic getProjectileEndTargetGraphic(CombatProjectile projectile) {
        switch (projectile.getGfx()) {
            case 374:
                return new Graphic(377, Graphic.GraphicHeight.LOW); // Blood
            case 2004:
                return new Graphic(-1, Graphic.GraphicHeight.HIGH);
            case 2010:
                return new Graphic(2011, Graphic.GraphicHeight.LOW); // Smoke
            case 2012:
                return new Graphic(369, Graphic.GraphicHeight.LOW); // Ice
            default:
                return new Graphic(85, Graphic.GraphicHeight.MIDDLE); // Splash
        }
    }

    private static MagicAttackStyle getAttackStyleFromProjectile(CombatProjectile projectile) {
        switch (projectile.getGfx()) {
            case 374:
                return BLOOD; // Blood
            case 2004:
            case 2012:
                return ICE; // Ice
            case 2010:
                return SMOKE; // Smoke
            default:
                throw new IllegalStateException("Unexpected value: " + projectile.getGfx());
        }
    }

    public static void handleAttack(NPC maledictus) {
        maledictus.attackTimer = maledictus.getCombatDefinition().getAttackSpeed();
        Player target = getTarget(maledictus);
        if (target.getPosition().getAbsDistance(maledictus.getPosition()) <= 16) {
            attackPlayer(maledictus);
        } else {
            npcHandler.followPlayer(maledictus, maledictus.getPlayerAttackingIndex());
        }

        if (maledictus.isDead()) {
            maledictus.applyDead = true;
            maledictus.absX = maledictus.makeX;
            maledictus.absY = maledictus.makeY;
            maledictus.getHealth().reset();
            maledictus.startAnimation(0x328);
            maledictus.getRegionProvider().removeNpcClipping(maledictus);
        } else {
            maledictus.processMovement();
        }
    }
}
