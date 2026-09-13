package io.zaryx.content.combat.weapon;

import io.zaryx.Server;
import io.zaryx.model.CombatType;
import io.zaryx.model.entity.player.Player;

/** Rules for weapons whose modes change the attack itself. */
public final class SpecialWeaponRules {
    public static final int SALAMANDER_SPELL = 110;
    private SpecialWeaponRules() { }
    public static boolean isBulwark(int id) { return id == 21015 || id == 28682 || id == 25604; }
    public static boolean isSalamander(int id) { return id >= 10146 && id <= 10149; }
    public static boolean isSalamanderAttack(Player p) {
        return isSalamander(p.getItems().getWeapon()) && !p.usingClickCast;
    }
    public static int tar(int id) {
        switch (id) {
            case 10149: return 10142;
            case 10146: return 10143;
            case 10147: return 10144;
            case 10148: return 10145;
            default: return -1;
        }
    }
    public static boolean hasTar(Player p) {
        return p.playerEquipment[Player.playerArrows] == tar(p.getItems().getWeapon())
                && p.playerEquipmentN[Player.playerArrows] > 0;
    }
    public static void consumeTar(Player p) {
        int remaining = p.playerEquipmentN[Player.playerArrows] - 1;
        p.getItems().setEquipment(remaining > 0 ? tar(p.getItems().getWeapon()) : -1,
                Math.max(0, remaining), Player.playerArrows, true);
    }
    public static CombatType salamanderType(int mode) {
        return mode == 0 ? CombatType.MELEE : mode == 1 ? CombatType.RANGE : CombatType.MAGE;
    }
    public static int magicMaxHit(int id, int level) {
        int bonus = id == 10149 ? 56 : id == 10146 ? 59 : id == 10147 ? 77 : 92;
        return Math.max(0, (int) (0.5 + level * (bonus + 64.0) / 640.0));
    }
    public static boolean blocking(Player p) {
        return isBulwark(p.getItems().getWeapon()) && p.getCombatConfigs().getAttackStyle() == 1;
    }
    public static boolean blockReady(Player p) {
        return blocking(p) && Server.getTickCount() >= p.getCombatConfigs().getBulwarkReadyTick();
    }
}
