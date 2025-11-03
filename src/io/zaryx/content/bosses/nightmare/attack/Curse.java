package io.zaryx.content.bosses.nightmare.attack;

import io.zaryx.content.bosses.nightmare.Nightmare;
import io.zaryx.content.bosses.nightmare.NightmareAttack;
import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;

public class Curse extends NightmareAttack {

    @Override
    public void tick(Nightmare nightmare) {
        if (getTicks() == 0) {
            nightmare.requestTransform(9426);
            nightmare.startAnimation(8599);
        }

        if (getTicks() == 2) {
            nightmare.getInstance().getPlayers().forEach(player -> {
                CombatPrayer.shiftProtectionPrayersRight(player, true);
                player.getPA().sendScreenFlash(0xE73158, 80);
                player.sendMessage("@pur@The Nightmare has cursed you, shuffling your prayers!");
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (container.getTotalTicks() == 50 || !nightmare.checkPlayerState(player)) {
                            container.stop();
                        }
                    }

                    @Override
                    public void onStopped() {
                        CombatPrayer.shiftProtectionPrayersRight(player, false);
                        player.sendMessage("@gre@You feel the effects of the Nightmare's curse wear off.");
                    }
                }, 1);
            });
        }

        if (getTicks() == 3) {
            nightmare.transformToStandard();
            stop();
        }
    }

}
