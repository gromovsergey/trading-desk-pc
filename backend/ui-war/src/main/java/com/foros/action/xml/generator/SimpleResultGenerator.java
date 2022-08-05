package com.foros.action.xml.generator;

import com.foros.util.StringUtil;
import com.foros.util.xml.generator.XmlGenerator;

public class SimpleResultGenerator implements Generator<Object> {
    @Override
    public String generate(Object model) {
        XmlGenerator generator = new XmlGenerator();
        generator.root("result").text(StringUtil.toString(model));
        return generator.asString();
    }
}
