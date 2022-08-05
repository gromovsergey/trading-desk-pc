package app.programmatic.ui.channel.view;

import app.programmatic.ui.channel.dao.model.Channel;

import java.util.ArrayList;
import java.util.List;

public class ExpressionChannelView {
    private Long id;
    private String name;
    private String country;
    private Long accountId;
    private String visibility;
    private Long version;

    private List<List<Channel>> includedChannels = new ArrayList<>();
    private List<List<Channel>> excludedChannels = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<List<Channel>> getIncludedChannels() {
        return includedChannels;
    }

    public void setIncludedChannels(List<List<Channel>> includedChannels) {
        this.includedChannels = includedChannels;
    }

    public List<List<Channel>> getExcludedChannels() {
        return excludedChannels;
    }

    public void setExcludedChannels(List<List<Channel>> excludedChannels) {
        this.excludedChannels = excludedChannels;
    }
}
