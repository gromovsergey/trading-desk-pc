package com.foros.util.preview.token;

import com.foros.util.preview.PreviewContext;
import com.foros.util.preview.TokenDefinition;

public class StaticValueTokenDefinition implements TokenDefinition {
    private String value;

    public StaticValueTokenDefinition(String value) {
        this.value = value;
    }

    @Override
    public String evaluate(PreviewContext context) {
        return value;
    }

    @Override
    public String toString() {
        return "StaticValueTokenDefinition[" + value + "]";
    }
}
