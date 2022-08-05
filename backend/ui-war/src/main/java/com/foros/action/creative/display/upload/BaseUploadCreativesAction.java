package com.foros.action.creative.display.upload;

import com.foros.action.BaseActionSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.reporting.serializer.BulkFormat;
import com.foros.session.account.AccountService;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.creative.ValidationResultTO;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.util.CreateIfNull;
import javax.ejb.EJB;

public class BaseUploadCreativesAction extends BaseActionSupport implements RequestContextsAware {

    @EJB
    protected DisplayCreativeService displayCreativeService;

    @EJB
    private AccountService accountService;

    @CreateIfNull
    protected ValidationResultTO validationResult;

    private String id;
    private BulkFormat format = BulkFormat.CSV;
    private boolean alreadySubmitted = false;
    private Long advertiserId;
    private Account account;

    public String getId() {
        return id;
    }

    public void setId(String countryCode) {
        this.id = countryCode;
    }

    public BulkFormat getFormat() {
        return format;
    }

    public void setFormat(BulkFormat format) {
        this.format = format;
    }

    public boolean isAlreadySubmitted() {
        return alreadySubmitted;
    }

    public void setAlreadySubmitted(boolean alreadySubmitted) {
        this.alreadySubmitted = alreadySubmitted;
    }

    public ValidationResultTO getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResultTO validationResult) {
        this.validationResult = validationResult;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.switchTo(getAccount());
    }

    public Account getAccount() {
        if (account == null) {
            account = accountService.find(getAdvertiserId());
        }
        return account;
    }
}
