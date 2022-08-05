package com.foros.session.creative;

public enum CreativeSortType {
    ATOZ("form.orderBy.asc", "name", "asc"),
    ZTOA("form.orderBy.desc", "name", "desc"),
    LASTREVIEWED("form.orderBy.lastreviwed", "version", "desc"),
    FIRSTREVIEWED("form.orderBy.firstreviwed", "version", "asc");

    private String optionNameTranslation;
    private String orderColumn;
    private String orderDirection;

    private CreativeSortType(String optionNameTranslation, String orderColumn, String orderDirection) {
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
