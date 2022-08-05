package com.foros.action.support;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.reporting.tools.CancelQueryService;
import com.foros.reporting.tools.CancelQueryTO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ejb.EJB;

public class CancelQueryAction extends BaseActionSupport {

    @EJB
    private CancelQueryService cancelQueryService;

    @ReadOnly
    public String all() {
        return SUCCESS;
    }

    public List<CancelQueryTO> getAllContexts() {
        List<CancelQueryTO> allContexts = cancelQueryService.getAllContexts();
        Collections.sort(allContexts, new Comparator<CancelQueryTO>() {
            @Override
            public int compare(CancelQueryTO o1, CancelQueryTO o2) {
                return o1.getStarted().compareTo(o2.getStarted());
            }
        });
        return allContexts;
    }
}
