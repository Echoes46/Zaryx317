package io.zaryx.model.cycleevent.impl.curses;

import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Player;

public class LeechStrength extends Event<Player> {

    public LeechStrength(String signature,Player attachment,int ticks) {
        super(signature,attachment,ticks);
    }
    @Override
    public void execute() {

        if (attachment == null || attachment.isDead || attachment.isDisconnected() || attachment.getSession() == null) {
            super.stop();
            return;
        }
        if (!attachment.prayerActive[CombatPrayer.LEECH_STRENGTH]) {
            attachment.leechStrength= 0;
            attachment.leechStrength_enemy= 0;
            attachment.getPA().sendString(99683,"0%");
            super.stop();
            return;
        }
        if (attachment.leechStrength < 10)
            attachment.leechStrength++;
        if (attachment.underAttackByPlayer > 0 || attachment.underAttackByNpc > 0) {


            if (attachment.leechStrength_enemy < 25)
                attachment.leechStrength_enemy++;


        }
        else {
            attachment.leechStrength_enemy = 10;
        }

        attachment.getPA().sendString(99683,attachment.leechStrength+"%");
    }

}


