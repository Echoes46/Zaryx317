package io.zaryx.content.bosses.nightmare.attack;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import io.zaryx.content.bosses.nightmare.Nightmare;
import io.zaryx.content.bosses.nightmare.NightmareAttack;
import io.zaryx.content.combat.npc.NPCAutoAttackBuilder;
import io.zaryx.content.combat.npc.NPCCombatAttack;
import io.zaryx.model.Animation;
import io.zaryx.model.CombatType;
import io.zaryx.model.ProjectileBase;
import io.zaryx.model.ProjectileBaseBuilder;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.util.Misc;

public class Husks extends NightmareAttack {

    @Override
    public void tick(Nightmare nightmare) {
        if (getTicks() == 0) {
            nightmare.requestTransform(9425);
            nightmare.startAnimation(new Animation(8605));
            sendHusks(nightmare);
        }

        if (getTicks() == 4) {
            stop();
        }
    }

    private void sendHusks(Nightmare nightmare) {
        players(nightmare).forEach(player -> {
            projectile().createTargetedProjectile(nightmare, player).send(nightmare.getInstance());

            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (!nightmare.isAlive() || !nightmare.isRegistered()) {
                        container.stop();
                        return;
                    }
                    Position last = new Position(0, 0, 0);
                    for (int count = 0; count < 2; count++) {
                        Husk husk = new Husk(last = player.getAdjacentPosition(last), nightmare);
                        husk.setPlayerAttackingIndex(player.getIndex());
                        husk.startAnimation(8567);
                    }
                    container.stop();
                }
            }, 5);
        });
    }

    private static ProjectileBase projectile() {
        return new ProjectileBaseBuilder()
                .setProjectileId(1781)
                .setStartHeight(90)
                .setSendDelay(4)
                .createProjectileBase();
    }

    private List<Player> players(Nightmare nightmare) {
        List<Player> players = Lists.newArrayList();
        players.add(nightmare.getInstance().getPlayers().get(Misc.trueRand(nightmare.getInstance().getPlayers().size())));

        for (Player player : nightmare.getInstance().getPlayers()) {
            if (!players.contains(player) && Misc.random(5) == 0) {
                players.add(player);
            }
        }

        return players;
    }

    private static class Husk extends NPC {

        public Husk(Position position, Nightmare nightmare) {
            super(9454, position);
            nightmare.getInstance().add(this);
            getHealth().setMaximumHealth(20);
            getHealth().setCurrentHealth(20);
            getBehaviour().setWalkHome(false);
            getBehaviour().setRespawn(false);
            setNpcAutoAttacks(Lists.newArrayList(
                    new NPCAutoAttackBuilder()
                            .setAnimation(new Animation(8565))
                            .setAttackDelay(4)
                            .setMaxHit(7)
                            .setHitDelay(1)
                            .setCombatType(CombatType.MELEE)
                            .setOnAttack(new Consumer<NPCCombatAttack>() {
                                @Override
                                public void accept(NPCCombatAttack npcCombatAttack) {
                                    npcCombatAttack.getVictim().asPlayer().freezeTimer = 4;
                                }
                            })
                            .createNPCAutoAttack()
            ));
        }

        @Override
        public int getDeathAnimation() {
            return 8566;
        }
    }
}
