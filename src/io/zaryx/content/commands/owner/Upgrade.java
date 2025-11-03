package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.upgrade.UpgradeMaterials;
import io.zaryx.model.entity.player.Player;

public class Upgrade extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
/*        for (int i = 0; i < 78; i++) {
            player.getPA().itemOnInterface(6199, 1, 35150, i);
        }*/
        player.getUpgradeInterface().openInterface(UpgradeMaterials.UpgradeType.WEAPON);
        player.getPA().showInterface(35000);
    }
}
