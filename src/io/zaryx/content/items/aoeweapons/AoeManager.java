package io.zaryx.content.items.aoeweapons;

import io.zaryx.content.combat.Hitmark;
import io.zaryx.model.Graphic;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

import java.util.Arrays;
import java.util.Iterator;

public class AoeManager {

    public static boolean canAOE(Player player) {

        return Boundary.isIn(player, Boundary.AOEInstance) &&
                Arrays.stream(AoeWeapons.values()).anyMatch(i -> i.ID == player.playerEquipment[Player.playerWeapon]);
    }

    public static void castAOE(Player player, Entity victim) {
        if (!canAOE(player)) {
            player.sendMessage("You cannot do this here.");
            player.attacking.reset();
            return;
        }

        AoeWeapons aoeData = AOESystem.getSingleton().getAOEData(player.playerEquipment[Player.playerWeapon]);
        if (aoeData != null) {

            int dmg = Misc.random(aoeData.DMG);
            int range = aoeData.Size;
            int delay = aoeData.Delay;
            int anim = aoeData.anim;
            int gfx = aoeData.gfx;

            player.startAnimation(anim);

            Iterator<NPC> it;
            if (player.isPlayer() && victim.isNPC()) {
//                it = Arrays.stream(NPCHandler.npcs).filter(i -> i.getPosition().withinDistance(player.getPosition(), range)).iterator();
                for (NPC next : NPCHandler.npcs) {
                    if (next != null) {
                        if (player.getPosition().withinDistance(next.getPosition(), range) && next.getHealth().getCurrentHealth() > 0) {
                            if (next.isNPC() && next.getHealth().getCurrentHealth() <= 0 && next.isDead()) {
                                continue;
                            }
                            if (victim != next) {
                                victim.startGraphic(new Graphic(gfx, 0, Graphic.GraphicHeight.MIDDLE));
                                int calc = Misc.random(dmg);
                                victim.appendDamage(calc, (calc > 0 ? Hitmark.HIT : Hitmark.MISS));
                            }
//                            RangeData.fireProjectileNpc(player, next.asNPC(), 50, 70, gfx, 43, 31, 37, 10);
                            next.startGraphic(new Graphic(gfx, 0, Graphic.GraphicHeight.MIDDLE));
                            int calc = Misc.random(dmg);
                            next.appendDamage(calc, (calc > 0 ? Hitmark.HIT : Hitmark.MISS));
                            next.attackEntity(player);
                            player.attackTimer = delay;
                        }
                    }
                }
            }
        }
    }
}