package com.foros.action.campaign.bulk;

import com.opensymphony.xwork2.ActionContext;
import com.foros.framework.MessageStoreInterceptor;
import com.foros.framework.ReadOnly;
import com.foros.model.campaign.Campaign;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.session.TooManyRowsException;
import com.foros.util.StringUtil;
import java.io.IOException;
import java.util.Collection;

public class CampaignExportAction extends CampaignExportBaseAction  {
    private static final int MAX_EXPORT_RESULT_SIZE = 65000;

    private Collection<Long> selectedCampaignIds;

    private AccountBean account = new AccountBean();

    @ReadOnly
    public String export() throws IOException {
        if (getAdvertiserId() == null) {
            setAdvertiserId(StringUtil.toLong(account.getId()));
        }

        Collection<Campaign> campaigns;

        if (selectedCampaignIds == null || selectedCampaignIds.isEmpty()) {
            addErrorAndSave(getText("campaign.export.nothingSelected"));
            return INPUT;
        }

        try {
            campaigns = bulkCampaignToolsService.findForExport(getAdvertiserId(), getTgtType(), selectedCampaignIds, MAX_EXPORT_RESULT_SIZE);
            if (campaigns == null || campaigns.isEmpty()) {
                addErrorAndSave(getText("campaign.export.emptyList"));
                return INPUT;
            }
        } catch (TooManyRowsException e) {
            addErrorAndSave(getText("campaign.export.tooManyRows", new String[] { String.valueOf(MAX_EXPORT_RESULT_SIZE) }));
            return INPUT;
        }

        MetaData<Column> metaData = getMetaDataBuilder().forExport();
        NaturalCampaignTreeIterator entityIterator = new NaturalCampaignTreeIterator(campaigns.iterator());
        AbstractCampaignRowSource rowSource = new CampaignExportRowSource(entityIterator, MAIN_WRITER);
        serialize(metaData, rowSource, true);

        return null;
    }

    private void addErrorAndSave(String actionError) {
        addActionError(actionError);
        MessageStoreInterceptor.saveErrors(ActionContext.getContext(), this);
    }

    public Collection<Long> getSelectedCampaignIds() {
        return selectedCampaignIds;
    }

    public void setSelectedCampaignIds(Collection<Long> selectedCampaignIds) {
        this.selectedCampaignIds = selectedCampaignIds;
    }

    public AccountBean getAccount() {
        return account;
    }

    public void setAccount(AccountBean account) {
        this.account = account;
    }
}
