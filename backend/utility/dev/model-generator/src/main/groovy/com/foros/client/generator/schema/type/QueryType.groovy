package com.foros.client.generator.schema.type

class QueryType extends ComplexType {

    QueryType(String name, List<Field> fields, List<Type> inheritance, Documentation documentation) {
        super(name, fields, [:], inheritance,  documentation)
    }

}
