package com.foros.changes.inspection.changeNode;

import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.ChangeNodeSupport;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.PrepareChangesContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.Iterator;

/**
 * Change node which determines a single change in a model, which may not be bound to any persistent entity or entity's field
 * Used for audit purpose only.
 *
 * @param <T>
 */
public class DummyChangeNode<T> extends ChangeNodeSupport {
    public static final Iterator<ChangeNode> EMPTY_ITERATOR = Collections.<ChangeNode>emptyList().iterator();

    private T value;

    private ChangeType changeType;

    public DummyChangeNode(T value, ChangeType type) {
        this.value = value;
        this.changeType = type;
    }

    @Override
    protected void prepareInternal(PrepareChangesContext context) {
    }

    @Override
    public Object getLastDefinedValue() {
        return value;
    }

    @Override
    public Iterator<? extends ChangeNode> getChildNodes() {
        return EMPTY_ITERATOR;
    }

    @Override
    public ChangeType getChangeType() {
        return changeType;
    }

    @Override
    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        if (value != null) {
            writer.writeCData(String.valueOf(value));
        }
    }
}
