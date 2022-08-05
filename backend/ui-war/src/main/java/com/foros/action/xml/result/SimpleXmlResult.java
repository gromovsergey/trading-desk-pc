package com.foros.action.xml.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.foros.action.xml.generator.Generator;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 19:41:54
 * Version: 1.0
 */
public class SimpleXmlResult extends AbstractXmlResult {

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
