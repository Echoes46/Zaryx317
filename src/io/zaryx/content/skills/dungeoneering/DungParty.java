package io.zaryx.content.skills.dungeoneering;

import io.zaryx.content.party.PartyInterface;
import io.zaryx.content.party.PlayerParty;
import io.zaryx.model.entity.player.Player;

/**
 * Author @Kai
 * Discord - ZDS_KAI
 */

public class DungParty extends PlayerParty {

    public static String TYPE = "Dung Party";

    public DungParty() {
        super(TYPE,100);
    }
    @Override
    public boolean canJoin(Player invitedBy, Player invited) {
        if (invitedBy.connectedFrom.equals((invited.connectedFrom))) {
            invited.sendMessage("You can't use an alt with a main account");
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
