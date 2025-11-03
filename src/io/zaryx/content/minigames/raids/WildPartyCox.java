package io.zaryx.content.minigames.raids;

import io.zaryx.content.party.PartyInterface;
import io.zaryx.model.entity.player.Player;
import io.zaryx.content.party.PlayerParty;

public class WildPartyCox extends PlayerParty {

    public static final String TYPE = "WildCox Party";

    public WildPartyCox() {
        super(TYPE, 5);
    }

    @Override
    public boolean canJoin(Player invitedBy, Player invited) {
/*        if (Raids.isMissingRequirements(invited)) {
            invitedBy.sendMessage("That player doesn't have the requirements to play Chambers of Xeric.");
            return false;
        }*/
        if (invitedBy.connectedFrom.equals(invited.connectedFrom)) {
            invitedBy.sendErrorMessage("You can't use an alt with a main in the same region #Rules!");
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
