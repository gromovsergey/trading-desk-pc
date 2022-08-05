package com.foros.changes.inspection;

public interface ChangeDescriptorRegistry {
    EntityChangeDescriptor getDescriptor(Object object);
}
