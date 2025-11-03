package io.zaryx.model.cycleevent.impl.curses;


import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Player;

public class LeechEnergy extends Event<Player> {

    public LeechEnergy(String signature,Player attachment,int ticks) {
        super(signature,attachment,ticks);
    }
    @Override
    public void execute() {

        //  System.out.println("here execute");
        if (attachment == null || attachment.isDead || attachment.isDisconnected() || attachment.getSession() == null) {
            super.stop();
            return;
        }
        if (!attachment.prayerActive[CombatPrayer.LEECH_ENERGY]) {

            super.stop();
            return;
        }
        if (attachment.getRunEnergy() == 10000) {
            return;
        }
        attachment.setRunEnergy(attachment.getRunEnergy() + 100, true);
    }

}



