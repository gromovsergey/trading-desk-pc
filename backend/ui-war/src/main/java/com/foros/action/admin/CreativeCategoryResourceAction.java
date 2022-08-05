package com.foros.action.admin;

public class CreativeCategoryResourceAction extends EntityResourceAction {
    @Override
    protected String getFinalKey() {
        return getEntityName() + "." + getResourceKey();
    }

    @Override
    public Long getMaxLength() {
        return 50L;
    }
}
