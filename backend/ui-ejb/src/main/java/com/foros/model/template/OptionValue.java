package com.foros.model.template;

import com.foros.model.account.Account;

public interface OptionValue {

    Long getOptionId();

    Option getOption();

    void setOption(Option option);

    String getValue();

    void setValue(String value);

    Account getAccount();

    boolean isFile();

    public String getFileStripped();

    public boolean isUrl();
}
