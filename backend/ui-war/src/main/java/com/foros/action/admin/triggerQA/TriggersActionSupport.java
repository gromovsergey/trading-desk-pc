package com.foros.action.admin.triggerQA;

import com.foros.action.BaseActionSupport;
import com.foros.session.channel.triggerQA.TriggerQAService;
import com.foros.session.channel.triggerQA.TriggerQATO;

import java.util.List;

import javax.ejb.EJB;

public abstract class TriggersActionSupport extends BaseActionSupport {
    @EJB
    private TriggerQAService triggerQAService;
    protected TriggersSearchForm searchParams = new TriggersSearchForm();
    private List<TriggerQATO> triggers;

    public TriggerQAService getTriggerQAService() {
        return triggerQAService;
    }

    public TriggersSearchForm getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(TriggersSearchForm searchParams) {
        this.searchParams = searchParams;
    }

    public List<TriggerQATO> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<TriggerQATO> triggers) {
        this.triggers = triggers;
    }

    public Long getPage() {
        return searchParams.getPage();
    }

    public void setPage(Long page) {
        searchParams.setPage(page);
    }
}
