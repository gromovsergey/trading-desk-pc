package com.foros.session;

import com.foros.model.Status;
import com.foros.model.security.Statusable;

import javax.ejb.Stateless;

@Stateless(name = "GenericEntityService")
public class GenericEntityServiceBean implements GenericEntityService {

    @Override
    public boolean isDeleted(Statusable entity) {
        return entity.getInheritedStatus().equals(Status.DELETED);
    }

}
