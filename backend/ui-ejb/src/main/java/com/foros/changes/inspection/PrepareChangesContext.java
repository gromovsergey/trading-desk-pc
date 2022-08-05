package com.foros.changes.inspection;

import com.foros.changes.inspection.changeNode.EntityChangeNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrepareChangesContext {
    private Map<Object, EntityChangeNode> changesMap;
    private ChangeDescriptorRegistry registry;

    public PrepareChangesContext(Map<Object, EntityChangeNode> changesMap, ChangeDescriptorRegistry registry) {
        this.changesMap = changesMap;
        this.registry = registry;
    }

    private ArrayDeque<ChangeNode> stack = new ArrayDeque<ChangeNode>();


    public List<ChangeNode> getStack() {
        return new ArrayList<ChangeNode>(stack);
    }

    public EntityChangeNode getChange(Object item) {
        return changesMap.get(item);
    }

    public EntityChangeDescriptor getDescriptor(Object o) {
        return registry.getDescriptor(o);
    }

    public void push(ChangeNode node) {
        stack.push(node);
    }

    public void pop() {
        stack.pop();
    }

    public EntityChangeNode getRoot() {
        return (EntityChangeNode) stack.getLast();
    }
}
