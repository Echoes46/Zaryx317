package io.zaryx.content.minigames.arbograve.party;

import io.zaryx.content.party.PartyFormAreaController;
import io.zaryx.content.party.PlayerParty;
import io.zaryx.model.entity.player.Boundary;

import java.util.Set;

public class ArbogravePartyWildController extends PartyFormAreaController {
    @Override
    public String getKey() {
        return ArbogravePartyWild.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(Boundary.WILD_ARBO);
    }

    @Override
    public PlayerParty createParty() {
        return new ArbogravePartyWild();
    }
}
