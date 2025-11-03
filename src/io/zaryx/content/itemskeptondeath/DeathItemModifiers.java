package io.zaryx.content.itemskeptondeath;

import io.zaryx.annotate.Init;
import io.zaryx.util.Reflection;

import java.util.HashMap;
import java.util.Map;

public class DeathItemModifiers {

    private static final Map<Integer, DeathItemModifier> modifiers = new HashMap<>();

    @Init
    public static void init() {
        Reflection.getSubClassInstances(DeathItemModifier.class).forEach(modifier -> {
            modifier.getItemIds().forEach(item -> {
                if (modifiers.containsKey(item))
                    throw new IllegalStateException("Modifier for lost item already exists for item " + item + ": " + modifiers.get(item));
                modifiers.put(item, modifier);
            });
        });
    }

    public static DeathItemModifier get(int itemId) {
        return modifiers.get(itemId);
    }
}
