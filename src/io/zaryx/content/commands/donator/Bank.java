package io.zaryx.content.commands.donator;

import io.zaryx.Server;
import io.zaryx.content.bosses.nightmare.NightmareConstants;
import io.zaryx.content.commands.Command;
import io.zaryx.content.minigames.arbograve.ArbograveConstants;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Right;

public class Bank extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.getPosition().inWild()
                || Server.getMultiplayerSessionListener().inAnySession(c)
                || Boundary.isIn(c, Boundary.DUEL_ARENA)
                || Boundary.isIn(c, Boundary.FIGHT_CAVE)
                || c.getPosition().inClanWarsSafe()
                || Boundary.isIn(c, Boundary.INFERNO)
                || c.getInstance() != null
                || Boundary.isIn(c, NightmareConstants.BOUNDARY)
                || Boundary.isIn(c, Boundary.OUTLAST_AREA)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
                || Boundary.isIn(c, Boundary.FOREST_OUTLAST)
                || Boundary.isIn(c, Boundary.SNOW_OUTLAST)
                || Boundary.isIn(c, Boundary.BOUNTY_HUNTER_OUTLAST)
                || Boundary.isIn(c, Boundary.ROCK_OUTLAST)
                || Boundary.isIn(c, Boundary.FALLY_OUTLAST)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
                || Boundary.isIn(c, new Boundary(3117, 3640, 3137, 3644))
                || Boundary.isIn(c, new Boundary(3114, 3611, 3122, 3639))
                || Boundary.isIn(c, new Boundary(3122, 3633, 3124, 3639))
                || Boundary.isIn(c, new Boundary(3122, 3605, 3149, 3617))
                || Boundary.isIn(c, new Boundary(3122, 3617, 3125, 3621))
                || Boundary.isIn(c, new Boundary(3144, 3618, 3156, 3626))
                || Boundary.isIn(c, new Boundary(3155, 3633, 3165, 3646))
                || Boundary.isIn(c, new Boundary(3157, 3626, 3165, 3632))
                || Boundary.isIn(c, Boundary.SWAMP_OUTLAST)
                || Boundary.isIn(c, Boundary.WG_Boundary)
                || Boundary.isIn(c, Boundary.PEST_CONTROL_AREA)
                || Boundary.isIn(c, Boundary.RAIDS)
                || Boundary.isIn(c, Boundary.OLM)
                || Boundary.isIn(c, Boundary.RAID_MAIN)
                || Boundary.isIn(c, Boundary.XERIC)
                || Boundary.isIn(c, Boundary.VOTE_BOSS)
                || Boundary.isIn(c, Boundary.NEX)
                || Boundary.isIn(c, Boundary.DONATOR_ZONE_BLOODY)
                || Boundary.isIn(c, Boundary.DONATOR_ZONE_BOSS)
                || Boundary.isIn(c, ArbograveConstants.ALL_BOUNDARIES)) {
            return;
        }



        if (c.teleTimer > 0) {
            return;
        }

        if (c.getY() >= 3520 && c.getY() <= 3525) {
            return;
        }

        if (c.amDonated >= 500 || c.getRights().isOrInherits(Right.MODERATOR)) {
            if (c.wildLevel <= 0) {
                c.getPA().c.itemAssistant.openUpBank();
                c.inBank = true;
            }
        }
    }
}
