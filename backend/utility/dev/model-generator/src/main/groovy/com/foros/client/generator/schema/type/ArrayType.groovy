package com.foros.client.generator.schema.type

class ArrayType extends Type {

    List<Type> contentTypes

    ArrayType(List<Type> contentTypes) {
        super(null)
        this.contentTypes = contentTypes
    }

    List<Type> getContentTypes() {
        this.contentTypes
    }

    @Override
    String toString() {
        return "${contentTypes}"
    }

    String getName() {
        return "[" + contentTypes[0].name + "]";
    }
}
