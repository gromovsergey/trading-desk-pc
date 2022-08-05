package com.foros.client.generator.schema.type

class Parameter extends Type implements Parametrized {

    List<Type> typeParameters
    Type type

    Parameter(String name, Type type, List<Type> typeParameters = [], Documentation documentation = null) {
        super(name, documentation)
        this.type = type
        this.typeParameters = typeParameters
    }

    public String toString ( ) {
        return "${type.name} ${name}";
    }

}
