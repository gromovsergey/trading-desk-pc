package com.foros.reporting.tools.query;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Adjuster {

    public static final Adjuster NULL_ADJUSTER = new Adjuster();

    public CallableStatement adjustCallableStatement(CallableStatement cs) throws SQLException {
        return cs;
    }

    public PreparedStatement adjustPreparedStatement(PreparedStatement ps) throws SQLException {
        return ps;
    }

    public ResultSet adjustResultSet(ResultSet rs) throws SQLException {
        return rs;
    }

    public static Adjuster sequence(final Adjuster... adjusters) {
        return new Adjuster() {
            @Override
            public CallableStatement adjustCallableStatement(CallableStatement statement) throws SQLException {
                for (Adjuster adjuster : adjusters) {
                    statement = adjuster.adjustCallableStatement(statement);
                }
                return statement;
            }

            @Override
            public PreparedStatement adjustPreparedStatement(PreparedStatement statement) throws SQLException {
                for (Adjuster adjuster : adjusters) {
                    statement = adjuster.adjustPreparedStatement(statement);
                }
                return statement;
            }

            @Override
            public ResultSet adjustResultSet(ResultSet rs) throws SQLException {
                for (Adjuster adjuster : adjusters) {
                    rs = adjuster.adjustResultSet(rs);
                }
                return rs;
            }
        };
    }
}
