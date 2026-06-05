package io.zaryx.content.tradingpost;

import io.zaryx.model.items.GameItem;
import lombok.Getter;
import lombok.Setter;

/**
 * Updated by Khaos
 */
public class TradePostOffer {

    /**
     * Currency IDs:
     * -1 = Upgrade Points
     * 995 = Coins
     * 13204 = Plat
     */
    public static final int UPGRADE_POINTS = -1;
    public static final int COINS = 995;
    public static final int PLAT = 13204;

    @Getter
    @Setter
    private String username;

    @Getter
    private final GameItem item;

    @Getter
    private final int pricePerItem;

    @Getter
    private final long timestamp;

    @Getter
    private final int currencyId;

    @Getter
    private final int totalSold;

    public TradePostOffer(String username, GameItem item, int pricePerItem, long timestamp, int currencyId, int totalSold) {
        this.username = username;
        this.item = item;
        this.pricePerItem = pricePerItem;
        this.timestamp = timestamp;
        this.currencyId = currencyId;
        this.totalSold = totalSold;
    }

    /**
     * Legacy constructor.
     * Keeps older code working:
     * true = Upgrade Points
     * false = Plat
     */
    public TradePostOffer(String username, GameItem item, int pricePerItem, long timestamp, boolean nomad, int totalSold) {
        this(
                username,
                item,
                pricePerItem,
                timestamp,
                nomad ? UPGRADE_POINTS : PLAT,
                totalSold
        );
    }

    /**
     * Legacy support for old checks.
     */
    public boolean isNomad() {
        return currencyId == UPGRADE_POINTS;
    }

    public boolean isCoins() {
        return currencyId == COINS;
    }

    public boolean isPlat() {
        return currencyId == PLAT;
    }

    public boolean usesInventoryCurrency() {
        return currencyId != UPGRADE_POINTS;
    }
}