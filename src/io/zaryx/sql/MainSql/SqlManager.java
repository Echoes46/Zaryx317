package io.zaryx.sql.MainSql;

import io.zaryx.Server;


public class SqlManager {

    public static SQLNetwork getGameSqlNetwork() {
        return Server.gameSqlNetwork;
    }
    public static SQLNetwork getRealmSqlNetwork() {
        return Server.realmSqlNetwork;
    }

}
