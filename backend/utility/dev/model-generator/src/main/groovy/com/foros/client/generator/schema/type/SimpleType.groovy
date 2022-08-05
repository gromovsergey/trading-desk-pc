package com.foros.client.generator.schema.type

class SimpleType extends Type {

    Type type
    List<String> values

    SimpleType(String name, List<String> values, Type type, Documentation documentation = null) {
        super(name, documentation)
        this.type = type
        this.values = values
    }

    @Override
    String toString() {
        return "${documentation?:""}\n${type}(${values})"
    }
}
