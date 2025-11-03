package io.zaryx.content.dialogue.impl;

import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.entity.player.Player;

public class SkillingPortalDialogue extends DialogueBuilder {



    public SkillingPortalDialogue(Player player) {
        super(player);
        setNpcId(-1)
                .option(new DialogueOption("Skilling Island", p -> player.getPA().startTeleport(2345,3804, 0, "modern", false)),
                        new DialogueOption("Hunter Area", p -> player.getPA().startTeleport(2020, 6005, 0, "modern", false)),
                        new DialogueOption("Farming", p -> player.getPA().startTeleport(2039, 6005, 0, "modern", false)));

    }
}
