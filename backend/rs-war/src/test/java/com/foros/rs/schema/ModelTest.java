package com.foros.rs.schema;

import com.foros.AbstractUnitTest;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.Attributes;

import static org.junit.Assert.*;

@Category(Unit.class)
public class ModelTest extends AbstractUnitTest {
    private static final UpdateAttributes UPDATE_ATTRIBUTES = new UpdateAttributes();

    @Test
    public void builder() {
        ModelBuilder builder = new GeneratedModelBuilder();
        ModelNode model = builder.build();
        assertNotNull(model);
    }

    @Test
    public void inspector() {
        ModelBuilder builder = new GeneratedModelBuilder();
        ModelNode model = builder.build();
        ModelInspector modelInspector = new ModelInspector(model);
        assertPath("", modelInspector);
        modelInspector.stepNext("operations", null);
        assertFalse(modelInspector.defective());
        assertPath("operations", modelInspector);

        try {
            modelInspector.stepNext("unknown", null);
            fail();
        } catch (InvalidTagException e) {
            assertNotNull(e);
        }
        assertTrue(modelInspector.defective());
        modelInspector.stepBack("unknown");
        assertPath("operations", modelInspector);
        assertFalse(modelInspector.defective());

        modelInspector.stepNext("operation", UPDATE_ATTRIBUTES);
        assertPath("operations[0]", modelInspector);
        assertFalse(modelInspector.defective());

        modelInspector.stepNext("campaign", null);
        assertPath("operations[0].campaign", modelInspector);
        assertFalse(modelInspector.defective());
        modelInspector.stepBack("campaign");
        assertPath("operations[0]", modelInspector);

        try {
            modelInspector.stepNext("campaign", null);
            fail();
        } catch (UnexpectedCollectionException e) {
            assertNotNull(e);
        }

        assertPath("operations[0].campaign[1]", modelInspector);
        assertTrue(modelInspector.defective());
        modelInspector.stepBack("campaign");
        assertPath("operations[0]", modelInspector);

        modelInspector.stepBack("operation");
        assertPath("operations", modelInspector);

        modelInspector.stepNext("operation", UPDATE_ATTRIBUTES);
        assertPath("operations[1]", modelInspector);
        assertFalse(modelInspector.defective());
        modelInspector.stepBack("operation");
        assertPath("operations", modelInspector);

        modelInspector.stepBack("operations");
        assertPath("", modelInspector);
        assertFalse(modelInspector.defective());
    }

    private void assertPath(String path, ModelInspector inspector) {
        assertEquals(path, inspector.getPathNode().fullPath("."));
    }

    private static class UpdateAttributes implements Attributes {
        @Override
        public int getLength() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getURI(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getLocalName(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getQName(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getType(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getValue(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getIndex(String uri, String localName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getIndex(String qName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getType(String uri, String localName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getType(String qName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getValue(String uri, String localName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getValue(String qName) {
            return "UPDATE";
        }
    }
}
