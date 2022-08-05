package com.foros.profiling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfilingContext {

    private StatisticKey key;

    private ProfilingContext parent;
    private List<ProfilingContext> children = null;

    private long beginTime;
    private long endTime;

    public ProfilingContext(StatisticKey key, ProfilingContext parentProfilingContext) {
        this.key = key;
        parent = parentProfilingContext;
        this.beginTime = System.nanoTime();
    }

    public List<ProfilingContext> getChildren() {
        return children != null ? children : Collections.<ProfilingContext>emptyList();
    }

    public ProfilingContext getParent() {
        return parent;
    }

    public StatisticKey getKey() {
        return key;
    }

    public void addChild(ProfilingContext context) {
        if (children == null) {
            children = new ArrayList<ProfilingContext>();
        }

        children.add(context);
    }

    public void end() {
        this.endTime = System.nanoTime();
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public String toString() {
        return " - " + key + " [" + (endTime - beginTime) + " nanosec]";
    }

    public long getBeginTime() {
        return beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getContextCount() {
        int count = 1;
        for (ProfilingContext child : children) {
            count += child.getContextCount();
        }
        return count;
    }
}
