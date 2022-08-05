package com.foros.action.xml.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.foros.action.xml.generator.Generator;
import com.foros.action.xml.generator.StringErrorGenerator;
import com.foros.action.xml.generator.ValidationExceptionGenerator;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 19:32:45
 * Version: 1.0
 */
public class ValidationXmlResult extends AbstractXmlResult {

    @Override
    protected Generator createGenerator(ActionInvocation invocation) {
        Object action = invocation.getAction();

        if (action instanceof ValidationAware) {
            ValidationAware validationAware = (ValidationAware) action;
            if (validationAware.hasErrors()) {
                return new ValidationExceptionGenerator(
                        validationAware.getFieldErrors(),
                        validationAware.getActionErrors(),
                        validationAware.getActionMessages()
                );
            }
        }

        return new StringErrorGenerator("Action not support validation.");
    }

}