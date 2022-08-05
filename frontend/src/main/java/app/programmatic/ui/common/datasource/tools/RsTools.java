package app.programmatic.ui.common.datasource.tools;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RsTools {

    public static Long getNullableLong(ResultSet rs, String columnName) throws SQLException {
        long result = rs.getLong(columnName);
        return rs.wasNull() ? null : result;
    }
}
