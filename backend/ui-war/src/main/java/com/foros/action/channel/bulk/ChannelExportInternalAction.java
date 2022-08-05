package com.foros.action.channel.bulk;

import com.foros.action.admin.channel.ChannelSearchStatus;
import com.foros.action.channel.ChannelsSearchParams;
import com.foros.framework.ReadOnly;
import com.foros.model.channel.Channel;
import com.foros.service.RemoteServiceException;
import com.foros.session.TooManyRowsException;
import com.foros.session.account.AccountService;
import com.foros.session.channel.TooManyTriggersException;
import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.session.channel.service.SearchChannelService;

import com.opensymphony.xwork2.ModelDriven;
import java.io.IOException;
import java.util.Collection;
import javax.ejb.EJB;

public class ChannelExportInternalAction extends ChannelExportBaseAction implements ModelDriven<ChannelsSearchParams> {
    @EJB
    protected SearchChannelService searchChannelService;

    @EJB
    protected AccountService accountService;

    private ChannelsSearchParams searchParams = new ChannelsSearchParams();
    private Boolean resubmitRequired;

    @ReadOnly
    public String export() throws IOException {
        try {
            Collection<Channel> channels = searchChannelService.searchForExport(
                    searchParams.getName(),
                    getAccountId(),
                    searchParams.getCountryCode(),
                    searchParams.getTestOption(),
                    new AdvertisingChannelType[]{ channelTypeHidden },
                    ChannelSearchStatus.toDisplayStatuses(searchParams.getStatus()),
                    searchParams.getVisibilityCriteria(),
                    searchParams.getPhrase(),
                    MAX_EXPORT_RESULT_SIZE);

            if (channels == null || channels.isEmpty()) {
                addErrorAndSave(getText("channel.export.emptyList"));
                return INPUT;
            }

            ChannelRowSource rowSource = new ChannelRowSource(channels.iterator(), createCsvNodeWriter());
            serialize(getMetaDataBuilder().forExport(), rowSource);

        } catch (RemoteServiceException e) {
            addActionError(getText("errors.serviceIsNotAvailable", new String[] {getText("channel.channelSearchService")}));
            return INPUT;
        } catch (TooManyTriggersException e) {
            addErrorAndSave(getText("channel.export.tooManyTriggers", new String[] {String.valueOf(e.getMaxTriggersCount())}));
            return INPUT;
        } catch (TooManyRowsException e) {
            addErrorAndSave(getText("channel.export.tooManyRows", new String[]{String.valueOf(MAX_EXPORT_RESULT_SIZE)}));
            return INPUT;
        }

        return null;
    }

    public ChannelsSearchParams getModel() {
        return searchParams;
    }

    public void setModel(ChannelsSearchParams searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public boolean isInternalProcessing() {
        return true;
    }

    public Boolean getResubmitRequired() {
        return resubmitRequired;
    }

    public void setResubmitRequired(Boolean resubmitRequired) {
        this.resubmitRequired = resubmitRequired;
    }
}
