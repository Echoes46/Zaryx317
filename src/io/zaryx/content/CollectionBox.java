package io.zaryx.content;

import io.zaryx.model.items.RewardDelivery;

import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.save.PlayerSaveEntry;
import io.zaryx.model.items.GameItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Collection box is just a place to put items that the player couldn't claim because they didn't have enough space or other reasons.
 */
public class CollectionBox {

    private final java.util.List<GameItem> pending = new java.util.ArrayList<>();

    public void add(Player player, GameItem gameItem) {
        pending.add(gameItem.copy());
        player.sendMessage(gameItem.getFormattedString() + " is waiting in your collection box. Use ::collect after making space.");
    }

    public void collect(Player player) {
        if (player.isBusy()) {
            player.sendMessage("Finish what you're doing before collecting your items.");
            return;
        }

        if (!Boundary.EDGEVILLE_PERIMETER.in(player)) {
            player.sendMessage("You must be in Edgeville to collect your items.");
            return;
        }

        List<GameItem> gameItems = new java.util.ArrayList<>(pending);

        if (gameItems.isEmpty()) {
            player.sendMessage("Your collection box is empty.");
            return;
        }

        for (GameItem gameItem : gameItems) {
            if (RewardDelivery.give(player, gameItem)) {
                player.sendMessage("Collected {}.", gameItem.getFormattedString());
                pending.remove(gameItem);
            } else {
                break;
            }
        }
    }

    public static class CollectionBoxSave implements PlayerSaveEntry {

        @Override
        public List<String> getKeys(Player player) {
            return List.of("collection_box");
        }

        @Override
        public boolean decode(Player player, String key, String value) {
            player.getCollectionBox().pending.clear();
            if (value == null || value.length() == 0)
                return true;
            String[] data = value.split(";");
            List<GameItem> items = Arrays.stream(data).map(it -> {
                String[] split = it.split(":");
                return new GameItem(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }).collect(Collectors.toList());

            player.getCollectionBox().pending.clear();
            player.getCollectionBox().pending.addAll(items);
            return true;
        }

        @Override
        public String encode(Player player, String key) {
            return player.getCollectionBox().pending.stream().map(it -> it.getId() + ":" + it.getAmount()).collect(Collectors.joining(";"));
        }

        @Override
        public void login(Player player) { }
    }
}
