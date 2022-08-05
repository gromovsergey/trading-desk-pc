package com.foros.client.generator.schema.type

class ComplexType extends Type {

    Map<String, GenericType> genericTypes = [:]
    List<Type> inheritance = []
    List<Field> fields = []

    ComplexType(String name, List<Field> fields, Map<String, GenericType> genericTypes, List<Type> inheritance, Documentation documentation = null) {
        super(name, documentation)
        this.fields = fields
        this.genericTypes = genericTypes
        this.inheritance = inheritance
    }

    @Override
    String toString() {
        def builder = new StringBuilder()

        if (documentation) {
            builder.append(documentation)
        }

        builder.append("class ${name}")
        if (inheritance) {
             builder.append(" extends ${inheritance.collect{it.name}}")
        }
        builder.append(" {\n")
        fields.each { builder.append("\t${it}\n") }
        builder.append("}\n")
        return builder.toString()
    }

    public Set<Type> getDependentTypes() {
        return (fields.collect {it.type} + inheritance).unique()
    }
}
