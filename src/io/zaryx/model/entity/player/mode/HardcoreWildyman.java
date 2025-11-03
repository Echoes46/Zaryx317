package io.zaryx.model.entity.player.mode;

import io.zaryx.content.fireofexchange.FireOfExchangeBurnPrice;
import io.zaryx.model.entity.player.Player;

public class HardcoreWildyman extends IronmanMode {
    public HardcoreWildyman(ModeType type) {
        super(type);
    }

    @Override
    public boolean isTradingPermitted(Player player, Player other) {
        return other.getMode().getType().equals(ModeType.GROUP_WILDYMAN) && player.isInWildymanGroupWith(player);
    }
    @Override
    public double getDropModifier() {
        return -0.25;
    }
    @Override
    public boolean isItemScavengingPermitted() {
        return true;
    }

    @Override
    public boolean isShopAccessible(int shopId) {
        if (shopId == FireOfExchangeBurnPrice.SHOP_ID) {
            return true;
        }
        switch (shopId) {
            case 112:
            case 600:
            case 77:
            case 131:
            case 121:
            case 2:
            case 20:
            case 191:
            case 10:
            case 41:
            case 16:
            case 171:
            case 21:
            case 197:
            case 122:
            case 22:
            case 23:
            case 118:
            case 196:
            case 80:
            case 119:
            case 195:
            case 192:
            case 179:
            case 17:
            case 389:
                return true;

        }
        return false;
    }
    public boolean isItemSellable(int shopId, int itemId) {
        switch (shopId) {
            case 26:
            case 122:
            case 29:
            case 18:
            case 115:
            case 116:
            case 41:
                return true;
            case 195:
                if (itemId == 7478) {
                    return true;
                }
                break;
        }
        return false;
    }
    @Override
    public boolean isItemPurchasable(int shopId, int itemId) {
        switch (shopId) {
            case 389:
            case 112:
            case 179:
            case 17:
            case 197:
            case 21:
            case 41:
            case 23:
            case 122:
            case 131:
            case 77:
            case 118:
            case 196:
            case 191:
            case 121:
            case 2:
            case 20:
            case 195:
            case 10:
            case 171:
            case 22:
            case 16:
            case 80:
            case 119:
            case 192:
            case 600:
                return true;
        }
        return false;
    }
}
