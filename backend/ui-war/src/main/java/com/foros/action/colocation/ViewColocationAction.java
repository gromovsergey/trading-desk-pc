package com.foros.action.colocation;

import com.foros.framework.ReadOnly;
import com.foros.util.EntityUtils;

import javax.persistence.EntityNotFoundException;

public class ViewColocationAction extends ColocationActionSupport {

    @ReadOnly
    public String view() {
        if (colocation.getId() == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }
        colocation = EntityUtils.applyOwnerStatusRule(colocationService.find(colocation.getId()));
        return SUCCESS;
    }

}
