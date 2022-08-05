package com.foros.action.reporting.conversionPixels.treeFilter;


import com.foros.action.reporting.conversions.treeFilter.ConversrionsReportAbstractTreeFilterAction;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.action.ActionService;

import java.util.List;

import javax.ejb.EJB;


public class ConversionsTreeFilterAction extends ConversrionsReportAbstractTreeFilterAction {

    @EJB
    private ActionService actionService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        return actionService.search(ownerId);
    }

    @Override
    public String getParameterName() {
        return "conversionIds";
    }

    @Override
    protected int getLevel() {
        return 1;
    }
}
