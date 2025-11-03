package io.zaryx.content.minigames.arbograve.party;

import io.zaryx.content.party.PartyInterface;
import io.zaryx.content.party.PlayerParty;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.ExpMode;
import io.zaryx.model.entity.player.mode.group.ExpModeType;

public class ArbograveParty extends PlayerParty {

    public static final String TYPE = "Arbo Party";

    public ArbograveParty() {
        super(TYPE, 4);
    }

    @Override
    public boolean canJoin(Player invitedBy, Player invited) {
        if (invitedBy.connectedFrom.equals(invited.connectedFrom)) {
            invitedBy.sendErrorMessage("You can't use an alt with a main in the same region #Rules!");
            return false;
        }

        if (invited.totalLevel < 1500 &&
                !invited.getExpMode().equals(new ExpMode(ExpModeType.OneTimes)) &&
                !invited.getExpMode().equals(new ExpMode(ExpModeType.FiveTimes))) {
            invited.sendMessage("You need a total level of at least 1500 to join this raid!");
            return false;
        } else if (invited.totalLevel < 1500 &&
                (invited.getExpMode().equals(new ExpMode(ExpModeType.OneTimes)) ||
                invited.getExpMode().equals(new ExpMode(ExpModeType.FiveTimes)))) {
            invited.sendMessage("You need a total level of at least 1500 to join this raid!");
            return false;
        }

        return true;
    }

    @Override
    public void onJoin(Player player) {
        PartyInterface.refreshOnJoinOrLeave(player, this);
    }

    @Override
    public void onLeave(Player player) {
        PartyInterface.refreshOnJoinOrLeave(player, this);
    }
}
