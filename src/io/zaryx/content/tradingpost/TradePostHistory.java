package io.zaryx.content.tradingpost;

import io.zaryx.model.items.GameItem;
import lombok.Getter;

public class TradePostHistory {

    @Getter
    private final String buyer;
    @Getter
    private final String seller;
    @Getter
    private final GameItem item;
    @Getter
    private final long timestamp;
    @Getter
    private final boolean nomad;
    @Getter
    private final int cost;


    public TradePostHistory(String buyer, String seller, GameItem item, long timestamp, boolean nomad, int cost) {
        this.buyer = buyer;
        this.seller = seller;
        this.item = item;
        this.timestamp = timestamp;
        this.nomad = nomad;
        this.cost = cost;
    }
}
