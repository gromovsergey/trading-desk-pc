package com.foros.restriction;

import group.Db;
import group.Restriction;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.aspect.AspectException;
import com.foros.aspect.registry.AspectDeclarationRegistry;
import com.foros.aspect.registry.GlobalAspectRegistry;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class RestrictConsistentTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private GlobalAspectRegistry aspectRegistry;

    @Autowired
    private AspectDeclarationRegistry aspectDeclarationRegistry;

    @Test
    public void testConsistent() {
        try {
            /*for (ElAspectDescriptor aspectDescriptor : aspectRegistry.getDescriptors(Restriction.class)) {
                if (!aspectDescriptor.getName().equals("TestEntity.nonexistentRestriction")) {
                    AspectDeclarationDescriptor descriptor =
                            aspectDeclarationRegistry.getDescriptor(Restriction.class, aspectDescriptor.getName());
                }
            }*/
        } catch (AspectException e) {
            assertTrue(e.getMessage(), false);
        }
    }

}
