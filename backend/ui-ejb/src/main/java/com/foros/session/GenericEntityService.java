package com.foros.session;

import com.foros.model.security.Statusable;

import javax.ejb.Local;

@Local
public interface GenericEntityService {

    boolean isDeleted(Statusable target);

}
