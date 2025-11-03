package io.zaryx.model.cycleevent.impl.curses;

import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Player;

public class LeechDefence extends Event<Player> {

    public LeechDefence(String signature,Player attachment,int ticks) {
        super(signature,attachment,ticks);
    }
    @Override
    public void execute() {

        //  System.out.println("here execute");
        if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
            super.stop();
            return;
        }
            if (!attachment.prayerActive[CombatPrayer.LEECH_DEFENCE]) {
            attachment.leechDefence= 0;
            attachment.leechDefence_enemy= 0;
            attachment.getPA().sendString(99684,"0%");
            super.stop();
            return;
        }
        if (attachment.leechDefence < 10)
            attachment.leechDefence++;
        if (attachment.underAttackByPlayer > 0 || attachment.underAttackByNpc > 0) {


            if (attachment.leechDefence_enemy < 25)
                attachment.leechDefence_enemy++;

        }
        else {

            attachment.leechDefence_enemy = 10;
        }
        attachment.getPA().sendString(99684,attachment.leechDefence+"%");
    }

}

