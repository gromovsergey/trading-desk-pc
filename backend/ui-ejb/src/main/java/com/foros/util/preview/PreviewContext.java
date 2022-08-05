package com.foros.util.preview;

import com.foros.model.template.CreativeToken;
import com.foros.model.template.OptionValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class PreviewContext {
    private PreviewContext parent;
    private OptionValueSource optionValueSource;

    private Stack<String> evaluationStack = new Stack<>();
    private Map<String, TokenDefinition> tokenDefinitions = new HashMap<>();
    private Map<String, String> contextValues = new HashMap<>();
    private Map<String, String> evaluatedValues = new HashMap<>();

    public PreviewContext(Map<String, TokenDefinition> tokenDefinitions, OptionValueSource optionValueSource) {
        this.tokenDefinitions = tokenDefinitions;
        this.optionValueSource = optionValueSource;
    }

    public Map<String, String> evaluateAll() {
        for (String token : tokenDefinitions.keySet()) {
            evaluateToken(token);
        }
        return evaluatedValues;
    }

    public String evaluateToken(CreativeToken token) {
        return evaluateToken(token.getName());
    }

    public String evaluateToken(String name) {
        String value = evaluatedValues.get(name);
        if (value != null) {
            return value;
        }

        if (!evaluationStack.contains(name)) {
            evaluationStack.push(name);
            try {
                TokenDefinition definition = tokenDefinitions.get(name);
                if (definition == null) {
                    // no definition, it must be parent or missing
                    if (parent != null) {
                        return parent.evaluateToken(name);
                    } else {
                        return "";
                    }
                }
                value = definition.evaluate(this);
            } finally {
                evaluationStack.pop();
            }
        } else {
            // break the cycle
            value = "";
        }

        if (value == null) {
            value = "";
        }
        evaluatedValues.put(name, value);

        return value;
    }


    public OptionValue getOptionValue(Long optionId) {
        return optionValueSource.get(optionId);
    }

    public String getContextValue(String name) {
        String value = contextValues.get(name);
        if (value == null && !contextValues.containsKey(name)) {
            throw new IllegalArgumentException(name);
        }
        return value;
    }

    public void putContextValue(String name, String value) {
        contextValues.put(name, value);
    }

    public PreviewContext getParent() {
        return parent;
    }

    public void setParent(PreviewContext parent) {
        this.parent = parent;
    }
}
