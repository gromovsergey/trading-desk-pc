package com.foros.action.xml.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.foros.action.xml.generator.Generator;
import com.foros.action.xml.generator.OptionsGenerator;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 19:26:54
 * Version: 1.0
 */
public class OptionsXmlResult extends AbstractXmlResult {

    @Override
    protected Generator createGenerator(ActionInvocation invocation) {
        return new OptionsGenerator();
    }

}
