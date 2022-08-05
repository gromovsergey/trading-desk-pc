package com.foros.action.admin.template.file;

import com.foros.validation.constraint.violation.ConstraintViolationException;

public class DeleteCreativeTemplateFileAction extends CreativeTemplateFileActionSupport {

    private Long id;

    public String delete() {
        file = templateService.findTemplateFileById(id);
        try {
            templateService.deleteTemplateFile(file.getId());
            return SUCCESS;
        } catch (ConstraintViolationException e) {
            return "input_error";
        }
    }

    public void setId(Long id) {
        this.id = id;
    }
}
