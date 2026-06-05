package io.zaryx.model.entity.player.packets;

import io.zaryx.Configuration;
import io.zaryx.content.combat.stats.MonsterKillLog;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.PacketType;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.multiplayersession.flowerpoker.FlowerPoker;

import java.util.Objects;

/**
 * Updated by Khaos
 */
public class PlayerOptionsHandler implements PacketType {

    @Override
    public void processPacket(Player player, int opCode, int opSize) {

        if (player.getMovementState().isLocked() || player.getLock().cannotInteract(player)) {
            return;
        }

        if (player.isFping()) {
            return;
        }

        player.interruptActions();

        int index = player.getInStream().readUnsignedWord();

        if (index >= PlayerHandler.players.length || index < 0) {
            return;
        }

        Player requested = PlayerHandler.players[index];

        if (Objects.isNull(requested)) {
            return;
        }

        if (player.getBankPin().requiresUnlock()) {
            player.getBankPin().open(2);
            return;
        }

        if (requested.getBankPin().requiresUnlock()) {
            return;
        }

        if (player.getInterfaceEvent().isActive()) {
            player.sendMessage("Please finish what you're doing.");
            return;
        }

        if (requested.getInterfaceEvent().isActive()) {
            player.sendMessage("That player is busy right now.");
            return;
        }

        player.faceEntity(requested);

        switch (opCode) {

            case 128:
                /*
                 * Duel Challenge / Flower Poker / Player Option
                 */

                /*
                 * Duel lobby challenge area.
                 *
                 * The last Boundary.DUEL_ARENA entry is being used as the lobby.
                 * Players should be able to challenge each other here.
                 */
                if (isInDuelLobby(player) || isInDuelLobby(requested)) {

                    if (!isInDuelLobby(player)) {
                        player.sendMessage("You must be in the duel arena lobby to challenge this player.");
                        return;
                    }

                    if (!isInDuelLobby(requested)) {
                        player.sendMessage("That player must be in the duel arena lobby to be challenged.");
                        return;
                    }

                    if (!Configuration.NEW_DUEL_ARENA_ACTIVE) {
                        player.getDH().sendStatement(
                                "@red@Dueling Temporarily Disabled",
                                "The duel arena minigame is currently being rewritten.",
                                "No player has access to this minigame during this time.",
                                "",
                                "Thank you for your patience, Dev Khaos."
                        );
                        player.nextChat = -1;
                        return;
                    }

                    if (player.getDuel().requestable(requested)) {
                        player.getDuel().request(requested);
                    }
                    return;
                }

                /*
                 * Flower Poker.
                 */
                if (Boundary.isIn(player, FlowerPoker.BOUNDARIES)) {
                    if (Boundary.isIn(requested, FlowerPoker.BOUNDARIES)) {
                        if (player.getFlowerPokerRequest().requestable(requested)) {
                            player.getFlowerPokerRequest().request(requested);
                        }
                    }
                    return;
                }

                /*
                 * Monster Kill Log / Player option fallback.
                 */
                if (MonsterKillLog.onPlayerOption(player, requested, "PlayerOptions")) {
                    return;
                }

                return;
        }
    }

    /**
     * The final Duel Arena boundary is used as the lobby/challenge area.
     */
    private boolean isInDuelLobby(Player player) {
        return Boundary.isIn(player, getDuelLobbyBoundary());
    }

    /**
     * Uses the last boundary in the DUEL_ARENA array as the lobby.
     */
    private Boundary getDuelLobbyBoundary() {
        return Boundary.DUEL_ARENA[Boundary.DUEL_ARENA.length - 1];
    }
}