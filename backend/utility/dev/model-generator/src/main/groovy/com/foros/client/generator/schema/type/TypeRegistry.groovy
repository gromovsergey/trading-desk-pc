package com.foros.client.generator.schema.type

class TypeRegistry {

    private Map<String, Type> registry = [:]
    private TypeRegistry parentRegistry

    TypeRegistry(TypeRegistry parentRegistry = null) {
        this.parentRegistry = parentRegistry
    }

    public <T extends Type> T registerType(T type) {
        registry[type.name] = type
        return type
    }

    Type findType(String name) {
        if (!name) {
            return null
        }

        if (parentRegistry) {
            Type found = parentRegistry.findType(name)
            if (found) {
                return found
            }
        }

        if ((name.startsWith("xs:") || name.startsWith("oui:")) && !registry[name]) {
            registerType(new PrimitiveType(name))
        }

        return registry[name]
    }

    public Type type(String name, List<Type> types = []) {
        def type = findType(name)

        if (!type) {
            type = new LazyType(name, this)
        }

        if (!types) {
            return type
        }

        return new ParametrizedType(type, types)
    }
}
