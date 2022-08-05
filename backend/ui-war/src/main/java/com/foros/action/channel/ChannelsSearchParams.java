package com.foros.action.channel;

import com.foros.action.SearchForm;
import com.foros.model.account.AccountSearchTestOption;
import com.foros.session.channel.ChannelVisibilityCriteria;
import com.foros.util.StringUtil;

public class ChannelsSearchParams extends SearchForm {
    private String name;
    private String phrase;
    private Long accountId;
    private String countryCode;
    private String channelType;
    private String visibility;
    private String status;
    private AccountSearchTestOption testOption = AccountSearchTestOption.EXCLUDE;
    private String language;
    private Long categoryChannelId;

    public AccountSearchTestOption getTestOption() {
        return testOption;
    }

    public void setTestOption(AccountSearchTestOption testOption) {
        this.testOption = testOption;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ChannelVisibilityCriteria getVisibilityCriteria() {
        if (StringUtil.isPropertyEmpty(visibility)) {
            return ChannelVisibilityCriteria.ALL;
        }
        return ChannelVisibilityCriteria.valueOf(visibility.toUpperCase());
    }

    public Long getCategoryChannelId() {
        return categoryChannelId;
    }

    public void setCategoryChannelId(Long categoryChannelId) {
        this.categoryChannelId = categoryChannelId;
    }
}
