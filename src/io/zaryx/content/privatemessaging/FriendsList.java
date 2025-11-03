package io.zaryx.content.privatemessaging;

import io.zaryx.Server;
import io.zaryx.content.Censor;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Right;
import io.zaryx.util.Misc;
import io.zaryx.util.logging.player.PrivateChatLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FriendsList {

    private static final Logger logger = LoggerFactory.getLogger(FriendsList.class);
    public static final int ONLINE = 0;
    public static final int FRIENDS = 1;
    public static final int OFFLINE = 2;
    private static final int MAX_SIZE = 125;

    private final Player player;
    private final FriendsListRepository repository = new FriendsListRepository();

    public FriendsList(Player player) {
        this.player = player;
    }

    public void onLogin() {
        player.addQueuedAction(plr -> updateOnlineStatusForOthers());
    }

    public void onLogout() {
        updateOnlineStatusForOthers();
    }

    public void addFromSave(List<FriendsListEntry> friendsListEntryList) {
        if (friendsListEntryList.isEmpty())
            return;
        if (Server.getConfiguration().isDisplayNamesDisabled()) {
            player.addQueuedAction(plr -> friendsListEntryList.forEach(entry -> addToList(FriendsListEntry.withDisplayName(entry, entry.getLoginName()))));
            player.addQueuedAction(plr -> updateOnlineStatusForOthers());
            return;
        }

    }

    public void addNew(String displayName, FriendType type) {
        int size = repository.getSize(type);
        if (size >= MAX_SIZE) {
            player.sendMessage("Your " + type.getName() + " list is full (@dre@" + size + "/" + MAX_SIZE + "@bla@).");
            return;
        }

        if (displayName.equalsIgnoreCase(player.getDisplayName())) {
            return;
        }

        if (Server.getConfiguration().isDisplayNamesDisabled()) {
            player.debug("Display names are disabled.");
            addToList(new FriendsListEntry(type, displayName, displayName));
            return;
        }
    }

    /**
     * Add to list on login or when adding new entries.
     */
    private void addToList(FriendsListEntry friendsListEntry) {
        if (friendsListEntry.getType() == FriendType.IGNORE && repository.isFriend(friendsListEntry)) {
            player.sendMessage("You must remove this person from your ignore list first.");
            return;
        } else if (friendsListEntry.getType() == FriendType.FRIEND && repository.isIgnore(friendsListEntry)) {
            player.sendMessage("You must remove this person from your friend's list first.");
            return;
        }

        repository.add(friendsListEntry);

        if (friendsListEntry.getType() == FriendType.FRIEND) {
            Player other = PlayerHandler.getPlayerByDisplayName(friendsListEntry.getDisplayName());
            player.getPA().addFriendOrIgnore(friendsListEntry.getDisplayName(), true, isOnline(other) ? 1 : 0);
        } else {
            player.getPA().addFriendOrIgnore(friendsListEntry.getDisplayName(), false, 0);
        }
    }

    public void remove(String displayName, FriendType type) {
        if (repository.remove(displayName, type)) {
            Player other = PlayerHandler.getPlayerByDisplayName(displayName);
            if (other != null) {
                other.getFriendsList().updateOnlineStatus(player);
            }
        }
    }

    /**
     * Cycles through every online player and updates the local player ({@link FriendsList#player}'s
     * online status.
     */
    public void updateOnlineStatusForOthers() {
        PlayerHandler.nonNullStream().forEach(plr -> plr.getFriendsList().updateOnlineStatus(player));
    }

    public void updateOnlineStatus(Player other) {
        if (!repository.isFriend(other))
            return;
        int world = isOnline(other) ? 1 : 0;
        player.getPA().updateFriendOnlineStatus(other.getDisplayName(), world);
        logger.debug("Update online status for {} on {} friends list.", other.getLoginName(), player.getLoginName());
    }

    public void updateDisplayName(Player other, String oldName) {
        if (repository.isFriend(other)) {
            player.getPA().sendFriendUpdatedDisplayName(oldName, other.getDisplayName());
        }
    }

    public void sendPrivateMessage(String displayName, byte[] message) {
        Player other = PlayerHandler.getPlayerByDisplayName(displayName);
        if (other == null) {
            player.sendMessage("That player is currently offline.");
            return;
        }

//        FriendsListEntry friendsListEntry = repository.get(other);
//        if (friendsListEntry == null) {
//            player.sendMessage("That player is not on your friend's list.");
//            return;
//        }

        if (!isOnline(other)) {
            player.sendMessage("@dre@That player is currently offline and won't get your message.");
            return;
        }

        try {
            String text = Misc.decodeMessage(message, message.length);

            if (!Misc.isValidChatMessage(text)) {
                player.sendMessage("Invalid chat message.");
                return;
            }

            if (!other.getFriendsList().getRepository().isFriend(player)) {
                if (Censor.isCensored(player, text)) {
                    player.sendMessage("Your private message to a non-friend contained censored words.");
                    return;
                }
            }

            Server.getLogging().write(new PrivateChatLog(player, text, other.getLoginName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Warning for staff because players always appear online even if private is set to off/friends
        if (other.getPrivateChat() == OFFLINE && player.getRights().hasStaffPosition()) {
            player.sendMessage("This player is appearing offline, they might not see your message.");
        }

        logger.debug("{} send pm to {}", player.getLoginName(), other.getLoginName());
        other.getPA().sendPM(player.getDisplayName(), player.getRights(), message);
    }

    private boolean isOnline(Player other) {
        if (other == null || other.isReadyToLogout())
            return false;

        // Always appear online for staff
        if (!player.getRights().isOrInherits(Right.GAME_DEVELOPER)) {
            return other.getPrivateChat() != OFFLINE
                    && !other.getFriendsList().getRepository().isIgnore(player)
                    && (other.getPrivateChat() != FRIENDS || other.getFriendsList().getRepository().isFriend(player));
        }

        return true;
    }

    public FriendsListRepository getRepository() {
        return repository;
    }
}
