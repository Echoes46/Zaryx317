package io.zaryx.content.combat.specials.impl;
import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.npc.stats.NpcCombatDefinition;
import io.zaryx.model.entity.npc.stats.NpcCombatSkill;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import java.util.ArrayList;
import java.util.List;

public final class ShieldBash extends Special {
    public ShieldBash() { super(5.0, 1.2, 1.0, new int[]{21015,28682,25604}); }
    @Override public void activate(Player p, Entity target, Damage damage) { p.startAnimation(7512); }
    /** Do not turn an NPC special into an unrequested PvP attack. */
    public static List<Entity> secondaryTargets(Player p, Entity primary) {
        List<Entity> result = new ArrayList<>();
        if (!p.getPosition().inMulti() || !primary.getPosition().inMulti()) return result;
        Entity[] candidates = primary.isNPC() ? NPCHandler.npcs : PlayerHandler.players;
        for (Entity e : candidates) {
            if (e == null || e == p || e == primary || e.isDead || e.getHealth().getCurrentHealth() <= 0
                    || !e.isRegistered() || e.getInstance() != p.getInstance() || e.getHeight() != p.getHeight()
                    || !e.getPosition().inMulti() || Math.abs(e.getX()-p.getX()) > 5 || Math.abs(e.getY()-p.getY()) > 5) continue;
            if (p.attacking.attackEntityCheck(e, false)) result.add(e);
            if (result.size() == 9) break;
        }
        return result;
    }
    /** Melee is compared as the average of Attack and Strength. Ties prefer melee. */
    public static int[] drain(int attack, int strength, int ranged, int magic) {
        int[] levels = {attack,strength,ranged,magic};
        if ((long)attack + strength >= 2L * Math.max(ranged,magic)) {
            levels[0] -= attack / 20; levels[1] -= strength / 20;
        } else if (ranged >= magic) levels[2] -= ranged / 20;
        else levels[3] -= magic / 20;
        return levels;
    }
    @Override public void hit(Player p, Entity target, Damage damage) {
        if (target.isPlayer()) {
            Player t = target.asPlayer(); int[] skills = {0,2,4,6};
            int[] reduced = drain(t.playerLevel[0],t.playerLevel[2],t.playerLevel[4],t.playerLevel[6]);
            for (int i=0;i<skills.length;i++) { t.playerLevel[skills[i]]=reduced[i]; t.getPA().refreshSkill(skills[i]); }
        } else {
            NpcCombatDefinition d = target.asNPC().getCombatDefinition();
            if (d == null) return;
            NpcCombatSkill[] skills = {NpcCombatSkill.ATTACK,NpcCombatSkill.STRENGTH,NpcCombatSkill.RANGE,NpcCombatSkill.MAGIC};
            int[] reduced = drain(d.getLevel(skills[0]),d.getLevel(skills[1]),d.getLevel(skills[2]),d.getLevel(skills[3]));
            for (int i=0;i<skills.length;i++) d.setLevel(skills[i],reduced[i]);
        }
    }
}
