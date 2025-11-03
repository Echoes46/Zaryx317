package io.zaryx.model.entity.player.mode.wildygroup;

import io.zaryx.model.entity.player.mode.wildygroup.log.GwmDropItemLog;
import io.zaryx.model.entity.player.mode.wildygroup.log.GwmWithdrawItemLog;
import io.zaryx.model.items.GameItem;

import java.util.*;

public class GroupWildyGroupSave {

    public static GroupWildyGroupSave toSave(GroupWildyManGroup group) {
        return new GroupWildyGroupSave(group.getName(),
                group.getMembers(),
                group.isFinalized(),
                group.getBank().getInventory().getItems(),
                group.getJoined(),
                group.getMergedCollectionLogs(),
                group.getWithdrawItemLog(),
                group.getDropItemLog()
        );
    }

    private final String name;
    private final List<String> members;
    private final boolean finalized;
    private final GameItem[] bank;
    private final int joins;
    private final List<String> mergedCollectionLogs;
    private final Deque<GwmWithdrawItemLog> withdrawItemLog;
    private final Deque<GwmDropItemLog> dropItemLog;

    // For Jackson
    private GroupWildyGroupSave() {
        this(null, null, false, new GameItem[GroupWildyManBank.BANK_SIZE], 0, new ArrayList<>(), new ArrayDeque<>(), new ArrayDeque<>());
    }

    public GroupWildyGroupSave(String name, List<String> members, boolean finalized, GameItem[] bank, int joins,
                               List<String> mergedCollectionLogs, Deque<GwmWithdrawItemLog> withdrawItemLog, Deque<GwmDropItemLog> dropItemLog) {
        this.name = name;
        this.members = members;
        this.finalized = finalized;
        this.bank = bank;
        this.joins = joins;
        this.mergedCollectionLogs = mergedCollectionLogs;
        this.withdrawItemLog = withdrawItemLog;
        this.dropItemLog = dropItemLog;
    }

    public GroupWildyManGroup toGroup() {
        GroupWildyManGroup group = new GroupWildyManGroup(name, members);
        group.setFinalized(finalized);
        group.getBank().getInventory().set(bank);
        group.setJoined(joins);
        group.getMergedCollectionLogs().addAll(mergedCollectionLogs);
        group.getWithdrawItemLog().addAll(withdrawItemLog);
        group.getDropItemLog().addAll(dropItemLog);
        return group;
    }

    public String getName() {
        return name;
    }
}
