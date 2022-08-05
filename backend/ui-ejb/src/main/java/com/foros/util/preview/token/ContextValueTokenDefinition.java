package com.foros.util.preview.token;

import com.foros.util.preview.PreviewContext;
import com.foros.util.preview.TokenDefinition;

public class ContextValueTokenDefinition implements TokenDefinition {
    private String name;

    public ContextValueTokenDefinition(String name) {
        this.name = name;
    }

    @Override
    public String evaluate(PreviewContext context) {
        return context.getContextValue(name);
    }

    @Override
    public String toString() {
        return "ContextValueTokenDefinition[" + name + "]";
    }
}
