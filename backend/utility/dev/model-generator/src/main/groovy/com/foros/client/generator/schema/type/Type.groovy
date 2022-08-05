package com.foros.client.generator.schema.type

abstract class Type {

    Map<String, Object> meta = [:];
    String name
    Documentation documentation
    String packageName;

    Type(String name, Documentation documentation = null) {
        this.name = name
        this.documentation = documentation
    }
}
