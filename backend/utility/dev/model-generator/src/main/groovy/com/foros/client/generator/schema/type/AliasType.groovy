package com.foros.client.generator.schema.type

class AliasType extends Type {

    Type type

    AliasType(String name, Type type, Documentation documentation = null) {
        super(name, documentation)
        this.type = type
    }

    @Override
    String toString() {
        return "AliasType ${name} for ${type.name}"
    }

}
