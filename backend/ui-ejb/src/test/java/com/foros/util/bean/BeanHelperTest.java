package com.foros.util.bean;

import com.foros.model.EntityBase;
import com.foros.util.CollectionUtils;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Category( Unit.class )
public class BeanHelperTest {
    @Test
    public void propertyDescriptors() throws Exception {
        PropertyDescriptor[] descriptors = Introspector.getBeanInfo(EntityBase.class, Object.class).getPropertyDescriptors();
        assertNotNull(findDescriptor(descriptors, "changes"));
        assertNull(findDescriptor(descriptors, "class"));
    }

    private PropertyDescriptor findDescriptor(PropertyDescriptor[] descriptors, final String name) {
        return CollectionUtils.find(Arrays.asList(descriptors), new Filter<PropertyDescriptor>() {
            @Override
            public boolean accept(PropertyDescriptor element) {
                return element.getName().equals(name);
            }
        });
    }
}
