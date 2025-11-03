package io.zaryx.content.commands.owner;

import java.util.List;
import java.util.Optional;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.bank.BankItem;
import io.zaryx.util.Misc;
import io.zaryx.model.items.GameItem;

public class DropTest extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");
        if (args.length < 1) {
            player.sendMessage("::droptest npc_id amount (default amount is 10k)");
            return;
        }

        int npcId;
        try {
            npcId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid NPC ID.");
            return;
        }

        int count = 10_000;
        if (args.length == 2) {
            try {
                count = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid amount. Defaulting to 10k.");
            }
        }

        List<GameItem> dropSample = Server.getDropManager().getDropSample(player, npcId);
        if (dropSample == null || dropSample.isEmpty()) {
            player.sendMessage("No drops for NPC: " + npcId);
            return;
        }

        player.getItems().queueBankContainerUpdate();
        player.getBank().deleteAllItems();
        for (int k = 0; k < count; k++) {
            for (GameItem item : dropSample) {
                player.getBank().getCurrentBankTab().add(new BankItem(item.getId(), item.getAmount()));
            }
        }

        player.getItems().updateBankContainer();
        player.getItems().openUpBank();
        player.sendMessage("Drop testing " + npcId + " x " + Misc.insertCommas(count) + ".");
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Gets NPC drop x times and puts in bank, clears your bank.");
    }
}
