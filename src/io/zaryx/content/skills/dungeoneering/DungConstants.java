package io.zaryx.content.skills.dungeoneering;

import com.google.api.client.util.Lists;
import io.zaryx.Configuration;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;

import java.util.List;
import java.util.Objects;

/**
 * Author @Kai
 * Discord - ZDS_KAI
 */

public class DungConstants {

    public static List<DungMain> dungGames = Lists.newArrayList();

    public static int currentDungHeight = 4;

    public static void checkInstances() {
        Lists.newArrayList(dungGames).stream().filter(Objects::nonNull).forEach(dung -> {
            if (dung.getPlayers().isEmpty()){
                dung.killAllSpawns();
                dungGames.remove(dung);
            }
        });
    }

    public static void checkLogin(Player player) {
        checkInstances();
        if (Boundary.isIn(player, Boundary.DUNG)) {
            boolean[] addedToGame = {false};
            Lists.newArrayList(dungGames)
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(dung -> dung.hadPlayer(player))
                    .findFirst()
                    .ifPresent(dung -> {
                        boolean added = dung.login(player);
                        if (added) {
                            addedToGame[0] = true;
                        }

                    });
            if (!addedToGame[0]) {
                player.sendMessage("You logged out and were removed from the Dungeon");
                player.getPA().movePlayerUnconditionally(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0);
                player.setDungInstance(null);
                player.specRestore = 120;
                player.specAmount = 10.0;
                player.setRunEnergy(100, true);
                player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
                player.getPA().refreshSkill(Player.playerPrayer);
                player.getHealth().removeAllStatuses();
                player.getHealth().reset();
                player.getPA().refreshSkill(5);
            }
        }
    }

}
