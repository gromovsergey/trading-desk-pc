package com.foros.rs.schema;

import com.foros.rs.schema.ModelNode.UserRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.bulk.OperationType;

import java.util.Stack;

import org.xml.sax.Attributes;

public class ModelInspector {

    public static enum Error {
        NO_COLLECTION_EXPECTED("xml.modelValidation.noCollectionExpected");

        private String messageKey;

        Error(String messageKey) {
            this.messageKey = messageKey;
        }

        public String getMessageKey() {
            return messageKey;
        }
    }

    private static final String OPERATION_TAG_NAME = "operation";
    private static final String TYPE_ATTRIBUTE_NAME = "type";

    private ModelNode current;
    private PathNode pathNode;
    private Stack<String> defective = new Stack<String>();
    private OperationType operationType;
    private UserRole userRole;

    public ModelInspector(ModelNode model) {
        current = model;
        userRole = SecurityContext.isInternal() ? UserRole.INTERNAL : UserRole.EXTERNAL;
    }

    public void stepNext(String name, Attributes attributes) {
        RuntimeException exception = null;

        pathNode = pathNode == null ? new PathNode(name) : pathNode.getChild(name);
        if (OPERATION_TAG_NAME.equals(name) && attributes != null) {
            try {
                if (pathNode.getParent().getName().equals("operations")) {
                    setOperationType(attributes.getValue(TYPE_ATTRIBUTE_NAME));
                }
            } catch (IllegalArgumentException e) {
                exception = new InvalidOperationException();
            }
        }
        ModelNode next = null;
        if (defective.empty()) {
            next = current.findChild(name, userRole, operationType);

            if (next == null && current.findChild(name, userRole) == null) {
                exception = new InvalidTagException();
            } else if (next != null && pathNode.getCounter() > 1 && !next.isCollectionElement()) {
                exception = new UnexpectedCollectionException();
                next = null;
            }

            if (next != null) {
                current = next;
            } else {
                defective.push(name);
            }

            if (exception != null) {
                throw exception;
            }
        }
    }

    public void stepBack(String name) {
        if (defective.empty()) {
            current = current.getParent();
        } else if (defective.peek().equals(name)) {
            defective.pop();
        }
        if (OPERATION_TAG_NAME.equals(name)) {
            operationType = null;
        }
        pathNode = pathNode.getParent();
    }

    public boolean defective() {
        return !defective.empty();
    }

    public PathNode getPathNode() {
        return pathNode == null ? new PathNode("") : pathNode;
    }

    public String getNodeTypeName() {
        return current == null ? null : current.getTypeName();
    }

    private void setOperationType(String value) {
        if (value != null) {
            operationType = OperationType.valueOf(value);
        }
    }
}
