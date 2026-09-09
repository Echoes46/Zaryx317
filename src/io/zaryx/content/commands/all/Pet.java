package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.npc.pets.PetHandler;
import io.zaryx.model.entity.npc.pets.PetPerks;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.ItemAssistant;
import java.util.*;

/** Read-only details for the active companion. */
public class Pet extends Command {
    public static final int ROW_COUNT = 64;
    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.getInterfaceEvent().isActive() || io.zaryx.Server.getMultiplayerSessionListener().inAnySession(player)) {
            player.sendMessage("Please finish your current activity first.");
            return;
        }
        PetHandler.Pets pet = player.hasFollower ? PetHandler.forItem(player.petSummonId) : null;
        List<String> lines = new ArrayList<>();
        if (pet != null) {
            for (String line : PetPerks.describe(pet.getItemId())) {
                while (line.length() > 67) {
                    int split = line.lastIndexOf(' ', 67);
                    if (split <= 0) split = 67;
                    lines.add(line.substring(0, split));
                    line = line.substring(split).trim();
                }
                lines.add(line);
            }
        } else {
            lines.add("@or1@CHOOSE A COMPANION");
            lines.add("Summon a pet, then use ::pet to inspect its abilities.");
            lines.add("");
            lines.add("Bonuses require the pet to be summoned unless stated.");
            lines.add("Pets without perks are shown as companions.");
        }
        player.getPA().sendString(22747, pet == null ? "No pet summoned" :
                PetPerks.displayName(pet.getItemId(), ItemAssistant.getItemName(pet.getItemId())));
        player.getPA().sendString(22754, pet == null ? "No active companion bonuses" : "Active companion | Bonuses require summoning unless stated");
        for (int i = 0; i < ROW_COUNT; i++) player.getPA().sendString(22800 + i, i < lines.size() ? lines.get(i) : "");
        // Keep old clients useful while the new scrollable panel rolls out.
        for (int i = 0; i < 5; i++) player.getPA().sendString(22742 + i, i < lines.size() ? lines.get(i) : "");
        player.getPA().setScrollableMaxHeight(22755, Math.max(176, lines.size() * 18 + 8));
        player.getPA().resetScrollBar(22755);
        player.getPA().showInterface(22731);
    }
}