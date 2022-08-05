package com.foros.client.generator.schema.type


class PrimitiveType extends Type {

    PrimitiveType(String name) {
        super(name)
    }

    @Override
    String toString() {
        return name
    }

}
