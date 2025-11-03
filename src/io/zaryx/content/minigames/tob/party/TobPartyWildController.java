package io.zaryx.content.minigames.tob.party;

import io.zaryx.content.minigames.tob.TobConstants;
import io.zaryx.content.party.PartyFormAreaController;
import io.zaryx.content.party.PlayerParty;
import io.zaryx.model.entity.player.Boundary;

import java.util.Set;

public class TobPartyWildController extends PartyFormAreaController {

    @Override
    public String getKey() {
        return TobPartyWild.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(TobConstants.WILD_TOBPARTY);
    }

    @Override
    public PlayerParty createParty() {
        return new TobPartyWild();
    }
}
