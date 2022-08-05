package app.programmatic.ui.common.tool.javabean;

import app.programmatic.ui.common.model.VersionEntityBase;
import app.programmatic.ui.flight.dao.model.FrequencyCap;
import app.programmatic.ui.flight.dao.model.LineItem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


@RunWith(SpringRunner.class)
public class JavaBeanUtilsTest extends Assert {

    private JavaBeanAccessor<LineItem> lineItemEntityAccessor;

    @Before
    public void initialize() {
        lineItemEntityAccessor = JavaBeanUtils.createJavaEntityBeanAccessor(LineItem.class);
    }

    @Test
    public void testCache() {
        assertTrue(lineItemEntityAccessor == JavaBeanUtils.createJavaEntityBeanAccessor(LineItem.class));
        assertTrue(lineItemEntityAccessor != JavaBeanUtils.createJavaBeanAccessor(LineItem.class, VersionEntityBase.class));
    }

    @Test
    public void testJavaBeanEntityAccessor() {
        LineItem lineItem = createLineItemObject();
        LineItem cloned = cloneLineItem(lineItem, lineItemEntityAccessor);

        assertEntityFieldsCloned(cloned);
        assertNotNull(cloned.getVersion());
    }

    @Test
    public void testJavaBeanCustomAccessor() {
        LineItem lineItem = createLineItemObject();
        LineItem cloned = cloneLineItem(lineItem,
                JavaBeanUtils.createJavaBeanAccessor(LineItem.class, VersionEntityBase.class));

        assertEntityFieldsCloned(cloned);
        assertNull("Stop class is VersionEntityBase, so version must not be copied", cloned.getVersion());
    }

    @Test
    public void testClearProperties() {
        LineItem lineItem = new LineItem();
        lineItem.setId(1l);

        ArrayList<Long> channelIds = new ArrayList(1);
        channelIds.add(1l);
        lineItem.setChannelIds(channelIds);

        lineItemEntityAccessor.clearProperties(lineItem, Arrays.asList("id", "channelIds"));

        assertNull(lineItem.getId());
        assertTrue(lineItem.getChannelIds().isEmpty());
    }

    private LineItem createLineItemObject() {
        LineItem lineItem = new LineItem();

        // Ordinary Field
        lineItem.setId(1l);
        // Collection Field
        lineItem.setChannelIds(Collections.singletonList(1l));
        // Empty Collection field
        lineItem.setSiteIds(Collections.emptyList());
        // Object Field
        lineItem.setFrequencyCap(new FrequencyCap());
        //
        lineItem.setVersion(new Timestamp(0));

        return lineItem;
    }

    private LineItem cloneLineItem(LineItem source, JavaBeanAccessor<LineItem> lineItemAccessor) {
        LineItem cloned = new LineItem();

        for (String propertyName: lineItemAccessor.getPropertyNames()) {
            Object value = lineItemAccessor.get(source, propertyName);
            lineItemAccessor.set(cloned, propertyName, value);
        }

        return cloned;
    }

    private void assertEntityFieldsCloned(LineItem lineItem) {
        // Ordinary Field
        assertEquals(Long.valueOf(1), lineItem.getId());
        assertNull(lineItem.getCcgChannelId());

        // Collection Field
        assertNotNull(lineItem.getChannelIds());
        assertEquals(1, lineItem.getChannelIds().size());
        assertEquals(Long.valueOf(1), lineItem.getChannelIds().get(0));

        // Empty Collection field
        assertNotNull(lineItem.getSiteIds());
        assertTrue(lineItem.getSiteIds().isEmpty());

        // Null collection field
        assertNull(lineItem.getDeviceChannelIds());

        // Object Field
        assertNotNull(lineItem.getFrequencyCap());
        assertNull(lineItem.getMinCtrGoal());
    }
}
