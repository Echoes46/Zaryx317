package io.zaryx.content.combat.npc;

import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;

import static io.zaryx.content.bosses.nightmare.NightmareStatusNPC.npc;

public class PlayerCombatAttack {

    private final Player player;
    private final Entity victim;

    public PlayerCombatAttack(Player player, Entity victim) {
        this.player = player;
        this.victim = getNpc();
    }

    public NPC getNpc() {
        return npc;
    }

    public Entity getVictim() {
        return victim;
    }

}
