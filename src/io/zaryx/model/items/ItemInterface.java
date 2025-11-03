package io.zaryx.model.items;

import io.zaryx.model.definitions.ItemDef;

public interface ItemInterface {

    default ItemDef getDef() {
        return ItemDef.forId(getId());
    }

    int getId();

    int getAmount();

}
