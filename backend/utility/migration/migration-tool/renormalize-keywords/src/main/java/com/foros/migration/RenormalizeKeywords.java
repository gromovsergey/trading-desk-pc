package com.foros.migration;

import com.foros.util.SQLUtil;
import com.foros.util.url.TriggerQANormalization;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class RenormalizeKeywords implements Migration.Executor {

    private static final int PRINT_SIZE = 10000;
    private static final int UPDATE_LIMIT = 5000;

    private static String countryCode;

    @Autowired
    Logger logger;

    @Autowired
    @Qualifier("postgresJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        if (args != null && args.length >= 1) {
            countryCode = args[0];
        }
        Migration.perform(RenormalizeKeywords.class);
    }

    @Override
    public void run() throws Exception {
        logger.info("Counting records number...");

        String commonSQL = " from channeltrigger ct" +
                " join triggers t using (trigger_id)" +
                " where ct.trigger_type in ('P', 'S')";

        if (countryCode != null) {
            commonSQL += " and t.country_code = '" + countryCode + "'";
        }

        Long recordsCount = jdbcTemplate.queryForObject("select count(*)" + commonSQL, Long.class);

        logger.info("Found " + recordsCount + " records");

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(
                "select channel_trigger_id, original_trigger, t.normalized_trigger, ct.negative, t.country_code" +
                        commonSQL);

        Map<Long, String> changedTriggers = new HashMap<Long, String>();

        int checkedCnt = 0;
        int invalidCnt = 0;
        for (; rowSet.next(); ++checkedCnt) {
            if (checkedCnt > 0 && checkedCnt % PRINT_SIZE == 0) {
                logger.info(String.format("%d triggers processed from %d", checkedCnt, recordsCount));
            }

            Long channelTriggerId = rowSet.getLong(1);
            String originalTrigger = rowSet.getString(2);
            String normalizedTrigger = rowSet.getString(3);
            String negative = rowSet.getString(4);
            String countryCode = rowSet.getString(5);

            try {
                String newNormalizedTrigger = ("Y".equals(negative) ? "-" : "") +
                        TriggerQANormalization.normalizeKeyword(countryCode, originalTrigger);

                if (!normalizedTrigger.equals(newNormalizedTrigger)) {
                    changedTriggers.put(channelTriggerId, newNormalizedTrigger);
                }
            } catch (Exception e) {
                logger.info("Original trigger " + originalTrigger +
                        " (ID=" + channelTriggerId + ") can't be normalized");
                invalidCnt++;
            }
        }

        int wantedCnt = changedTriggers.size();

        logger.info(checkedCnt + " triggers were checked");
        logger.info(invalidCnt + " triggers have errors during normalization");
        logger.info(wantedCnt + " incorrectly normalized triggers were found");

        if (wantedCnt == 0) {
            return;
        }

        logger.info("Normalizing triggers...");

        List<String> data = new ArrayList<>(wantedCnt);

        for (Map.Entry<Long, String> entry : changedTriggers.entrySet()) {
            data.add("(" + entry.getKey() + "," + SQLUtil.escapeStructValue(entry.getValue()) + ")");
        }

        for (int i = 0; i < wantedCnt; i += UPDATE_LIMIT) {
            final List<String> subList =
                    wantedCnt > i + UPDATE_LIMIT ? data.subList(i, i + UPDATE_LIMIT) : data.subList(i, wantedCnt);
            try {
                jdbcTemplate.update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        CallableStatement statement =
                                connection.prepareCall("{ call trigger.bulk_normalize_channel_triggers(?) }");
                        statement.setArray(1,
                                connection.createArrayOf("normalized_channel_trigger", subList.toArray()));
                        return statement;
                    }
                });
            } catch (Exception e) {
                logger.severe("Data: " + subList);
                throw e;
            }
            logger.info(subList.size() + " triggers were updated");
        }
    }
}
