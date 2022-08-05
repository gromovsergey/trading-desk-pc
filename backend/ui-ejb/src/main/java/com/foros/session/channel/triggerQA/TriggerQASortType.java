package com.foros.session.channel.triggerQA;

public enum TriggerQASortType {
    NEWEST("form.orderBy.newest", "created", "desc"),
    OLDEST("form.orderBy.oldest", "created", "asc"),
    ATOZ("form.orderBy.asc", "trigger", "asc"),
    ZTOA("form.orderBy.desc", "trigger", "desc"),
    LASTREVIEWED("form.orderBy.lastreviwed", "version", "desc"),
    FIRSTREVIEWED("form.orderBy.firstreviwed", "version", "asc");

    private String optionNameTranslation;
    private String orderColumn;
    private String orderDirection;

    private TriggerQASortType(String optionNameTranslation, String orderColumn, String orderDirection) {
        this.optionNameTranslation = optionNameTranslation;
        this.orderColumn = orderColumn;
        this.orderDirection = orderDirection;
    }

    public String getOptionNameTranslation() {
        return optionNameTranslation;
    }

    public String getOrderColumn() {
        return orderColumn;
    }

    public String getOrderDirection() {
        return orderDirection;
    }
}
