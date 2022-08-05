package com.foros.action.xml.generator;

import com.foros.util.xml.generator.XmlGenerator;

public class BooleanGenerator implements Generator<Boolean>{
    public String generate(Boolean model) {
        XmlGenerator generator = new XmlGenerator();
        generator.root("result").text(String.valueOf(model));
        return generator.asString();
    }
}
