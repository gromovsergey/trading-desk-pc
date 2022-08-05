package com.foros.action.channel.bulk;

import static com.foros.reporting.serializer.BulkFormat.CSV;
import static com.foros.session.channel.service.AdvertisingChannelType.BEHAVIORAL;
import com.foros.action.BaseActionSupport;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.framework.support.CmpSelfIdAware;
import com.foros.model.account.Account;
import com.foros.reporting.serializer.BulkFormat;
import com.foros.session.account.AccountService;
import com.foros.session.channel.BulkChannelToolsService;
import com.foros.session.channel.ValidationResultTO;
import com.foros.session.channel.service.AdvertisingChannelType;

import com.opensymphony.xwork2.util.CreateIfNull;
import javax.ejb.EJB;


public class UploadChannelActionSupport extends BaseActionSupport implements AgencySelfIdAware, AdvertiserSelfIdAware, CmpSelfIdAware {
    @EJB
    protected BulkChannelToolsService bulkChannelToolsService;

    private Long accountId;
    private Account account;
    private Account myAccount;

    @EJB
    protected AccountService accountService;

    @CreateIfNull
    protected ValidationResultTO validationResult;
    private boolean alreadySubmitted = false;
    protected BulkFormat format = CSV;
    protected AdvertisingChannelType channelType = BEHAVIORAL;

    public Long getAdvertiserId() {
        return accountId;
    }

    public Account getAccount() {
        if (account == null) {
            account = accountService.find(accountId);
        }
        return account;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAccountId() {
        return accountId;
    }

    @Override
    public void setAgencyId(Long agencyId) {
        accountId = agencyId;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.accountId = advertiserId;
    }

    public ValidationResultTO getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResultTO validationResult) {
        this.validationResult = validationResult;
    }

    public boolean isAlreadySubmitted() {
        return alreadySubmitted;
    }

    public void setAlreadySubmitted(boolean alreadySubmitted) {
        this.alreadySubmitted = alreadySubmitted;
    }

    public BulkFormat getFormat() {
        return format;
    }

    public void setFormat(BulkFormat format) {
        this.format = format;
    }

    public AdvertisingChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(AdvertisingChannelType channelType) {
        this.channelType = channelType;
    }

    protected void setAccount(Account account) {
        this.account = account;
    }

    public Account getMyAccount() {
        if (myAccount == null) {
            myAccount = accountService.getMyAccount();
        }
        return myAccount;
    }

    @Override
    public void setCmpId(Long cmpId) {
        this.accountId = cmpId;
    }
}
