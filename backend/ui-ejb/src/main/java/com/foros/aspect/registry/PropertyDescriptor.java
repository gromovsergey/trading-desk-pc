package com.foros.aspect.registry;

public class PropertyDescriptor {

    private String property;
    private PropertyAccessor propertyAccessor;
    private AspectDescriptor aspectDescriptor;

    public PropertyDescriptor(String property, PropertyAccessor propertyAccessor, AspectDescriptor aspectDescriptor) {
        this.property = property;
        this.propertyAccessor = propertyAccessor;
        this.aspectDescriptor = aspectDescriptor;
    }

    public Object getValue(Object bean) {
        return propertyAccessor.getValue(bean);
    }

    public String getProperty() {
        return property;
    }

    public AspectDescriptor getAspectDescriptor() {
        return aspectDescriptor;
    }

}
