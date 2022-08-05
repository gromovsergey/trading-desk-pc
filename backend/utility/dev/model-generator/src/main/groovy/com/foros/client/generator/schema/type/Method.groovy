package com.foros.client.generator.schema.type

class Method extends Type {

    String httpMethod
    Type returnType
    List<Parameter> parameters

    Method(String name, Type returnType, List<Parameter> parameters, String httpMethod, Documentation documentation = null) {
        super(name, documentation)
        this.returnType = returnType
        this.parameters = parameters
        this.httpMethod = httpMethod
    }

    public String toString ( ) {
        return "${returnType.name} ${name}(${parameters})";
    }

}
