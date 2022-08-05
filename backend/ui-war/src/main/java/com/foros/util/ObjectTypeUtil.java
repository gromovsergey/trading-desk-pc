package com.foros.util;

import com.foros.model.security.ObjectType;
import com.foros.model.EntityBase;

/**
 * Author: Boris Vanin
 */
public class ObjectTypeUtil {

    public static int getObjectType(EntityBase entity) {
        return ObjectType.valueOf(entity.getClass()).getId();
    }
    
}
