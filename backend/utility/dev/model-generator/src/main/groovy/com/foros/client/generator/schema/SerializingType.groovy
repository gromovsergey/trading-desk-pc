package com.foros.client.generator.schema


enum SerializingType {
    QUERY("query"), BODY("body");

    private String type

    SerializingType(String type) {
        this.type = type
    }

    static SerializingType findType(String type) {
        SerializingType.values().find {it.type == type }
    }
}
