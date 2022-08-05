package com.foros.aspect.registry.find;

import com.foros.aspect.AspectInfo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AspectContainer<T> {
    private Map<T, Set<AspectInfo>> aspects = new HashMap<T, Set<AspectInfo>>();

    public Set<AspectInfo> get(T key) {
        Set<AspectInfo> aspectInfos = aspects.get(key);

        if (aspectInfos == null) {
            aspectInfos = new HashSet<AspectInfo>();
            aspects.put(key, aspectInfos);
        }

        return aspectInfos;
    }

    public boolean has(T key) {
        Set<AspectInfo> aspectInfos = aspects.get(key);
        return aspectInfos != null && !aspectInfos.isEmpty();
    }

    public void add(T key, AspectInfo info) {
        get(key).add(info);
    }
    
    public void add(T key, Set<AspectInfo> infos) {
        get(key).addAll(infos);
    }
    
    public boolean isEmpty() {
        return aspects.isEmpty();
    }

    public void iterate(Class<?> type, Aspects.AspectListener<T> fieldListener) {
        for (Map.Entry<T, Set<AspectInfo>> entry : aspects.entrySet()) {
            fieldListener.onValue(type, entry.getKey(), entry.getValue());
        }
    }
}
