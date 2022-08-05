package com.foros.action.campaign.bulk;

import com.foros.action.BaseActionSupport;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.campaign.TGTType;
import com.foros.reporting.serializer.BulkFormat;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.BulkCampaignToolsService;
import com.foros.session.campaign.ValidationResultTO;
import com.foros.util.context.RequestContexts;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.util.CreateIfNull;

public class UploadCampaignActionSupport extends BaseActionSupport implements AdvertiserSelfIdAware, RequestContextsAware {
    @EJB
    protected BulkCampaignToolsService bulkCampaignToolsService;

    @EJB
    AccountService accountService;

    private Long advertiserId;
    @CreateIfNull
    protected ValidationResultTO validationResult;
    private boolean alreadySubmitted = false;
    protected BulkFormat format = BulkFormat.CSV;
    protected TGTType tgtType;
    private List<TGTType> campaignTypes;


    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchToAdvertiser(advertiserId);
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
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

    public String getTgtType() {
        if (tgtType == null) {
            loadAvailableTGTTypes();
        }
        return tgtType.name();
    }

    public void setTgtType(TGTType tgtType) {
        this.tgtType = tgtType;
    }

    public List<TGTType> getCampaignTypes() {
        if (campaignTypes == null) {
            loadAvailableTGTTypes();
        }

        return campaignTypes;
    }

    protected void loadAvailableTGTTypes() {
        campaignTypes = new ArrayList<TGTType>();
        Account account = accountService.find(getAdvertiserId());

        if (account.getAccountType().isAllowTextChannelAdvertisingFlag()) {
            campaignTypes.add(TGTType.CHANNEL);
        } else if (tgtType == TGTType.CHANNEL) {
            tgtType = null;
        }

        if (account.getAccountType().isAllowTextKeywordAdvertisingFlag()) {
            campaignTypes.add(TGTType.KEYWORD);
        } else if (tgtType == TGTType.KEYWORD) {
            tgtType = null;
        }

        if (tgtType == null && !campaignTypes.isEmpty()) {
            tgtType = campaignTypes.get(0);
        }
    }
}

