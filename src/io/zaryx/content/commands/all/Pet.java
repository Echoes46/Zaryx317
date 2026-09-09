package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.npc.pets.PetHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.ItemAssistant;
import java.util.ArrayList;
import java.util.List;

/** Displays existing server pet classifications without inventing upgrade bonuses. */
public class Pet extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.getInterfaceEvent().isActive() || io.zaryx.Server.getMultiplayerSessionListener().inAnySession(player)) {
            player.sendMessage("Please finish your current activity first.");
            return;
        }
        PetHandler.Pets pet = PetHandler.forItem(player.petSummonId);
        List<String> effects = new ArrayList<>();
        if (pet != null) {
            if (PetHandler.hasMeleePet(player) || PetHandler.hasDarkMeleePet(player)) effects.add("Melee companion bonus active.");
            if (PetHandler.hasRangePet(player) || PetHandler.hasDarkRangePet(player)) effects.add("Ranged companion bonus active.");
            if (PetHandler.hasMagePet(player) || PetHandler.hasDarkMagePet(player)) effects.add("Magic companion bonus active.");
            if (PetHandler.hasstoragepetout(player)) effects.add("Pet item storage available.");
            if (effects.isEmpty()) effects.add("No combat-category or storage bonus registered.");
        } else {
            effects.add("Summon a pet to view its information.");
        }
        player.getPA().sendString(22747, pet == null ? "No pet summoned" : ItemAssistant.getItemName(pet.getItemId()));
        for (int i = 0; i < 5; i++) player.getPA().sendString(22742 + i, i < effects.size() ? effects.get(i) : "");
        player.getPA().showInterface(22731);
    }
}
