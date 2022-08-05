package com.foros.changes.inspection.changeNode;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import org.apache.commons.lang3.text.WordUtils;

public class EmbeddedChange<T> extends FieldChange<T> {
    private static final Iterator<ChangeNode> EMPTY_ITERATOR = Collections.<ChangeNode>emptyList().iterator();

    public EmbeddedChange(FieldChangeDescriptor descriptor, T oldValue, T newValue) {
        super(descriptor, oldValue, newValue);
    }

    @Override
    public Iterator<? extends ChangeNode> getChildNodes() {
        return EMPTY_ITERATOR;
    }

    @Override
    public void prepareInternal(PrepareChangesContext context) {
        if (oldValue == null && newValue == null) {
            changeType = ChangeType.UNCHANGED;
            return;
        }
        if (newValue == null) {
            changeType = ChangeType.REMOVE;
            return;
        }
        if (oldValue == null) {
            changeType = ChangeType.ADD;
            return;
        }

        Class<T> clazz = (Class<T>)oldValue.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                Method method = clazz.getMethod("get" + WordUtils.capitalize(field.getName()), null);

                Object oldFieldValue = method.invoke(oldValue);
                Object newFieldValue = method.invoke(oldValue);

                boolean isChanged = oldFieldValue instanceof Comparable ? ((Comparable) oldFieldValue).compareTo(newFieldValue) != 0 :
                        !oldFieldValue.equals(newFieldValue);
                if (isChanged) {
                    changeType = ChangeType.UPDATE;
                    return;
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    protected void serializeContent(XMLStreamWriter writer) throws XMLStreamException {
        AuditSerializer serializer = descriptor.getSerializer();
        serializer.startSerialize(writer, this);
        serializer.endSerialize(writer);
    }

    public static class Factory implements ChangeNode.FieldChangeFactory {
        @Override
        public EmbeddedChange newInstance(FieldChangeDescriptor descriptor, Object oldValue, Object newValue) {
            return new EmbeddedChange(descriptor, oldValue, newValue);
        }
    }
}

