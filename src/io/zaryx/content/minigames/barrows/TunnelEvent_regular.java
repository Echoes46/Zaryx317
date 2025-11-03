package io.zaryx.content.minigames.barrows;


import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;

public class TunnelEvent_regular extends Event<Player> {

    public TunnelEvent_regular(String signature, Player attachment, int ticks) {
        super(signature, attachment, ticks);
    }

    @Override
    public void execute() {
        if (attachment == null) {
            super.stop();
            return;
        }
        if (!Boundary.isIn(attachment, Barrows.TUNNEL)) {
            stop();
            return;
        }

        attachment.getBarrows().drainPrayer();
    }

    @Override
    public void stop() {
        super.stop();
        if (attachment == null) {
            return;
        }
        attachment.getPA().sendFrame107();
    }

}
