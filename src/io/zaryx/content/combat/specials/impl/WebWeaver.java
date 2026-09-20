package io.zaryx.content.combat.specials.impl;

import io.zaryx.Server;
import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.content.skills.Skill;
import io.zaryx.model.CombatType;
import io.zaryx.model.cycleevent.DelayEvent;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerAssistant;
import io.zaryx.util.Misc;

import static io.zaryx.content.WeaponGames.WGModes.max;

public class WebWeaver extends Special {
    public WebWeaver() {
        super(5, 4.0, 1.0, new int[]{27655});
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(9964);
        player.gfx0(2354);

        // Run delayed combat visuals on the game event loop. Creating a JVM thread
        // for every special attack can exhaust the server during group combat.
        Server.getEventHandler().submit(new DelayEvent(2) {
            @Override
            public void onExecute() {
                if (target == null || !target.isRegistered() || !player.sameInstance(target)) {
                    return;
                }
                if (target.isPlayer()) {
                    target.asPlayer().gfx0(2355);
                } else if (target.isNPC()) {
                    target.asNPC().gfx0(2355);
                }
            }
        });

        if (damage.getAmount() == 0) {
            int second = Misc.random(0, max);
            if (second == 0) {
                doHit(player, target, 0, 0);
                doHit(player, target, (int) (max * 0.75d), 1);
                doHit(player, target, (int) (max * 0.75d), 1);
            } else {
                doHit(player, target, second, 0);
                doHit(player, target, second / 2, 1);
                doHit(player, target, second / 2, 1);
            }
        } else {
            int halvedHit = damage.getAmount() == 0 ? 0 : damage.getAmount() / 2;
            int finalHit = halvedHit == 0 ? 0 : halvedHit / 2;
            doHit(player, target, halvedHit, 0);
            doHit(player, target, finalHit, 1);
            doHit(player, target, finalHit, 1);
        }
    }

    private void doHit(Player player, Entity target, int damage, int delay) {
        player.getDamageQueue().add(new Damage(target, damage, player.hitDelay + delay, player.playerEquipment, damage > 0 ? Hitmark.HIT : Hitmark.MISS, CombatType.RANGE));
        player.getPA().addXpDrop(new PlayerAssistant.XpDrop(damage, Skill.ATTACK.getId()));
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {
    }
}
