package com.foros.session;

import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateTimeUserType;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;

public class PostgresqlJdbcTemplate extends JdbcTemplate {

    public void execute(String sql, Object... params) {
        final PreparedStatementSetter pss = newArgPreparedStatementSetter(params);
        execute(sql, new PreparedStatementCallback<Object>() {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                pss.setValues(ps);
                return ps.execute();
            }
        });
    }

    public Array createArray(final String type, final Collection objects) {
        if (objects == null) {
            return null;
        }
        return createArray(type, objects.toArray());
    }

    public Array createArray(final String type, final Object... objects) {
        return execute(new ConnectionCallback<Array>() {
            @Override
            public Array doInConnection(Connection con) throws SQLException, DataAccessException {
                if (objects == null || objects.length == 0) {
                    return null;
                }
                return con.createArrayOf(type, objects);
            }
        });
    }

    @Override
    protected PreparedStatementSetter newArgPreparedStatementSetter(Object[] args) {
        return new ArgumentPreparedStatementSetter(args);
    }

    private static class ArgumentPreparedStatementSetter extends org.springframework.jdbc.core.ArgumentPreparedStatementSetter {
        public ArgumentPreparedStatementSetter(Object[] args) {
            super(args);
        }

        @Override
        protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
            if (argValue instanceof LocalDate) {
                PostgreLocalDateUserType.INSTANCE.nullSafeSet(ps, argValue, parameterPosition);
            } else if (argValue instanceof LocalDateTime) {
                PostgreLocalDateTimeUserType.INSTANCE.nullSafeSet(ps, argValue, parameterPosition);
            } else {
                super.doSetValue(ps, parameterPosition, argValue);
            }
        }
    }
}
