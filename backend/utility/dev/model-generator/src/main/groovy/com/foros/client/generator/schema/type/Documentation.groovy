package com.foros.client.generator.schema.type

class Documentation {

    String text

    Documentation(String text) {
        this.text = text
    }

    def eachLine(Closure closure) {
        text?.split("\n")?.each { line ->
            closure(line)
        }
    }

    public String toString() {
        return "/**\n*${text}\n*/";
    }

}
