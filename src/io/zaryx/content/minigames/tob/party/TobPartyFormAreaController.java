package io.zaryx.content.minigames.tob.party;

import io.zaryx.content.minigames.tob.TobConstants;
import io.zaryx.content.party.PartyFormAreaController;
import io.zaryx.content.party.PlayerParty;
import io.zaryx.model.entity.player.Boundary;

import java.util.Set;

public class TobPartyFormAreaController extends PartyFormAreaController {

    @Override
    public String getKey() {
        return TobParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(TobConstants.TOB_LOBBY);
    }

    @Override
    public PlayerParty createParty() {
        return new TobParty();
    }
}
