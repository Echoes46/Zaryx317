package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.CombatType;
import io.zaryx.model.Graphic;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;

public class AncientGodsword extends Special {

    public AncientGodsword() {
        super(5.0, 1.0, 1.1, new int[] { 26233 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(9171);
        player.gfx0(1996);
        if (damage.getAmount() > 0) {
            if (target.isPlayer()) {
                target.asPlayer().sendMessage("You have been hit by @red@Blood Sacrifice!");
            }
            CycleEventHandler.getSingleton().addEvent(target, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (container.getTotalExecutions() == 5) {
                        int damage2 = 25;
                        player.getDamageQueue().add(new Damage(target, damage2, 2, player.playerEquipment, Hitmark.HIT, CombatType.MAGE));
                        player.getHealth().increase(25);
                        target.startGraphic(new Graphic(2003, 0, Graphic.GraphicHeight.MIDDLE));
                        container.stop();
                    }
                }
            }, 1);
        }
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }

}