package com.foros.client.generator.schema.type

class GenericType extends Type {

    List<Type> inheritance = []

    GenericType(String name, List<Type> inheritance, Documentation documentation = null) {
        super(name, documentation)
        this.inheritance = inheritance
    }

}
