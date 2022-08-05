package com.foros.session.site;

import com.foros.security.AccountRole;
import com.foros.session.status.ApprovableEntityTO;
import com.foros.model.site.Site;

/**
 * @author dmitry_antonov
 * @since 07.02.2008
 */
public class SiteTO extends ApprovableEntityTO {
    private Long accountId;
    private String accountName;
    private AccountRole accountRole;
    private Long accountFlags;
    private String siteUrl;
    private Long flags;

    public SiteTO(Long id, String name, Long accountId, String accountName, AccountRole accountRole, Long accountFlags, char status, char qaStatus, String siteUrl, Long flags, Long displayStatusId) {
        super(id, name, status, qaStatus, Site.getDisplayStatus(displayStatusId));
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountRole = accountRole;
        this.accountFlags = accountFlags == null ? 0L : accountFlags;
        this.siteUrl = siteUrl;
        this.flags = flags;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }

    public Long getAccountFlags() {
        return accountFlags;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public Long getFlags() {
        return flags;
    }

    public void setFlags(Long flags) {
        this.flags = flags;
    }
}
