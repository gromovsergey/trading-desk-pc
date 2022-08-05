package com.foros.client.generator.schema.type

class Field extends Type {

    Type type
    String nillable

    Field(String name, Type type, Documentation documentation = null, String nillable = null) {
        super(name, documentation)
        this.type = type
        this.nillable = nillable
    }

    @Override
    String toString() {
        return "${documentation?:""}\n${type.name} ${name}"
    }


}
