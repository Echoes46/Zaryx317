package io.zaryx.model.cycleevent.impl.curses;


import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Player;

public class SapMage extends Event<Player> {

    public SapMage(String signature,Player attachment,int ticks) {
        super(signature,attachment,ticks);
    }
    @Override
    public void execute() {

        if (attachment == null || attachment.isDead || attachment.isDisconnected() || attachment.getSession() == null) {
            super.stop();
            return;
        }
        if (!attachment.prayerActive[CombatPrayer.SAP_MAGE]) {
            attachment.sapMagic= 10;

            super.stop();
            return;
        }
        if (attachment.underAttackByPlayer > 0 || attachment.underAttackByNpc > 0) {


            if (attachment.sapDefence < 20)
                attachment.sapDefence++;
            if (attachment.sapMagic < 20)
                attachment.sapMagic++;


        } else {
            attachment.sapMagic = 10;
            attachment.sapDefence = 10;
        }
    }

}

