package com.foros.session.query;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.transform.BasicTransformerAdapter;

public abstract class AbstractEntityTransformer<T> extends BasicTransformerAdapter {

    protected abstract T transform(Map<String, Object> values);

    private static Map<String, Object> createMap(Object[] tuple, String[] aliases) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        for (int i = 0; i < tuple.length; i++) {
            String name = aliases[i];
            Object value = tuple[i];
            result.put(name, value);
        }

        return result;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return transform(createMap(tuple, aliases));
    }

}
