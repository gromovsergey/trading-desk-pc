package com.foros.action.channel.bulk;

import com.foros.framework.ReadOnly;
import com.foros.model.channel.Channel;
import com.foros.session.TooManyRowsException;
import com.foros.session.channel.TooManyTriggersException;

import java.io.IOException;
import java.util.Collection;

public class ChannelExportAction extends ChannelExportBaseAction  {

    private Collection<Long> selectedChannelIds;
    private Collection<Long> selectedExpIds;
    private Collection<Long> selectedBehIds;

    @ReadOnly
    public String export() throws IOException {
        Collection<? extends Channel> channels;

        fillChannelIds();
        if (selectedChannelIds == null || selectedChannelIds.isEmpty()) {
            addErrorAndSave(getText("channel.export.nothingSelected"));
            return INPUT;
        }

        try {
            channels = bulkChannelToolsService.findForExport(getAdvertiserId(), getChannelTypeHidden(), selectedChannelIds, MAX_EXPORT_RESULT_SIZE);
            if (channels == null || channels.isEmpty()) {
                addErrorAndSave(getText("channel.export.emptyList"));
                return INPUT;
            }
        } catch (TooManyTriggersException e) {
            addErrorAndSave(getText("channel.export.tooManyTriggers", new String[] {String.valueOf(e.getMaxTriggersCount())}));
            return INPUT;
        } catch (TooManyRowsException e) {
            addErrorAndSave(getText("channel.export.tooManyRows", new String[] {String.valueOf(MAX_EXPORT_RESULT_SIZE)}));
            return INPUT;
        }

        ChannelRowSource rowSource = new ChannelRowSource(channels.iterator(), createCsvNodeWriter());
        serialize(getMetaDataBuilder().forExport(), rowSource);

        return null;
    }

    private void fillChannelIds() {
        switch (channelTypeHidden) {
        case EXPRESSION:
            selectedChannelIds = selectedExpIds;
            break;
        case BEHAVIORAL:
            selectedChannelIds = selectedBehIds;
            break;
        }
    }

    public void setSelectedBehavioralChannelIds(Collection<Long> ids) {
        this.selectedBehIds = ids;
    }

    public void setSelectedExpressionChannelIds(Collection<Long> ids) {
        this.selectedExpIds = ids;
    }

    @Override
    public boolean isInternalProcessing() {
        return false;
    }
}
