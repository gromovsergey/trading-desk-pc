package com.foros.reporting.tools;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.reporting.tools.query.PreparedStatementResultSetExecutor;
import com.foros.session.StatsDbQueryProvider;
import com.foros.util.ExceptionUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.After;
import org.junit.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;

public class CancelQueryServiceTest extends AbstractServiceBeanIntegrationTest {

    private static final String TEST_ID = "test";

    @Autowired
    private CancelQueryService cancelQueryService;

    @Autowired
    private StatsDbQueryProvider statsDbQueryProvider;

    @Test
    public void testCancellable() {
        cancelQueryService.doCancellable(TEST_ID, new Runnable() {
            @Override
            public void run() {
                executeWithDelay(0);
            }
        });
    }

    @Test
    public void testCancel() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    fail(e.getMessage());
                }
                cancelQueryService.cancel(TEST_ID);
            }
        });
        try {
            cancelQueryService.doCancellable(TEST_ID, new Runnable() {
                @Override
                public void run() {
                    executeWithDelay(20);
                }
            });
            fail("Expected to be cancelled");
        } catch (Exception e) {
            PSQLException psqlException = ExceptionUtil.getCause(e, PSQLException.class);
            assertNotNull(psqlException);
            assertEquals("Should be canceled by user", "57014", psqlException.getSQLState());
        }
    }

    @Test
    public void testAllContexts() {
        cancelQueryService.doCancellable(TEST_ID, new Runnable() {
            @Override
            public void run() {
                Collection<CancelQueryTO> all = cancelQueryService.getAllContexts();
                assertEquals(1, all.size());
            }
        });
    }

    @After
    public void verifyNoContexts() {
        Collection<CancelQueryTO> all = cancelQueryService.getAllContexts();
        assertTrue(all.isEmpty());
    }

    private void executeWithDelay(int delay) {
        long testValue = System.currentTimeMillis();
        PreparedStatementResultSetExecutor executor = new PreparedStatementResultSetExecutor();
        executor.setSql("select ? from (select pg_sleep(?)) s");
        SqlParameter testValueParameter = new SqlParameter("testValue", Types.BIGINT);
        SqlParameter delayParameter = new SqlParameter("delay", Types.INTEGER);
        executor.getDeclaredParameters().add(testValueParameter);
        executor.getDeclaredParameters().add(delayParameter);
        List<SqlParameterValue> params = Arrays.asList(
                new SqlParameterValue(testValueParameter, testValue),
                new SqlParameterValue(delayParameter, delay)
        );
        TestResultSetExtractor extractor = new TestResultSetExtractor();
        statsDbQueryProvider.execute(executor, params, extractor);
        assertEquals(testValue, extractor.testValue);
    }

    private static class TestResultSetExtractor implements ResultSetExtractor {
        public long testValue;

        @Override
        public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
            assertTrue(rs.next());
            testValue = rs.getLong(1);
            return testValue;
        }
    }
}