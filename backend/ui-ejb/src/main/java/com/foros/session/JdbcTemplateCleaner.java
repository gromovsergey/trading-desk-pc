package com.foros.session;

public class JdbcTemplateCleaner {

    private LoggingJdbcTemplate jdbcTemplate;

    public void initialize(LoggingJdbcTemplate jdbcTemplate) {
        if (this.jdbcTemplate == null) {
            this.jdbcTemplate = jdbcTemplate;
        }
    }

    public void clear() {
        if (this.jdbcTemplate != null) {
            this.jdbcTemplate.clearAuthInfo();
            this.jdbcTemplate = null;
        }
    }
}
