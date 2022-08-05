package com.foros.client.generator.schema.type

class ParametrizedType extends Type implements Parametrized {
    Type type
    List<Type> parameterTypes

    ParametrizedType(Type type, List<Type> parameterTypes) {
        super(null, null)
        this.type = type
        this.parameterTypes = parameterTypes
    }

    String getName() {
        return type.name
    }

    List<Type> getTypeParameters() {
        return parameterTypes
    }

}
