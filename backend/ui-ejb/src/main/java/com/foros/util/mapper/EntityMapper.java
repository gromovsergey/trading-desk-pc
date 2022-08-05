package com.foros.util.mapper;

import com.foros.session.EntityTO;

/**
 * Author: Boris Vanin
 */
public class EntityMapper implements Mapper<EntityTO, Long, String> {

    public Pair<Long, String> item(EntityTO value) {
        return new Pair<Long, String>(value.getId(), value.getName());
    }
    
}
