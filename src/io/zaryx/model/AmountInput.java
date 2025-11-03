package io.zaryx.model;

import io.zaryx.model.entity.player.Player;

public interface AmountInput {
    void handle(Player player, int amount);
}
