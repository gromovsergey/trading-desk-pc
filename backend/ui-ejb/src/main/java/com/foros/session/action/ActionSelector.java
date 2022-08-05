package com.foros.session.action;

import com.foros.model.Status;
import com.foros.model.action.Action;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;

import java.util.List;

public class ActionSelector implements Selector<Action> {

    private List<Long> advertiserIds;
    private List<Long> actionIds;
    private List<Status> actionStatuses;
    private Paging paging;

    public List<Long> getAdvertiserIds() {
        return advertiserIds;
    }

    public void setAdvertiserIds(List<Long> advertiserIds) {
        this.advertiserIds = advertiserIds;
    }

    public List<Long> getActionIds() {
        return actionIds;
    }

    public void setActionIds(List<Long> actionIds) {
        this.actionIds = actionIds;
    }

    @Override
    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public List<Status> getActionStatuses() {
        return actionStatuses;
    }

    public void setActionStatuses(List<Status> actionStatuses) {
        this.actionStatuses = actionStatuses;
    }

}
