package io.zaryx.content.bosses.kratos;

import io.zaryx.Server;
import io.zaryx.content.combat.npc.NPCAutoAttack;
import io.zaryx.content.combat.npc.NPCAutoAttackBuilder;
import io.zaryx.content.combat.npc.NPCCombatAttack;
import io.zaryx.content.combat.npc.NPCCombatAttackHit;
import io.zaryx.model.Animation;
import io.zaryx.model.CombatType;
import io.zaryx.model.ProjectileBase;
import io.zaryx.model.ProjectileBaseBuilder;
import io.zaryx.model.entity.EntityReference;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.ClientGameTimer;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.world.objects.GlobalObject;
import io.zaryx.util.Misc;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SeldaehWeb implements Function<SeldaehNpc, NPCAutoAttack> {

    private static Position[] CORNER_AREAS = {

    };

    private static ProjectileBase projectile() {
        return new ProjectileBaseBuilder()
                .setSendDelay(1)
                .setSpeed(75)
                .setCurve(0)
                .setStartHeight(75)
                .setEndHeight(50)
                .setProjectileId(1687)
                .createProjectileBase();
    }

    @Override
    public NPCAutoAttack apply(SeldaehNpc nightmare) {
        Consumer<NPCCombatAttackHit> freeze = t -> {
            if (t.getCombatHit().missed())
                return;
            if (!t.getVictim().isFreezable())
                return;
            t.getVictim().attackTimer += 6;
            t.getVictim().freezeTimer = 6;
            t.getVictim().resetWalkingQueue();
            t.getVictim().frozenBy = EntityReference.getReference(t.getNpc());
            if (t.getVictim().isPlayer()) {
                Player p = (Player) t.getVictim();
                ((Player) t.getVictim()).sendMessage("uh oh, youre stuck.");
                ((Player) t.getVictim()).getPA().sendGameTimer(ClientGameTimer.FREEZE, TimeUnit.MILLISECONDS, 600 * 6);
                GlobalObject ground_web = new GlobalObject(34895, p.getX(), p.getY(), p.heightLevel, 2, 10, 5, -1);
                Server.getGlobalObjects().add(ground_web);
            }
            if (nightmare.nextWalk == null) {
                nightmare.nextWalk = CORNER_AREAS[Misc.random(CORNER_AREAS.length - 1)];
            }
            nightmare.resetAttack();
            nightmare.getBehaviour().setAggressive(false);
            nightmare.randomWalk = false;
        };
        Consumer<NPCCombatAttack> hiss = t -> {
            Player p = (Player) t.getVictim();
            t.getNpc().forceChat("Fear me, Now and Forever!");
            nightmare.attackCounter = 0;
        };
        List<Player> players = NPCAutoAttack.getPlayers(nightmare);
        return new NPCAutoAttackBuilder()
                .setSelectPlayersForMultiAttack(new Function<>() {
                    @Override
                    public List<Player> apply(NPCCombatAttack npcCombatAttack) {
                        return players.stream().filter(plr -> Boundary.isIn(plr, Boundary.KRATOS_AREA))
                                .collect(Collectors.toList());
                    }
                })
                .setSelectAutoAttack(new Function<NPCCombatAttack, Boolean>() {
                    @Override
                    public Boolean apply(NPCCombatAttack npcCombatAttack) {
                        return nightmare.attackCounter >= 4;
                    }
                })
                .setAnimation(new Animation(4922))
                .setCombatType(CombatType.SPECIAL)
                .setAttackDelay(4)
                .setHitDelay(3)
                .setDistanceRequiredForAttack(6)
                .setMultiAttack(true)
                .setOnAttack(
                        hiss
                )
                .setOnHit(freeze
                )
                .setProjectile(projectile())
                .createNPCAutoAttack();
    }
}