package io.zaryx.sql.MainSql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author RuneCrest
 * @social Discord: RuneCrest
 * Website: www.RuneCrest.com/site
 * @since 09/03/2024
 */
public interface SQLNetworkTransaction {

    void call(Connection connection) throws SQLException;

}
