package com.foros.action.xml.options.filter;

import com.foros.session.EntityTO;
import com.foros.util.EntityUtils;

import java.util.Collection;

/**
 * User: paresh.morker
 * Date: Jun 25, 2009
 * Time: 1:21:50 PM
 */
public class OptionStatusFilter implements Filter<EntityTO> {
    private boolean includeDeleted;

    public OptionStatusFilter(boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public void filter(Collection<? extends EntityTO> options) {
        EntityUtils.applyStatusRules(options, null, includeDeleted);
    }

}
