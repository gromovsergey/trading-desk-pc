package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.model.channel.Channel;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.session.CurrentUserService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.reporting.channel.ChannelReportService;
import com.foros.util.NameValuePair;
import com.foros.util.StringUtil;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class ReportAvailableColumnsXmlAction extends AbstractXmlAction<Collection<NameValuePair<String, String>>> {

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private SearchChannelService searchChannelService;

    @EJB
    private ChannelReportService channelReportService;

    private Long channelId;
    private Channel channel;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Channel getChannel() {
        if (channel == null && channelId != null) {
            channel = searchChannelService.find(channelId);
        }
        return channel;
    }

    public Collection<NameValuePair<String, String>> generateModel() throws ProcessException {
        ReportMetaData<DbColumn> metaData = channelReportService.getMetaData(getChannel(), currentUserService.isInternal());
        if (metaData == null) {
            return Collections.emptyList();
        }

        Collection<NameValuePair<String, String>> result = new ArrayList<>(metaData.getMetricsColumns().size());
        for (DbColumn column : metaData.getMetricsColumns()) {
            String shortKey = column.getNameKey();
            String longKey = shortKey + ".long";
            String longName = StringUtil.getLocalizedString(longKey, true);

            result.add(new NameValuePair(longName != null ? longKey : shortKey,
                                         longName != null ? longName : StringUtil.getLocalizedString(shortKey)));
        }

        return result;
    }
}
