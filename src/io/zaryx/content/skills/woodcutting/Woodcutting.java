package io.zaryx.content.skills.woodcutting;

import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;

public class Woodcutting {

	private static final Woodcutting INSTANCE = new Woodcutting();

    /* Updated by Khaos */
	public void chop(Player player, int objectId, int x, int y) {
		Tree tree = Tree.forObject(objectId);
        if (tree == null) { //Added safety check
            player.sendMessage("You can't chop this.");
            return;
        }

		player.facePosition(x, y);
		if (player.playerLevel[Player.playerWoodcutting] < tree.getLevelRequired()) {
			player.sendMessage("You do not have the woodcutting level required to cut this tree down.");
			return;
		}
		Hatchet hatchet = Hatchet.getBest(player);
		if (hatchet == null) {
			player.sendMessage("You must have an axe and the level required to cut this tree down.");
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.sendMessage("You must have at least one free inventory space to do this.");
			return;
		}
        if (tree.getStumpId() != -1 && Server.getGlobalObjects().exists(tree.getStumpId(), x, y)) { // Skipping stump check if for -1
            player.sendMessage("This tree has been cut down to a stump, you must wait for it to grow.");
            return;
        }
		player.getPA().stopSkilling();
		player.sendMessage("You swing your axe at the tree.");
		player.startAnimation(hatchet.getAnimation());
		Server.getEventHandler().submit(new WoodcuttingEvent(player, tree, hatchet, objectId, x, y));
	}

	public static Woodcutting getInstance() {
		return INSTANCE;
	}

}
