package io.zaryx.sql.eventcalendar.queries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.zaryx.content.event.eventcalendar.ChallengeParticipant;
import io.zaryx.sql.DatabaseManager;
import io.zaryx.sql.DatabaseTable;
import io.zaryx.sql.SqlQuery;
import io.zaryx.sql.eventcalendar.tables.EventCalendarBlacklistTable;

public class CheckForBlacklistQuery implements SqlQuery<Boolean> {

    private static final DatabaseTable TABLE = new EventCalendarBlacklistTable();

    private final ChallengeParticipant participant;

    public CheckForBlacklistQuery(ChallengeParticipant participant) {
        this.participant = participant;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery("select * from " + TABLE.getName()
             + " where " + EventCalendarBlacklistTable.IP_ADDRESS + "='" + participant.getIpAddress() + "' "
             + " or " + EventCalendarBlacklistTable.MAC_ADDRESS + "='" + participant.getMacAddress() + "'");
        return rs.next();
    }
}
