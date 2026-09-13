package io.zaryx.content.combat.specials.impl;
import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;

public final class BloodInfusion extends Special {
    public BloodInfusion() { super(2.5, 1.0, 1.25, new int[]{28997}); }
    public static boolean hasSet(Player p) {
        return p.playerEquipment[Player.playerWeapon] == 28997
                && p.playerEquipment[Player.playerHat] == 29028
                && p.playerEquipment[Player.playerChest] == 29022
                && p.playerEquipment[Player.playerLegs] == 29025;
    }
    @Override public boolean canActivate(Player p) {
        if (hasSet(p)) return true;
        p.sendMessage("Blood Infusion requires the full Blood Moon armour set.");
        return false;
    }
    public static int healthCost(int currentHealth) { return Math.max(0, currentHealth / 4); }
    @Override public void onStart(Player p) {
        int cost = healthCost(p.getHealth().getCurrentHealth());
        if (cost > 0) p.appendUnmitigatedSelfDamage(cost);
    }
    @Override public void activate(Player p, Entity target, Damage damage) { p.startAnimation(10989); }
    @Override public void hit(Player p, Entity target, Damage damage) { }
}
