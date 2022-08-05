package com.foros.session.restriction;

import com.foros.model.restriction.Predicates;
import com.foros.model.restriction.RestrictionCommandsOperation;

import javax.ejb.Local;


@Local
public interface LookupRestrictionService {
    Predicates lookupAndInvoke(RestrictionCommandsOperation operation);
}
