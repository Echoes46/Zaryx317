package io.zaryx.sql.leaderboard;

import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.sql.DatabaseManager;
import io.zaryx.sql.SqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardGetAllUnclaimedRewards implements SqlQuery<List<GameItem>> {

    private final Player player;

    public LeaderboardGetAllUnclaimedRewards(Player player) {
        this.player = player;
    }

    @Override
    public List<GameItem> execute(DatabaseManager context, Connection connection) throws SQLException {
        ArrayList<GameItem> entries = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM leaderboards_collection "
                + " where username = ? and claimed = 0");
        statement.setString(1, player.getLoginName());

        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            int itemId = rs.getInt("item_id");
            int amount = rs.getInt("amount");
            entries.add(new GameItem(itemId, amount));
        }

        statement = connection.prepareStatement("UPDATE leaderboards_collection SET " +
                "claimed = 1 WHERE username = ?");
        statement.setString(1, player.getLoginName());
        statement.executeUpdate();

        return entries;
    }

}
