package com.foros.session.channel;

import com.foros.model.DisplayStatus;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.Channel;
import com.foros.session.NamedTO;

import java.sql.Timestamp;

public class DiscoverChannelTO extends NamedTO {
    private char channelType = 'D';
    private InternalAccount account = new InternalAccount();
    private String country;

    private DisplayStatus displayStatus;

    private long totalNews;
    private long dailyNews;
    private String discoverQuery;
    private Timestamp version;
    private String baseKeyword;

    public DiscoverChannelTO() {
    }

    public DiscoverChannelTO(Long id, String name, Long accountId, String accountName, Long accountDisplayStatusId,
                             Long displayStatusId, long totalNews, long dailyNews, String discoverQuery, String country) {
        super(id, name);

        this.account.setId(accountId);
        this.account.setName(accountName);
        this.account.setDisplayStatusId(accountDisplayStatusId);

        this.country = country;
        this.displayStatus = Channel.getDisplayStatus(displayStatusId);
        this.totalNews = totalNews;
        this.dailyNews = dailyNews;
        this.discoverQuery = discoverQuery;
    }

    public char getChannelType() {
        return channelType;
    }

    public InternalAccount getAccount() {
        return account;
    }

    public void setAccount(InternalAccount account) {
        this.account = account;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public long getTotalNews() {
        return totalNews;
    }

    public long getDailyNews() {
        return dailyNews;
    }

    public String getDiscoverQuery() {
        return discoverQuery;
    }

    public String getArticles() {
        return getTotalNews() + " (" + getDailyNews() + ")";
    }

    public String getBaseKeyword() {
        return baseKeyword;
    }

    public void setBaseKeyword(String baseKeyword) {
        this.baseKeyword = baseKeyword;
    }

    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "DiscoverChannelTO{" +
                "totalNews=" + totalNews +
                ", dailyNews=" + dailyNews +
                ", discoverQuery='" + discoverQuery + '\'' +
                "} " + super.toString();
    }
}
