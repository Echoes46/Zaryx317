package io.zaryx.content.keyboard_actions;

import io.zaryx.model.entity.player.Player;

@FunctionalInterface
public interface KeyboardStrategy {
    void execute(Player player);
}
