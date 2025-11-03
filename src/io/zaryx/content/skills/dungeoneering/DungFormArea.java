package io.zaryx.content.skills.dungeoneering;

import io.zaryx.content.party.PartyFormAreaController;
import io.zaryx.content.party.PlayerParty;
import io.zaryx.model.entity.player.Boundary;

import java.util.Set;

/**
 * Author @Kai
 * Discord - ZDS_KAI
 */

public class DungFormArea extends PartyFormAreaController {
    @Override
    public String getKey() {
        return DungParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(Boundary.DUNG_LOBBY_ENTRANCE);
    }

    @Override
    public PlayerParty createParty() {
        return new DungParty();
    }
}
