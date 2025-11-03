package io.zaryx.model.entity.player.mode.wildygroup;

import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.wildygroup.log.GwmDropItemLog;
import io.zaryx.model.entity.player.mode.wildygroup.log.GwmWithdrawItemLog;

import java.util.List;
import java.util.stream.Collectors;

public class GroupWildymanLogsInterface {

    public static void open(Player player) {
        GroupWildyRepository.getGroupForOnline(player).ifPresentOrElse(g -> {
            new DialogueBuilder(player).option(
                    new DialogueOption("Bank Withdraw Logs", plr -> {
                        List<String> lines = g.getWithdrawItemLog().stream().map(GwmWithdrawItemLog::toString).collect(Collectors.toList());
                        plr.getPA().openQuestInterfaceNew("Bank Withdraw Logs", lines);
                    }),
                    new DialogueOption("Item Drop Logs", plr -> {
                        List<String> lines = g.getDropItemLog().stream().map(GwmDropItemLog::toString).collect(Collectors.toList());
                        plr.getPA().openQuestInterfaceNew("Item Drop Logs", lines);
                    }),
                    DialogueOption.nevermind()
            ).send();
        }, () -> player.sendMessage("You're not in a Group Wildyman group."));
    }

}
