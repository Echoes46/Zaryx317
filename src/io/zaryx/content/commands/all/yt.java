package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class yt extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        /*if (player.getRights().isOrInherits(Right.ADMINISTRATOR) || player.getRights().isOrInherits(Right.YOUTUBER)) {
            player.start(new DialogueBuilder(player).option("YouTube Management",
                    new DialogueOption("Open youtube voting page", YTManager::open),
                    new DialogueOption("Post youtube video", p -> {
                        p.getPA().closeAllWindows();
                        player.getPA().sendEnterString("Enter youtube video ID to add", YTManager::postVideo);
                    }),
                    new DialogueOption("Delete video", p -> {
                        p.getPA().closeAllWindows();
                        player.getPA().sendEnterString("Enter youtube video ID to delete", YTManager::deleteVideo);
                    })));
            return;
        }
        YTManager.open(player);*/
    }
}
