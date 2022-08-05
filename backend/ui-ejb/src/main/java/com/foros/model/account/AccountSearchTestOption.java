package com.foros.model.account;

public enum AccountSearchTestOption {

    EXCLUDE("account.testOption.exclude", 0), ONLY_TEST("account.testOption.onlyTest", 1), INCLUDE(
            "account.testOption.include", 2);

    private final String description;
    private final int value;

    private AccountSearchTestOption(String description, int value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return this.name();
    }
}
