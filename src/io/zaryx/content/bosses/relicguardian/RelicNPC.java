package io.zaryx.content.bosses.relicguardian;

import com.google.common.collect.Lists;
import io.zaryx.content.combat.npc.NPCAutoAttackBuilder;
import io.zaryx.model.Animation;
import io.zaryx.model.CombatType;
import io.zaryx.model.Graphic;
import io.zaryx.model.ProjectileBaseBuilder;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.util.Misc;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ynneh
 */
public class RelicNPC extends NPC {

    public static final int GROWTHLING = 8297;

    public RelicNPC(int npcId, Position position, Player spawnedBy) {
        super(npcId, position);
        setAttacks();
        this.spawnedBy = spawnedBy.getIndex();
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(6) == 2)//&& !isImmune(attack.getEntity().asPlayer()))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(20)
                        .setHitDelay(2)
                        .setAnimation(new Animation(-1))
                        .setMaxHit(45)
                        .setAttackDelay(2)
                        .setOnAttack(attack -> {
                            createGrowthlingAttack(attack.getVictim().asPlayer(), this);
                        })
                        .createNPCAutoAttack(),

                /**
                 * Magic attack
                 */
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(10)
                        .setHitDelay(4)
                        .setMaxHit(45)
                        .setAnimation(new Animation(-1))//magic attack
                        .setAttackDelay(6)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(139).setCurve(0).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(444, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                        })
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(444, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                        })
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(1)
                        .setHitDelay(2)
                        .setAnimation(new Animation(8946))
                        .setMaxHit(45)
                        .setAttackDelay(6)
                        .createNPCAutoAttack()

        ));
    }

    private void createGrowthlingAttack(Player player, NPC npc) {

        if (isDead())
            return;

        /**
         * Already summoned growthlings..
         */
        List<Position> possible_spawns = Arrays.asList(
                new Position(3024, 5783, getHeight()),
                new Position(3024, 5783, getHeight()),
                new Position(3024, 5783, getHeight()),
                new Position(3024, 5783, getHeight()),
                new Position(3018, 5783, getHeight()),
                new Position(3018, 5783, getHeight()),
                new Position(3018, 5783, getHeight()),
                new Position(3018, 5783, getHeight())
        );
        for (int i = 0; i < 3; i++) {
            NPC growthling = new Growthling(GROWTHLING, possible_spawns.get(Misc.random(possible_spawns.size() - 1)), player);
            player.getInstance().add(growthling);
            growthling.startGraphic(new Graphic(188));
            growthling.attackEntity(player);
        }
    }

    @Override
    public void process() {
        setAttacks();
        super.process();

    }

    @Override
    public boolean canBeAttacked(Entity entity) {
        if (this.spawnedBy != entity.getIndex()) {
            if (entity instanceof Player) {
                Player p = (Player) entity;
                if (p != null)
                    p.sendMessage(this.getName()+" isn't after you.");
            }
            return false;
        }
        return true;
    }

    public boolean isImmune(Player player) {
        List<NPC> npcs_in_area = player.getInstance().getNpcs();
        if (npcs_in_area == null)
            return false;
        return true;

    }

    @Override
    public boolean canBeDamaged(Entity entity) {
        if (entity instanceof NPC)
            return false;
        return true;
    }

    @Override
    public boolean isFreezable() {
        return false;
    }

}

