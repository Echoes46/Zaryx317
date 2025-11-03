package io.zaryx.model.cycleevent.impl.curses;

import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Player;

public class LeechRange extends Event<Player> {

    public LeechRange(String signature,Player attachment,int ticks) {
        super(signature,attachment,ticks);
    }
    @Override
    public void execute() {

        if (attachment == null || attachment.isDead || attachment.isDisconnected() || attachment.getSession() == null) {
            super.stop();
            return;
        }
        if (!attachment.prayerActive[CombatPrayer.LEECH_RANGED]) {
            attachment.leechRanged= 0;
            attachment.leechRanged_enemy= 0;
            attachment.getPA().sendString(99685,"0%");
            super.stop();
            return;
        }
        if (attachment.leechRanged < 10)
            attachment.leechRanged++;
        if (attachment.underAttackByPlayer > 0 || attachment.underAttackByNpc > 0) {


            if (attachment.leechRanged_enemy < 25)
                attachment.leechRanged_enemy++;


        } else {

            attachment.leechRanged_enemy = 10;
        }
        attachment.getPA().sendString(99685,attachment.leechRanged+"%");
    }

}

