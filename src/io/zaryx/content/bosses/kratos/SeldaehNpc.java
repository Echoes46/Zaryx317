package io.zaryx.content.bosses.kratos;

import com.google.common.collect.Lists;
import io.zaryx.model.Npcs;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.HealthStatus;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Position;




public class SeldaehNpc extends NPC {

    public static final Position SPAWN_POSITION =  new Position(3170, 5206, 0);
    public static boolean spawned = false;
    private boolean minionSpawn;
    public int attackCounter;
    public Position nextWalk;

    public SeldaehNpc(int id, Position pos) {
        super(id, pos);
        resetForSpawn();
    }

    public void resetForSpawn() {
        walkingType = 1;
        nextWalk = null;
        attackCounter = 0;
        getBehaviour().setWalkHome(false);
        getBehaviour().setAggressive(true);
        if (getNpcId() == Npcs.SELDAEH) {
            getBehaviour().setRespawn(true);
        } else {
            getBehaviour().setRespawn(false);
        }
        if (getNpcId() == 2482) {
            setNpcAutoAttacks(Lists.newArrayList(
                    new SeldaehMinionMelee().apply(this)
            ));
        } else if (getNpcId() == 7021) {
            setNpcAutoAttacks(Lists.newArrayList(
                    new SeldaehMinionMage().apply(this)
            ));
        } else {
            setNpcAutoAttacks(Lists.newArrayList(
                    new SeldaehMelee().apply(this),
                    new SeldaehRanged().apply(this)
            ));
        }
    }

    @Override
    public boolean susceptibleTo(HealthStatus status) {
        return false;
    }

    @Override
    public boolean canBeDamaged(Entity entity) {
        return !isDead();
    }

    @Override
    public boolean canBeAttacked(Entity entity) {
        return !isDead();
    }

    @Override
    public boolean isAutoRetaliate() {
        return getBehaviour().isAggressive() && !isDead() && nextWalk == null;
    }

    @Override
    public void process() {
        try {
            if (this.getNpcId() == Npcs.SELDAEH) {
                if (attackCounter >= 5) {
                    attackCounter = 0;
                    int p = getPlayerAttackingIndex();
                    if (PlayerHandler.players[p] != null) {
                        Player player = PlayerHandler.players[p];
                        this.attack(player, new SeldaehWeb().apply(this));
                    }
                }
                if (this.getHealth().getCurrentHealth() <= (this.getHealth().getMaximumHealth() * 0.66) && !minionSpawn ||
                        this.getHealth().getCurrentHealth() <= (this.getHealth().getMaximumHealth() * 0.33) && !minionSpawn) {
                    minionSpawn = true;
                    spawnMinions();
                }
                if (nextWalk != null) {
                    if (getPosition().equals(nextWalk)) {
                        nextWalk = null;
                        getBehaviour().setRunnable(false);
                        getBehaviour().setAggressive(true);
                    } else {
                        getBehaviour().setRunnable(true);
                        moveTowards(nextWalk.getX(),nextWalk.getY(),false);
                    }
                }
            }
            processCombat();
        } catch (Exception e) {
            e.printStackTrace();
            unregister();
        }
    }

    private void processCombat() {
        super.process();
    }

    @Override
    public NPC provideRespawnInstance() {
        NPC boss = new SeldaehNpc(Npcs.SELDAEH, SPAWN_POSITION);
        boss.getBehaviour().setAggressive(true);
        return boss;
    }

    @Override
    public void onDeath() {
        super.onDeath();
        if( this.getNpcId() == 8888 && this.getNpcId() == 2482 && this.getNpcId() == 7021){
            if (minionSpawn && minionsDead()) {
                minionSpawn = false;
            }
        } else {
            resetForSpawn();
            minionSpawn = false;
            despawnMinions();
        }
    }

    private void spawnMinions() {
        if (minionSpawn) {
            new SeldaehNpc(2482, SPAWN_POSITION);
            new SeldaehNpc(7021, SPAWN_POSITION);
        }
    }

    private void despawnMinions() {
        NPCHandler.despawn(2482, getHeight());
        NPCHandler.despawn(7021, getHeight());
    }

    private boolean minionsDead() {
        return NPCHandler.getNpc(2482, getHeight()) == null || NPCHandler.getNpc(7021, getHeight()) == null;
    }
}
