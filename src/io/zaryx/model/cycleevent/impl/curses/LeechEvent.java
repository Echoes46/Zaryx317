package io.zaryx.model.cycleevent.impl.curses;
import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Player;

///Increases Attack by 5%, gradually increasing to 10%.
//Drains opponent's Attack by 10%, gradually increasing to 25%.
public class LeechEvent extends Event<Player> {

    public LeechEvent(String signature,Player attachment,int ticks) {
        super(signature,attachment,ticks);
    }
    @Override
    public void execute() {

        if (attachment == null || attachment.isDead || attachment.isDisconnected() || attachment.getSession() == null) {
            super.stop();
            return;
        }
        if (!attachment.prayerActive[CombatPrayer.LEECH_ATTACK]) {
            attachment.leechAttack= 0;
            attachment.leechAttack_enemy= 0;
            attachment.getPA().sendString(99682,"0%");
            super.stop();
            return;
        }
        if (attachment.leechAttack < 10)
            attachment.leechAttack++;
        if (attachment.underAttackByPlayer > 0 || attachment.underAttackByNpc > 0) {

            if (attachment.leechAttack_enemy < 25)
                attachment.leechAttack_enemy++;


        } else {
            attachment.leechAttack = 5;
            attachment.leechAttack_enemy = 10;
        }
        attachment.getPA().sendString(99682,attachment.leechAttack+"%");
    }

}
