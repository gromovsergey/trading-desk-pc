package com.foros.session;

public class TestLoggingJdbcTemplate extends LoggingJdbcTemplate {

    @Override
    public void init() {
        ServiceLocatorMock.getInstance().injectService(LoggingJdbcTemplate.class, this);
        super.init();
    }
}
