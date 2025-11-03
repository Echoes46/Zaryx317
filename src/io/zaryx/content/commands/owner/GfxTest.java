package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.Graphic;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

import java.util.Optional;


public class GfxTest extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                for(int i = 0; i < 10; i++) {
                    c.startGraphic(new Graphic(Misc.random(1500)));
                }
            }
        }, 2);
    }
    @Override
    public Optional<String> getDescription() {
        return Optional.of("Tests new graphics system");
    }
}
