package com.foros.action;

import com.foros.model.EntityBase;

/**
 *
 * @author alexey_koloskov
 */
public interface EntityClassAware {
    Class<? extends EntityBase> getEntityClass();
}
