package com.foros.rs.schema;

import com.foros.session.bulk.OperationType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ModelNode {
    public static enum UserRole {
        INTERNAL,
        EXTERNAL
    }

    private String name;
    private ModelNode parent;
    private String typeName;
    private Set<ModelNode> children;
    private Set<UserRole> userRoles;
    private Set<OperationType> operationTypes;
    private boolean collection;

    private ModelNode(ModelNode parent, String name, String typeName,
                      Collection<UserRole> userRoles, Collection<OperationType> operationTypes) {
        this.name = name;
        this.parent = parent;
        this.typeName = typeName;
        this.children = new HashSet<ModelNode>();
        this.userRoles = userRoles == null ? null : new HashSet<UserRole>(userRoles);
        this.operationTypes = operationTypes == null ? null : new HashSet<OperationType>(operationTypes);
        this.collection = typeName != null && typeName.startsWith("[");
    }

    public static ModelNode root() {
        return new ModelNode(null, "$$MODEL_ROOT", "$$MODEL_ROOT_TYPE", null, null);
    }

    public String getName() {
        return name;
    }

    public ModelNode getParent() {
        return parent;
    }

    public Set<ModelNode> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    public ModelNode addChild(String name, String typeName,
                              Collection<UserRole> userRoles, Collection<OperationType> operationTypes) {
        ModelNode node = new ModelNode(this, name, typeName, userRoles, operationTypes);
        children.add(node);
        return node;
    }

    public ModelNode findChild(String name, UserRole userRole, OperationType operationType) {
        ModelNode child = findChild(name, userRole);
        if (child != null && child.isApplicable(operationType)) {
            return child;
        }
        return null;
    }

    public ModelNode findChild(String name, UserRole userRole) {
        for (ModelNode child : children) {
            if (child.getName() == null) {
                // collection without wrapper element
                ModelNode collectionNode = child.findChild(name, userRole);
                if (collectionNode != null) {
                    return collectionNode;
                }
            } else if (child.getName().equals(name) && child.isApplicable(userRole)) {
                return child;
            }
        }
        return null;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isCollection() {
        return collection;
    }

    public boolean isCollectionElement() {
        return parent != null && parent.isCollection();
    }

    private boolean isApplicable(OperationType operationType) {
        return operationTypes == null || operationTypes.contains(operationType);
    }

    private boolean isApplicable(UserRole userRole) {
        return userRoles == null || userRoles.contains(userRole);
    }

    @Override
    public String toString() {
        return name;
    }
}

