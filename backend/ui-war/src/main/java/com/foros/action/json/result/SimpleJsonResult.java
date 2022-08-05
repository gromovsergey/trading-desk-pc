package com.foros.action.json.result;

import com.foros.action.xml.generator.Generator;

import com.opensymphony.xwork2.ActionInvocation;

public class SimpleJsonResult extends AbstractJsonResult {
    private String generator;

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    protected Generator createGenerator(ActionInvocation invocation) throws Exception {
        return (Generator) Class.forName(generator).newInstance();
    }
}
