package com.foros.client.generator.template.java

import com.foros.client.generator.schema.type.ArrayType
import com.foros.client.generator.schema.type.ComplexType
import com.foros.client.generator.schema.type.Field
import com.foros.client.generator.schema.type.GenericType
import com.foros.client.generator.schema.type.LazyType
import com.foros.client.generator.schema.type.Parametrized
import com.foros.client.generator.schema.type.ParametrizedType
import com.foros.client.generator.schema.type.Type

class JavaTemplateUtil {

    static primitives = [
            "xs:string": new BuildInType("String", "java.lang"),
            "oui:entity-id": new BuildInType("Long", "java.lang"),
            "xs:long": new BuildInType("Long", "java.lang"),
            "xs:byte": new BuildInType("Byte", "java.lang"),
            "xs:int": new BuildInType("Integer", "java.lang"),
            "xs:boolean": new BuildInType("Boolean", "java.lang"),
            "xs:decimal": new BuildInType("BigDecimal", "java.math"),
            "xs:date": new BuildInType("XMLGregorianCalendar", "javax.xml.datatype"),
            "xs:dateTime": new BuildInType("XMLGregorianCalendar", "javax.xml.datatype"),
            "xs:time": new BuildInType("XMLGregorianCalendar", "javax.xml.datatype")
    ]

    static String resolveTypeName(Type type) {
        if (type == null) {
            return "void"
        } else if (type instanceof ArrayType) {
            return "List<" + resolveTypeName(type.contentTypes[0]) + ">"
        } else if (type instanceof Parametrized && type.typeParameters) {
            return "${type.name}<" + type.typeParameters.collect { resolveTypeName(it) }.join(",") + ">"
        } else {
            return resolveBuildInType(type).name
        }
    }

    static Collection<String> resolveImports(String packageName, String rootPackage, Collection<Type> types) {
        def res = new TreeSet<String>()
        types.each {
            it = resolveBuildInType(it)

            if (it instanceof ParametrizedType) {
                res.addAll(resolveImports(packageName, rootPackage, Collections.singleton(it.type) + it.typeParameters))
            } else if (it instanceof GenericType) {
                res.addAll(resolveImports(packageName, rootPackage, it.inheritance))
            } else if (it instanceof ArrayType) {
                res.add("java.util.List")
                res.add("java.util.ArrayList")
                res.addAll(resolveImports(packageName, rootPackage, it.contentTypes))
            } else {
                if (!packageName.equals(it.packageName)) {
                    def name
                    if (it.packageName != null) {
                        name = it.packageName + "." + it.name
                    } else {
                        name = it.name
                    }
                    if (!(it instanceof BuildInType)) {
                        name = rootPackage + "." + name;
                    }
                    res.add(name)
                }
            }
        }
        return res;
    }

    static String upperFirstLetter(String name) {
        if (name.length() == 1) {
            return name.toUpperCase();
        } else {
            return name[0].toUpperCase() + name[1..name.length() - 1]
        }
    }

    static String lowerFirstLetter(String name) {
        if (name.length() == 1) {
            return name.toLowerCase();
        } else {
            return name[0].toLowerCase() + name[1..name.length() - 1]
        }
    }

    static String toCamelStyle(String typeName) {
        return upperFirstLetter(typeName)
    }

    static String makeVariable(String name) {
        return lowerFirstLetter(name)
    }

    static  Type resolveBuildInType(Type type) {
        return primitives[type.name] ?: type;
    }

    static boolean isGeneric(Type type) {
        return type instanceof GenericType
    }

    static boolean isComplex(Type type) {
        return unLazy(type) instanceof ComplexType
    }

    static boolean isArray(Type type) {
        return type instanceof ArrayType
    }

    static boolean isAttribute(Field field) {
        return field.meta["attribute"]
    }

    static String defaultValue(Field field) {
        if (!field.meta["defaultValue"]) {
            return null;
        }
        if (isArray(field.type)) {
            return "new ArrayList<" + resolveTypeName(field.type.contentTypes[0]) + ">()";
        }
        return null;
    }

    List<Field> getAllFields(Type type) {
        if (type instanceof ComplexType) {
            List<Field> all = []
            for (field in type.fields) {
                if (field.meta["subElements"])
                    all = all + field.meta["subElements"]
                else
                    all = all + field
            }
            for (parent in type.inheritance) {
                all = getAllFields(parent) + all
            }
            return all
        } else if (type instanceof LazyType) {
            return getAllFields(type.real())
        }
        return [];
    }

    Type getRealType(Type type) {
        if (type instanceof LazyType)
            return getRealType(type.real())
        if (type instanceof ParametrizedType)
            return getRealType(type.type)
        if (type instanceof ArrayType)
            return getRealType(type.contentTypes[0])
        return type;
    }

    String getRealName(Field field) {
        if (field.type instanceof ArrayType)
            return field.meta.elementName
        return field.name;
    }

    private static class BuildInType extends Type {
        BuildInType(String name, String packageName) {
            super(name, null)
            this.packageName = packageName;
        }
    }

    public static Type unLazy(Type type) {
        if (type instanceof LazyType) {
            type = type.real();
        }
        return type;
    }
}
