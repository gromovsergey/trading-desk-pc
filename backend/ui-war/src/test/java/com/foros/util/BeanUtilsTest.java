package com.foros.util;

import com.foros.model.LocalizableName;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BeanUtilsTest {
    @Test
    @Category(Unit.class)
    public void copyProperties() throws Exception {
        SourceForm source = new SourceForm();
        source.setId("1");
        source.setName("Source Class");
        InnerForm inner = new InnerForm();
        inner.setId("1");
        inner.setName("Innet Class");
        source.getCollection().add(inner);
        source.setAbstractClass(new InnerForm("1", "test name"));

        TargetForm result = new TargetForm();

        NumberFormat nf = NumberFormat.getInstance(Locale.UK);
        BeanUtils.copyProperties(result, source, nf);
        assertTrue("The result of copying is not equal to original!",
                source.getId().equals(result.getId().toString()) && 
                source.getId().equals(result.getId().toString()) && 
                result.getCollection().size() == 1 && 
                result.getCollection().get(0) != null && 
                source.getCollection().get(0).getId().equals(result.getCollection().get(0).getId()) &&
                source.getCollection().get(0).getName().equals(result.getCollection().get(0).getName()));
        assertEquals(source.getAbstractClass().getName(), result.getAbstractClass().getName());
    }

    @Test
    @Category(Unit.class)
    public void copyToGenericProperty() throws Exception {
        // Generic is LocalizedName
        LNSourceForm sourceLN = new LNSourceForm();
        sourceLN.setGeneric(new LocalizableName("default", "key"));

        GenericTargetForm<LocalizableName> targetLN = new GenericTargetForm<LocalizableName>();

        BeanUtils.copyProperties(targetLN, sourceLN, NumberFormat.getInstance(Locale.UK));

        assertEquals(sourceLN.getGeneric(), targetLN.getGeneric());

        // Generic is String
        StringSourceForm sourceStr = new StringSourceForm();
        sourceStr.setGeneric("name");

        GenericTargetForm<String> targetStr = new GenericTargetForm<String>();

        BeanUtils.copyProperties(targetStr, sourceStr, NumberFormat.getInstance(Locale.UK));
        assertEquals(sourceStr.getGeneric(), targetStr.getGeneric());
    }

    @Test
    @Category(Unit.class)
    public void copyFromGenericProperty() throws Exception {
        // Generic is LocalizedName
        LNSourceForm targetLN = new LNSourceForm();

        GenericTargetForm<LocalizableName> sourceLN = new GenericTargetForm<LocalizableName>();
        sourceLN.setGeneric(new LocalizableName("default", "key"));

        BeanUtils.copyProperties(targetLN, sourceLN, NumberFormat.getInstance(Locale.UK));

        assertEquals(sourceLN.getGeneric(), targetLN.getGeneric());

        // Generic is String
        StringSourceForm targetStr = new StringSourceForm();

        GenericTargetForm<String> sourceStr = new GenericTargetForm<String>();
        sourceStr.setGeneric("name");

        BeanUtils.copyProperties(targetStr, sourceStr, NumberFormat.getInstance(Locale.UK));
        assertEquals(sourceStr.getGeneric(), targetStr.getGeneric());
    }

    public static class SourceForm {
        private String id;
        private String name;
        private List<InnerForm> collection = new LinkedList<InnerForm>();
        private InnerForm abstractClass;

        public SourceForm() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<InnerForm> getCollection() {
            return collection;
        }

        public void setCollection(List<InnerForm> collection) {
            this.collection = collection;
        }

        public InnerForm getAbstractClass() {
            return abstractClass;
        }

        public void setAbstractClass(InnerForm abstractClass) {
            this.abstractClass = abstractClass;
        }
    }

    public static class TargetForm {
        private Long id;
        private String name;
        private List<InnerForm> collection = new LinkedList<InnerForm>();
        private AbstractClass abstractClass = new ConcreteClass();

        public TargetForm() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<InnerForm> getCollection() {
            return collection;
        }

        public void setCollection(List<InnerForm> collection) {
            this.collection = collection;
        }

        public AbstractClass getAbstractClass() {
            return abstractClass;
        }

        public void setAbstractClass(AbstractClass abstractClass) {
            this.abstractClass = abstractClass;
        }
    }

    public static class StringSourceForm extends SourceForm {
        private String generic;

        public String getGeneric() {
            return generic;
        }

        public void setGeneric(String generic) {
            this.generic = generic;
        }
    }

    public static class LNSourceForm extends SourceForm {
        private LocalizableName generic;

        public LocalizableName getGeneric() {
            return generic;
        }

        public void setGeneric(LocalizableName generic) {
            this.generic = generic;
        }
    }

    public static class GenericTargetForm<T> extends TargetForm {
        private T generic;

        public T getGeneric() {
            return generic;
        }

        public void setGeneric(T generic) {
            this.generic = generic;
        }

    }

    public static class InnerForm {
        private String id;
        private String name;

        public InnerForm() {
        }

        public InnerForm(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static abstract class AbstractClass {
        private String name;

        public AbstractClass() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public abstract void doIt();
    }

    public static class ConcreteClass extends AbstractClass {
        @Override
        public void doIt() {
        }
    }
}
