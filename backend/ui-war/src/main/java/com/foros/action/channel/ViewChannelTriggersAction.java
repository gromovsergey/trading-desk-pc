package com.foros.action.channel;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.channel.service.ChannelTriggersStatsService;
import com.foros.session.channel.service.TriggerStatsTO;
import com.foros.session.channel.service.TriggersFilter;

import java.util.List;
import javax.ejb.EJB;

public class ViewChannelTriggersAction extends BaseActionSupport {

    @EJB
    private ChannelTriggersStatsService channelTriggersStatsService;

    private Long id;
    private TriggersFilter triggersFilter = new TriggersFilter();
    private List<TriggerStatsTO> triggers;
    private Long triggersTotal;

    @ReadOnly
    public String loadTriggersPage() {
        triggers = channelTriggersStatsService.getTriggers(id, triggersFilter);
        return SUCCESS;
    }

    public List<TriggerStatsTO> getTriggers() {
        return triggers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TriggersFilter getTriggersFilter() {
        return triggersFilter;
    }

    public void setTriggersFilter(TriggersFilter triggersFilter) {
        this.triggersFilter = triggersFilter;
    }

    public Long getTriggersTotal() {
        return triggersTotal;
    }

    public void setTriggersTotal(Long triggersTotal) {
        this.triggersTotal = triggersTotal;
    }
}
