package com.foros.util.mapper;

import com.foros.session.IdentifiableTO;

public class IdentifiableTOMapper<V extends IdentifiableTO> implements Mapper<V, Long, V> {
        @Override
        public Pair<Long, V> item(V value) {
            return new Pair<Long, V>(value.getId(), value);
        }
    }