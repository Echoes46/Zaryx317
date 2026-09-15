package io.zaryx.content.commands.all;
import io.zaryx.content.commands.Command;
import io.zaryx.content.dialogue.*;
import io.zaryx.content.holiday.HolidayEvents;
import io.zaryx.model.entity.player.Player;
public class Holiday extends Command {
    @Override public void execute(Player p,String name,String input) {
        p.start(new DialogueBuilder(p).option("Holiday quest journals",
            new DialogueOption("Halloween",pl->HolidayEvents.journal(pl,io.zaryx.content.holiday.Holiday.HALLOWEEN)),
            new DialogueOption("Christmas",pl->HolidayEvents.journal(pl,io.zaryx.content.holiday.Holiday.CHRISTMAS))));
    }
}
