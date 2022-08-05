package com.foros.action.xml.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.foros.action.xml.generator.ExceptionGenerator;
import com.foros.action.xml.generator.Generator;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 19:32:45
 * Version: 1.0
 */
public class ExceptionXmlResult extends AbstractXmlResult {

    @Override
    protected Generator createGenerator(ActionInvocation invocation) {
        return new ExceptionGenerator();
    }

}
