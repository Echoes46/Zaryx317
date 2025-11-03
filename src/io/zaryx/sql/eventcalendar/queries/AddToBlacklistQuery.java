package io.zaryx.sql.eventcalendar.queries;

import java.sql.Connection;
import java.sql.SQLException;

import io.zaryx.content.event.eventcalendar.ChallengeParticipant;
import io.zaryx.sql.DatabaseManager;
import io.zaryx.sql.DatabaseTable;
import io.zaryx.sql.SqlQuery;
import io.zaryx.sql.eventcalendar.tables.EventCalendarBlacklistTable;

public class AddToBlacklistQuery implements SqlQuery<Boolean> {

    private static final DatabaseTable TABLE = new EventCalendarBlacklistTable();

    private final ChallengeParticipant participant;

    public AddToBlacklistQuery(ChallengeParticipant participant) {
        this.participant = participant;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        connection.createStatement().execute("insert into " + TABLE.getName()+ " values("
               + "'" + participant.getIpAddress() + "', '" + participant.getMacAddress() + "')");
        return true;
    }

}
