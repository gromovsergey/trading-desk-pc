package com.foros.client.generator.schema

import com.foros.client.generator.schema.type.AliasType
import com.foros.client.generator.schema.type.ComplexType
import com.foros.client.generator.schema.type.SimpleType
import com.foros.client.generator.schema.type.Type
import com.foros.client.generator.schema.type.TypeRegistry

class Schema {

    def aliases = [:] as LinkedHashMap<String, AliasType>
    def simpleTypes = [:] as LinkedHashMap<String, SimpleType>
    def complexTypes = [:] as LinkedHashMap<String, ComplexType>
    TypeRegistry typeRegistry

    Schema(aliases, simpleTypes, complexTypes, TypeRegistry typeRegistry) {
        aliases.each { this.aliases[it.name] = it }
        simpleTypes.each { this.simpleTypes[it.name] = it }
        complexTypes.each { this.complexTypes[it.name] = it }
        this.typeRegistry = typeRegistry
    }

    @Override
    String toString() {
        def builder = new StringBuilder()

        builder.append("Aliaces:\n")
        aliases.values().each { builder.append(it).append("\n") }

        builder.append("Simple types:\n")
        simpleTypes.values().each { builder.append(it).append("\n") }

        builder.append("Complex types:\n")
        complexTypes.values().each { builder.append(it).append("\n") }

        return builder.toString()
    }

    void eachType(Closure closure) {
        types.each(closure)
    }

    Collection<Type> getTypes() {
        return aliases.values() + simpleTypes.values() + complexTypes.values()
    }

    def getAliasFor(String name) {
        return aliases.values().find {
            AliasType type -> type.type.name == name
        }
    }

    def isParent(Type type) {
        return complexTypes.values().find { ComplexType complexType ->
            return complexType.inheritance.find { it.name == type.name }
        } != null
    }

    def getChildren(Type type) {
        return complexTypes.values().findAll { ComplexType complexType ->
            return complexType.inheritance.find { it.name == type.name }
        }
    }

    Type findType(String name) {
        return typeRegistry.findType(name)
    }

}
