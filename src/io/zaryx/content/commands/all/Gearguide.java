package io.zaryx.content.commands.all;
import io.zaryx.content.commands.Command;
import io.zaryx.content.items.EquipmentGuide;
import io.zaryx.model.entity.player.Player;
public class Gearguide extends Command {
    @Override public void execute(Player p,String commandName,String input) { EquipmentGuide.click(p,EquipmentGuide.ENTRY); }
}
