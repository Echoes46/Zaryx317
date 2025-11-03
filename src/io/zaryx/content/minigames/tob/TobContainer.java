package io.zaryx.content.minigames.tob;

import io.zaryx.content.minigames.tob.instance.TobInstance;
import io.zaryx.content.minigames.tob.party.TobParty;
import io.zaryx.model.collisionmap.WorldObject;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.ExpMode;
import io.zaryx.model.entity.player.mode.group.ExpModeType;
import io.zaryx.model.items.GameItem;

import java.util.List;

/**
 * Handles actions outside of tob instance.
 */
public class TobContainer {

    private final Player player;

    public TobContainer(Player player) {
        this.player = player;
    }

    public void displayRewardInterface(List<GameItem> rewards) {
        player.getItems().sendItemContainer(22961, rewards);
        player.getPA().showInterface(22959);
    }

    public boolean handleClickObject(WorldObject object, int option) {
        if (object.getId() != TobConstants.ENTER_TOB_OBJECT_ID)
            return false;

        startTob();
        return true;
    }

    public void startTob() {
        if (!player.inParty(TobParty.TYPE)) {
            player.sendMessage("You must be in a party to start Theatre of Blood.");
            return;
        }

        if (player.totalLevel < 1000 &&
                !player.getExpMode().equals(new ExpMode(ExpModeType.OneTimes)) &&
                !player.getExpMode().equals(new ExpMode(ExpModeType.FiveTimes))) {
            player.sendMessage("You need a total level of at least 1000 to join this raid!");
            return;
        } else if (player.totalLevel < 750 &&
                (player.getExpMode().equals(new ExpMode(ExpModeType.OneTimes)) ||
                        player.getExpMode().equals(new ExpMode(ExpModeType.FiveTimes)))) {
            player.sendMessage("You need a total level of at least 750 to join this raid!");
            return;
        }

        player.getParty().openStartActivityDialogue(player, "Theatre of Blood", TobConstants.TOB_LOBBY::in, list -> new TobInstance(list.size()).start(list));
    }

    public boolean handleContainerAction1(int interfaceId, int slot) {
        if (inTob()) {
            return ((TobInstance) player.getInstance()).getFoodRewards().handleBuy(player, interfaceId, slot);
        }
        return false;
    }

    public boolean inTob() {
        return player.getInstance() != null && player.getInstance() instanceof TobInstance;
    }

}
