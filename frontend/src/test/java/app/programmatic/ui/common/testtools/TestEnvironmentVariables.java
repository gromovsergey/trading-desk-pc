package app.programmatic.ui.common.testtools;

import java.time.LocalDateTime;

public class TestEnvironmentVariables {
    private Long agencyId;
    private Long accountId;
    private LocalDateTime timestamp;

    public TestEnvironmentVariables(Long agencyId, Long accountId, LocalDateTime timestamp) {
        this.agencyId = agencyId;
        this.accountId = accountId;
        this.timestamp = timestamp;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
