package com.foros.session.db;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.util.StringUtil;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class DBConstraintTest extends AbstractServiceBeanIntegrationTest {
    private Set<Object> failures;

    @Test
    public void testDatabaseName() {
        for (DBConstraint constraint : DBConstraint.values()) {
            if (constraint == DBConstraint.DEFAULT) {
                continue;
            }

            String databaseName = constraint.getConstraintName();
            Integer res = jdbcTemplate.queryForObject(
                    "select count(*) from pg_index where indexrelid::regclass::text = ? and indisunique",
                    Integer.class,
                    databaseName
            );

            if (res != 1) {
                failures.add(constraint + "->" + databaseName);
            }
        }

        assertFailures("Some constraints are missing");
    }

    @Test
    public void testResourceKeys() {
        ResourceBundle bundle = StringUtil.getBundle();
        for (DBConstraint constraint : DBConstraint.values()) {
            String key = constraint.getResourceKey();
            if (!bundle.containsKey(key)) {
                failures.add(constraint + "->" + key);
            }
        }
        assertFailures("Some constraints have no resource keys");
    }

    @Before
    public void init() {
        failures = new HashSet<>();
    }

    private void assertFailures(String message) {
        if (!failures.isEmpty()) {
            fail(message + "\n\t" + failures.toString());
        }
    }
}