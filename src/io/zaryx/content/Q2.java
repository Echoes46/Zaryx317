package io.zaryx.content;

import io.zaryx.Server;
import io.zaryx.content.achievement.inter.AchieveV2;
import io.zaryx.content.combat.stats.MonsterKillLog;
import io.zaryx.content.item.lootable.LootableInterface;
import io.zaryx.content.playerinformation.Interface;
import io.zaryx.model.entity.player.Player;

public class Q2 {

    public static boolean Open(Player player, int buttonId) {
        switch (buttonId) {
            case 10282:
                player.getCollectionLog().openInterface(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10283:
                Server.getDropManager().openDefault(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10284:
                LootableInterface.openInterface(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10285:
                MonsterKillLog.openInterface(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10286:
                Interface.Open(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10287:
                AchieveV2.Open(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
            return true;
        }
        return false;
    }
}
