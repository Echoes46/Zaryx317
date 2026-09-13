package io.zaryx.model.entity.player;

import io.zaryx.content.combat.weapon.WeaponData;
import io.zaryx.content.combat.weapon.WeaponMode;
import io.zaryx.model.items.ItemAssistant;

/**
 * @author Arthur Behesnilian 12:37 PM
 */
public class CombatConfig {

    /**
     * The Player bound to this configuration
     */
    private Player player;

    /**
     * The WeaponData for the Player's current weapon
     */
    private WeaponData weaponData;

    /**
     * The WeaponMode for the Player's current weapon
     */
    private WeaponMode weaponMode;

    /**
     * Determines the combat style
     * [Accurate, Aggressive, Etc...]
     */
    private int attackStyle;
    private int lastWeaponId = -1;
    private long bulwarkReadyTick = Long.MAX_VALUE;
    public long getBulwarkReadyTick() { return bulwarkReadyTick; }


    public CombatConfig(Player player) {
        this.player = player;
    }

    public int getAttackStyle() {
        return attackStyle;
    }

    public void setAttackStyle(int attackStyle) {
        this.attackStyle = attackStyle;
        this.updateWeapon();
    }

    public void updateWeapon() {
        int weaponId = player.getItems().getWeapon();

        WeaponData data = WeaponData.forItemId(weaponId);
        this.attackStyle = Math.max(0, Math.min(this.attackStyle, data.getWeaponModes().length - 1));

        WeaponMode mode = data.getWeaponModes()[this.attackStyle];
        boolean wasBlocking = weaponData == WeaponData.BULWARK && weaponMode != null && weaponMode.getIndex() == 1;
        boolean nowBlocking = data == WeaponData.BULWARK && this.attackStyle == 1;
        if (wasBlocking && (!nowBlocking || weaponId != lastWeaponId))
            player.attackTimer = Math.max(player.attackTimer, 8);
        if (nowBlocking && (!wasBlocking || weaponId != lastWeaponId)) {
            bulwarkReadyTick = io.zaryx.Server.getTickCount() + 8;
            player.attacking.reset();
        } else if (!nowBlocking) bulwarkReadyTick = Long.MAX_VALUE;
        lastWeaponId = weaponId;
        if (weaponData == null || weaponMode == null || !weaponData.equals(data) || !weaponMode.equals(mode)) {
            this.weaponMode = mode;
            this.weaponData = data;
            updateWeaponModeConfig();
            if (player.debugMessage) {
                player.sendMessage("Setting weapon mode: " + weaponData + ", " + weaponMode);
            }
        }
    }

    public void updateWeaponModeConfig() {
        int value = this.attackStyle;
        if (this.weaponData == WeaponData.SCYTHE) {
            value = value == 2 ? 1 : value == 1 ? 2 : value;
        }
        player.getPA().sendConfig(ItemAssistant.FIGHT_MODE_CONFIG, value);
    }

    public WeaponMode getWeaponMode() {
        return weaponMode;
    }

    public WeaponData getWeaponData() {
        return weaponData;
    }

}
