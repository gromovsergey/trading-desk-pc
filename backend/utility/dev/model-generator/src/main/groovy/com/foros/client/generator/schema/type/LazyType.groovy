package com.foros.client.generator.schema.type

class LazyType extends Type {
    private Type cached
    private TypeRegistry typeRegistry

    LazyType(String name, TypeRegistry typeRegistry) {
        super(name, null)
        this.typeRegistry = typeRegistry
    }

    @Override
    Object getProperty(String property) {
        if (property == "name") {
            return super.name;
        }
        return real().getProperty(property)
    }

    @Override
    void setProperty(String property, Object newValue) {
        real().setProperty(property, newValue)
    }

    Type real() {
        if (!cached) {
            cached = this.typeRegistry.findType(this.name)
        }

        if (!cached) {
            throw new RuntimeException("Lazy type initialization: type with name '${this.name}' not found!")
        }

        return cached
    }

    @Override
    String toString() {
        return "Lazy: ${name}"
    }
}
