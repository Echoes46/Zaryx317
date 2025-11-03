package io.zaryx.model.entity.player.mode.wildygroup;

import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.Censor;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.model.controller.DefaultController;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.punishments.PunishmentType;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Ynneh
 */
public class GroupWildyMan {

    /**
     * Variable to repersent the maximum capacity a group can have
     */
    private static final int MAXIMUM_MEMBERS = 8;
    public static final int GROUP_FORM_NPC = 8976;
    public static final Position GROUP_FORM_START_POSITION = new Position(3438, 3849);

    private static boolean isGroupLeader(Player player) {
        Optional<GroupWildyManGroup> g = GroupWildyRepository.getFromGroupList(player);
        if (g.get().isLeader(player)) {
            return true;  // Placeholder implementation
        } else {
            player.sendMessage("@red@You must be the Group Leader to do this!");
            return false;
        }
    }

    private static boolean isInGroup(Player player) {
        Optional<GroupWildyManGroup> g = GroupWildyRepository.getFromGroupList(player);
        if (g.isEmpty()) {
            player.sendMessage("@red@You need to be in a Group Wildyman team in order to use this feature.");
            return false;
        }
        return true;
    }

    public static void sendGroupMessage(Player p, String msg) {
        Optional<GroupWildyManGroup> g = GroupWildyRepository.getFromGroupList(p);

        if (g.isEmpty()) {
            p.sendMessage("You need to be in a Group Wildyman team in order to use this feature.");
            return;
        }

        GroupWildyManGroup group = g.get();

        if (Server.getPunishments().contains(PunishmentType.MUTE, p.getLoginName()) || Server.getPunishments().contains(PunishmentType.NET_BAN, p.connectedFrom)) {
            p.sendMessage("You are muted for breaking a rule.");
            return;
        }
        if (Censor.isCensored(p, msg))
            return;
        if (msg.length() < 2)
            return;

        msg = msg.replace(":tradereq", "");
        msg = msg.replace(":gamblereq:", "");

        group.sendGroupChat(p, msg.replace(";;", ""));
    }

    public static String displayStats(GroupWildyManGroup group) {
        /**
         * P.I doesn't allow use of \n ???????????????????????????
         */
        StringBuilder s = new StringBuilder("- "+group.getName()+" Statistics - \n");
        long totalXp = 0;
        int totalVotePoints = 0, totalSlayerPoints = 0, totalExchangePoints = 0, totalPkp = 0, totalBossPoints = 0;
        for (Player p : group.getOnline()) {
            if (p != null) {
                totalXp += p.getTotalXp();
                totalVotePoints += p.votePoints;
                totalSlayerPoints += p.getSlayer().getPoints();
                totalExchangePoints += p.foundryPoints;
                totalBossPoints += p.bossPoints;
                totalPkp += p.pkp;
            }
        }
        s.append("\nMembers: ("+group.size()+"/"+MAXIMUM_MEMBERS+")");
        s.append("\nXP: "+totalXp);
        s.append("\nVote Points: "+totalVotePoints);
        s.append("\nSlayer Points: "+totalSlayerPoints);
        s.append("\nExchange Points: "+totalExchangePoints);
        s.append("\nBoss Points: "+totalBossPoints);
        s.append("\nPkp: "+totalPkp);
        return s.toString();
    }

    public static void moveToFormingLocation(Player player) {
        player.moveTo(GROUP_FORM_START_POSITION); // Tutorial island, last house
        player.setController(new GroupWildyManFormingController());

        String[] text = {
                "Welcome to the Group WildyMan Forming area!",
                "Get your group ready and start your adventure!",
                "@red@You can join one group per account, if you're", "@red@going to join another, leave without forming a group."
        };

        player.start(new DialogueBuilder(player).npc(GROUP_FORM_NPC, text));
        Arrays.stream(text).forEach(player::sendMessage);
    }

    public static void formGroup(Player player) {
        GroupWildyManGroup group = GroupWildyRepository.getGroupForOnline(player).orElse(null);
        if (group == null || group.isFinalized())
            return;

        GroupWildyRepository.finalize(group);
        group.getOnline().forEach(plr -> moveAfterJoin(plr, group));
    }

    public static void moveAfterJoin(Player plr, GroupWildyManGroup group) {
        plr.moveTo(Configuration.START_POSITION_WILDYMAN_GROUP);
        plr.setController(new DefaultController());
        String[] text;

        if (group != null) {
            text = new String[] {
                    "You're group, '" + group.getName() + "', has been formed!",
                    "Remember to use // to communicate easily with your team!",
                    "@red@The leader can use ::gwminvite to invite more players.",
                    "You can join 5 total players, you have " + (5 - group.getJoined()) + " joins left."
            };
        } else {
            text = new String[] {
                    "You haven't joined a group yet.",
                    "You can join or create in the Wildyman room.",
                    "@red@You can only join/form one group, choose wisely!",
            };
        }

        plr.start(new DialogueBuilder(plr).npc(GROUP_FORM_NPC, text));
        Arrays.stream(text).forEach(plr::sendMessage);
    }

}
