package com.foros.rs.resources;

import com.foros.AbstractUnitTest;
import com.foros.util.clazz.ClassFilter;
import com.foros.util.clazz.ClassSearcher;

import static org.junit.Assert.assertEquals;
import group.Unit;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class GeneralResourcesTest extends AbstractUnitTest {

    @Test
    public void checkAnnotations() throws Exception {
        final ClassSearcher classSearcher = new ClassSearcher("com.foros.rs.resources", true);

        final Set<Class> resourceClasses = classSearcher.search(new ClassFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
                return !clazz.getSimpleName().endsWith("Test");
            }
        });

        List<Class> invalidClasses = new ArrayList<>(resourceClasses.size());
        for (Class clazz : resourceClasses) {
            for (Annotation annotation : clazz.getAnnotations()) {
                if (annotation.toString().contains("ManagedBean")) {
                    invalidClasses.add(clazz);
                }
            }
        }

        if (!invalidClasses.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder("The following classes have '@ManagedBean' annotation:\n");
            for (Class invalidClass : invalidClasses) {
                errorMsg.append(invalidClass.getName());
                errorMsg.append('\n');
            }
            errorMsg.append("Please use '@javax.enterprise.context.RequestScoped' annotation instead. (For details, please see OUI-26125)");
            assertEquals("", errorMsg.toString());
        }
    }
}
