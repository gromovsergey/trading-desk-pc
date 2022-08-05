package com.foros.session.fileman;

import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.RequiredValidator;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.io.IOException;

@LocalBean
@Stateless
@Validations
public class SwfToHtmlConverterValidations {

    @Validation
    public void validateConvert(ValidationContext context,
                                FileManager fileManager,
                                String dir,
                                String swfFileName,
                                Boolean withoutClickUrlMacro,
                                String htmlWithoutClickUrlMacroFileName,
                                Boolean withClickUrlMacro,
                                String htmlWithClickUrlMacroFileName,
                                String clickMacro) {
        context.validator(RequiredValidator.class).withMessage("errors.field.required").withPath("sourceFileName").validate(swfFileName);
        if (withoutClickUrlMacro) {
            context.validator(RequiredValidator.class).withMessage("errors.field.required").withPath("targetFileName").validate(htmlWithoutClickUrlMacroFileName);
        }
        if (withClickUrlMacro) {
            context.validator(RequiredValidator.class).withMessage("errors.field.required").withPath("targetFileNameWithMacro").validate(htmlWithClickUrlMacroFileName);
            context.validator(RequiredValidator.class).withMessage("errors.field.required").withPath("clickTagSpelling").validate(clickMacro);
        }

        if (!withoutClickUrlMacro && !withClickUrlMacro) {
            context.addConstraintViolation("errors.field.required").withPath("swiffy");
        }

        if (withClickUrlMacro && withoutClickUrlMacro && htmlWithClickUrlMacroFileName.equals(htmlWithoutClickUrlMacroFileName)) {
            context.addConstraintViolation("fileman.error.fileNamesEqual").withPath("swiffy");
        }

        try {
            if (!StringUtil.isPropertyEmpty(swfFileName) && !fileManager.checkExist(dir, swfFileName)) {
                context.addConstraintViolation("fileman.error.fileNotExists")
                        .withParameters(swfFileName)
                        .withPath("sourceFileName");
            }
        } catch (IOException ignored) {
        }
    }
}
