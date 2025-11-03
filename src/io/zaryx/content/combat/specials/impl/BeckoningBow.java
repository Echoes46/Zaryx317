package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.bosses.Hunllef;
import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.range.RangeData;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.content.questing.hftd.DagannothMother;
import io.zaryx.model.Npcs;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

public class BeckoningBow extends Special {

    public BeckoningBow() {
        super(3.0, 3, 2.2, new int[] { 33005 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.usingBow = true;
        player.startAnimation(1074);
        damage.setAmount(Misc.random(130,200*2));

        if (target.isNPC()) {
            switch (target.asNPC().getNpcId()) {
                case Npcs.CORPOREAL_BEAST:
                case 5630:
                case 6474:
                case 2948:
                case 7527:
                case 7528:
                case 7585:
                case 7544:
                case 1226:
                case Npcs.DEMONIC_GORILLA_2:
                case Hunllef.RANGED_PROTECT:
                case DagannothMother.MELEE_PHASE:
                case 6374:
                case DagannothMother.EARTH_PHASE:
                case 6378:
                case DagannothMother.FIRE_PHASE:
                case 6376:
                case DagannothMother.WATER_PHASE:
                case 6375:
                case 6373:
                case 8355:
                case 8356:
                case DagannothMother.AIR_PHASE:
                    damage.setAmount(0);
            }
        }


        if (target.isPlayer()) {
            RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 1067, 43, 31, 37, 10);
        } else if (target.isNPC()) {
            RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 1067, 43, 31, 37, 10);
            RangeData.fireProjectileNpc(player, (NPC) target, 50, 65, 1067, 41, 34, 37, 10);
            RangeData.fireProjectileNpc(player, (NPC) target, 50, 60, 1067, 39, 28, 37, 10);
        }
    }


    @Override
    public void hit(Player player, Entity target, Damage damage) {
    }

}