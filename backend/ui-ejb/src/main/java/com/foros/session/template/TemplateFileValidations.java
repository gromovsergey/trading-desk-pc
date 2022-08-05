package com.foros.session.template;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.ApplicationFormat;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.model.template.TemplateFileType;
import com.foros.session.fileman.BadNameException;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.fileman.PathProviderUtil;
import com.foros.util.StringUtil;
import com.foros.util.preview.PreviewHelper;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;
import org.apache.commons.lang.ObjectUtils;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.io.InputStream;

@LocalBean
@Stateless
@Validations
public class TemplateFileValidations {

    @EJB
    private TemplateService templateService;

    @EJB
    private PathProviderService pathProviderService;

    @Validation
    public void validateDelete(ValidationContext context, TemplateFile file) {
        if (templateService.isCreativeSizeLinkedToCreatives(file)) {
            context.addConstraintViolation("CreativeTemplateFile.linked.delete");
        }
    }

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) TemplateFile file) {
        validateIfDuplicated(context, file);
        validateFileContents(context, file, "templateFile");
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) TemplateFile file) {
        validateIfFileInUse(context, file);
        validateIfDuplicated(context, file);
        validateFileContents(context, file, "templateFile");
    }

    private void validateIfFileInUse(ValidationContext context, TemplateFile file) {

        TemplateFile persistentFile = templateService.findTemplateFileById(file.getId());
        CreativeSize systemCreativeSize = persistentFile.getCreativeSize();
        ApplicationFormat systemApplicationFormat = persistentFile.getApplicationFormat();

        boolean creativeSizeChanged = !ObjectUtils.equals(systemCreativeSize.getId(), file.getCreativeSize().getId());
        boolean appFormatChanged = !ObjectUtils.equals(systemApplicationFormat, file.getApplicationFormat());
        if (creativeSizeChanged || appFormatChanged) {
            boolean templateInUse = templateService.isCreativeSizeLinkedToCreatives(persistentFile);
            if (templateInUse) {
                if (creativeSizeChanged) {
                    context.addConstraintViolation("CreativeTemplate.size.linked.update").withPath("creativeSize");
                }
                if (appFormatChanged) {
                    context.addConstraintViolation("CreativeTemplate.applicationFormat.linked.update").withPath("applicationFormat");
                }
            }
        }

    }

    private void validateIfDuplicated(ValidationContext context, TemplateFile file) {
        Template template = templateService.findById(file.getTemplate().getId());
        for (TemplateFile anotherFile : template.getTemplateFiles()) {
            if (!anotherFile.getId().equals(file.getId()) &&
                    anotherFile.getCreativeSize().getId().equals(file.getCreativeSize().getId()) &&
                    anotherFile.getApplicationFormat().getId().equals(file.getApplicationFormat().getId())) {
                context.addConstraintViolation("CreativeTemplate.invalid.duplTemplateFile");
                break;
            }
        }
    }

    public void validateFileContents(ValidationContext context, TemplateFile file, String path) {
        if (context.hasViolation("templateFile")) {
            return;
        }

        PathProvider templatesPP = pathProviderService.getTemplates();
        FileSystem templatesFS = pathProviderService.createFileSystem(templatesPP);
        String inputFileName = file.getTemplateFile();

        // validate file existence
        try {
            if (StringUtil.isPropertyNotEmpty(inputFileName) && !templatesFS.checkExist(inputFileName)) {
                addFileNotFoundViolation(context, inputFileName, path);
                return;
            }
        } catch (BadNameException ex) {
            addFileNotFoundViolation(context, inputFileName, path);
            return;
        }

        if (!templatesFS.checkExist(inputFileName)) {
            addFileNotFoundViolation(context, inputFileName, path);
            return;
        }

        if (file.getType().equals(TemplateFileType.XSLT)) {
            // validate XSLT file
            try(InputStream input = templatesFS.readFile(inputFileName)) {
                String uriResolverDir = templatesFS.getParent(inputFileName);
                PathProvider pp = PathProviderUtil.getNested(templatesPP, uriResolverDir);
                FileSystem uriResolverFS = pathProviderService.createFileSystem(pp);

                PreviewHelper.validateXSLT(input, uriResolverFS);
            } catch (Exception e) {
                context
                    .addConstraintViolation("errors.fileInvalidXSLT")
                    .withParameters(inputFileName)
                    .withPath(path);
            }
        }
    }

    private void addFileNotFoundViolation(ValidationContext context, String fileName, String path) {
        context
            .addConstraintViolation("errors.fileNotFound")
            .withParameters(fileName)
            .withPath(path);
    }
}
