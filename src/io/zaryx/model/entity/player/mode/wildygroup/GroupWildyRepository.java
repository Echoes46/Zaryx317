package io.zaryx.model.entity.player.mode.wildygroup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.zaryx.Server;
import io.zaryx.annotate.Init;
import io.zaryx.content.collection_log.CollectionLog;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class GroupWildyRepository {

    private static final Logger logger = LoggerFactory.getLogger(GroupWildyRepository.class);

    /**
     * Group data for all created Wildyman groups.
     */
    private static final List<GroupWildyManGroup> groups = new ArrayList<>();
    private static final Map<String, GroupWildyManGroup> groupsByLoginName = new HashMap<>();
    private static final Map<String, GroupWildyManGroup> groupsByGroupName = new HashMap<>();

    public static GroupWildyManGroup addFormingGroup(Player player, String name) {
        Optional<GroupWildyManGroup> current = getGroupForOnline(player);
        if (current.isPresent()) {
            player.sendStatement("You already have a group, you must leave your current group.");
            return null;
        }

        if (player.isJoinedWildymanGroup()) {
            player.sendStatement("You've already joined and left a group, you can't join/create another.");
            return null;
        }

        name = name.trim();
        Preconditions.checkState(!groupExistsWithName(name), "Group with name already exists: " + name);

        GroupWildyManGroup group = new GroupWildyManGroup(name, Lists.newArrayList(player.getLoginNameLower()));
        group.setOnline(player);

        CollectionLog collectionLog = new CollectionLog();
        collectionLog.setGroupWildy(true);
        collectionLog.setSaveName(group.getName().toLowerCase());
        group.setCollectionLog(collectionLog);

        addGroup(group);
        return group;
    }

    private static void addGroup(GroupWildyManGroup group) {
        Preconditions.checkState(!groupExistsWithName(group.getName()), "Group with name already exists: " + group.getName());
        addToGroupLists(group);
        logger.debug("Added a new group {}", group);
    }

    private static void addToGroupLists(GroupWildyManGroup group) {
        groups.add(group);
        groupsByGroupName.put(group.getName().toLowerCase(), group);
        group.getMembers().forEach(it -> groupsByLoginName.put(it.toLowerCase(), group));
    }

    private static void removeGroup(GroupWildyManGroup group) {
        groupsByGroupName.remove(group.getName().toLowerCase());
        groups.remove(group);
        logger.debug("Removed group: {}", group);
    }

    public static void addToGroup(Player player, GroupWildyManGroup group) {
        if (groupsByLoginName.containsKey(player.getLoginNameLower())) {
            player.sendStatement("You are already in a group!");
            return;
        }

        if (group.size() >= 5) {
            player.sendStatement("That group has the maximum amount of players already.");
            return;
        }

        if (group.getJoined() >= 5) {
            player.sendStatement("That group has already had the maximum amount of joins, which is 5.");
            return;
        }

        if (player.isJoinedWildymanGroup()) {
            player.sendStatement("You've already joined and left a group, you can't join/create another.");
            return;
        }

        if (group.isFinalized()) {
            player.setJoinedWildymanGroup(true);
            group.setJoined(group.getJoined() + 1);
        }

        group.sendGroupNotice(player.getDisplayNameFormatted() + " has joined your group.");
        group.addToGroup(player);
        groupsByLoginName.put(player.getLoginNameLower(), group);
        player.getCollectionLog().setLinked(group.getCollectionLog());
        CollectionLog.combineforGroupWildyMan(player, group);

        logger.debug("Added player to group: {}", group);
    }

    public static void removeFromGroup(Player player, GroupWildyManGroup group) {
        player.getCollectionLog().setLinked(null); // Remove link to group collection log
        groupsByLoginName.remove(player.getLoginNameLower());
        group.removeFromGroup(player);
        group.sendGroupNotice(player.getDisplayNameFormatted() + " has left your group.");
        logger.debug("Removed {} from group.", player);
        if (group.size() == 0) {
            removeGroup(group);
        }
    }

    public static void removeFromGroup(String loginName, GroupWildyManGroup group) {
        groupsByLoginName.remove(loginName);
        group.removeFromGroup(loginName);
        group.sendGroupNotice(loginName + " has left your group.");
        logger.debug("Removed player with login name {} from group.", loginName);
        if (group.size() == 0) {
            removeGroup(group);
        }
    }

    public static void finalize(GroupWildyManGroup group) {
        group.getOnline().forEach(it -> it.setJoinedWildymanGroup(true));
        group.setJoined(group.getMembers().size());
        group.setFinalized(true);
        serializeAll();
    }

    public static void onLogin(Player player) {
        getFromGroupList(player).ifPresent(group -> {
            //GroupIronmanContest.insertContestEntry(player, group);
            group.sendGroupNotice(player.getDisplayNameFormatted() + " has logged in.");
            group.setOnline(player);
            groupsByLoginName.put(player.getLoginNameLower(), group);
            logger.debug("Set player online for group: {}", group);
        });
    }

    public static void onLogout(Player player) {
        getGroupForOnline(player).ifPresent(group -> {
            if (!group.isFinalized()) {
                group.removeFromGroup(player);
                logger.debug("Removed player from group because it was still forming on logout player={}, group={}", player, group);
                if (group.getOnline().isEmpty()) {
                    removeGroup(group);
                }
            } else {
                group.setOffline(player);
                group.sendGroupNotice(player.getDisplayNameFormatted() + " has logged out.");
                logger.debug("Removed player from online group on logout player={}, group={}", player, group);
                if (group.getOnline().isEmpty()) {
                    group.setCollectionLog(null);
                    logger.debug("Disposing group collection log because all players are offine in group {}", group);
                }
            }
        });
    }

    public static Optional<GroupWildyManGroup> getGroupForOnline(Player player) {
        return Optional.ofNullable(groupsByLoginName.get(player.getLoginNameLower()));
    }

    public static Optional<GroupWildyManGroup> getGroupForOffline(String loginName) {
        return Optional.ofNullable(groupsByLoginName.get(loginName.toLowerCase()));
    }

    public static Optional<GroupWildyManGroup> getFromGroupList(Player player) {
        return groups.stream().filter(it -> it.getMembers().contains(player.getLoginNameLower())).findFirst();
    }

    public static GroupWildyManGroup get(String groupName) {
        return groupsByGroupName.get(groupName.toLowerCase());
    }

    public static boolean groupExistsWithName(String name) {
        return groupsByGroupName.containsKey(name.trim().toLowerCase());
    }

    private static String getGroupsSaveDirectory() {
        return Server.getSaveDirectory() + "/gwm/";
    }

    public static void serializeAll() {
        Server.getIoExecutorService().submit(GroupWildyRepository::serializeAllInstant);
    }

    public static void serializeAllInstant() {
        try {
            Files.createDirectories(new File(getGroupsSaveDirectory()).toPath());
            for (GroupWildyManGroup group : groups) {
                serializeInstant(group);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void serializeInstant(GroupWildyManGroup group) throws IOException {
        if (group.isFinalized()) {
            GroupWildyGroupSave save = GroupWildyGroupSave.toSave(group);
            JsonUtil.toJacksonJson(save, getGroupsSaveDirectory() + save.getName() + ".json");
            logger.debug("Saved group Wildyman group {}", group);
        }
    }

    @Init
    public static void load() throws IOException {
        if (!new File(getGroupsSaveDirectory()).exists())
            return;
        groups.clear();

        File[] files = new File(getGroupsSaveDirectory()).listFiles();

        for (File file : files) {
            if (file.getName().endsWith(".json")) {
                try {
                    GroupWildyGroupSave save = JsonUtil.fromJacksonJson(file, new TypeReference<GroupWildyGroupSave>() {});
                    addToGroupLists(save.toGroup());
                } catch (Exception e) {
                    System.err.println("Error loading: " + file);
                    throw e;
                }
            }
        }

        logger.info("Loaded " + GroupWildyRepository.groups.size() + " group Wildyman groups.");
    }
}
