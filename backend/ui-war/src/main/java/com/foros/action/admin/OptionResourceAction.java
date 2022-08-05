package com.foros.action.admin;

public class OptionResourceAction extends EntityResourceAction {
    @Override
    public Long getMaxLength() {
        return 1000L;
    }
}
