package app.programmatic.ui.authentication.model;

import app.programmatic.ui.account.dao.model.AccountRole;

public class Credentials {
    private String token;
    private String key;
    private AccountRole role;
    private Long id;
    private Long accountId;

    public Credentials(String token, String key, AccountRole role, Long id, Long accountId) {
        this.token = token;
        this.key = key;
        this.role = role;
        this.id = id;
        this.accountId = accountId;
    }

    public String getToken() {
        return token;
    }

    public String getKey() {
        return key;
    }

    public AccountRole getRole() {
        return role;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }
}
