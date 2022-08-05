package com.foros.test.factory;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.model.template.TemplateFileType;
import com.foros.session.template.TemplateService;
import com.foros.util.StringUtil;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
@Deprecated // Use Display/TextCreativeTemplateTestFactory
public class CreativeTemplateTestFactory extends TestFactory<CreativeTemplate> {
    @EJB
    private TemplateService templateService;

    @EJB
    private CreativeSizeTestFactory creativeSizeTF;

    @EJB
    private ApplicationFormatTestFactory applicationFormatTF;

    public void populate(CreativeTemplate template) {
        template.setDefaultName(getTestEntityRandomName());
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    @Override
    public CreativeTemplate create() {
        CreativeTemplate template = new CreativeTemplate();
        populate(template);
        return template;
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    public CreativeTemplate create(TemplateFile... files) {
        CreativeTemplate template = create();
        if (files != null) {
            Set<TemplateFile> templateFiles = new LinkedHashSet<TemplateFile>();
            template.setTemplateFiles(templateFiles);
            for (TemplateFile file : files) {
                template.getTemplateFiles().add(file);
            }
        }
        return template;
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    public CreativeTemplate createText() {
        CreativeTemplate template = new CreativeTemplate();
        populate(template);
        template.setDefaultName(CreativeTemplate.TEXT_TEMPLATE);
        return template;
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    @Override
    public void persist(CreativeTemplate template) {
        templateService.create(template);
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    public void update(CreativeTemplate template) {
        templateService.update(template);
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    @Override
    public CreativeTemplate createPersistent() {
        CreativeTemplate template = create();
        persist(template);
        return template;
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    public CreativeTemplate findText() {
        CreativeTemplate template = findAny(CreativeTemplate.class, new QueryParam("defaultName", CreativeTemplate.TEXT_TEMPLATE));

        // may be only one text creative template per table
        // so create it when not found
        if (template == null) {
            template = createText();
            persist(template);
        }

        return template;
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    public void persistTemplateFile(TemplateFile tf) {
        templateService.createTemplateFile(tf);
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    public TemplateFile createTemplateFile(Template template, TemplateFileType type, String file) {
        return createTemplateFile(template, type, null, creativeSizeTF.createPersistent(), file);
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    public TemplateFile createTemplateFile(Template template, TemplateFileType type, String appFmt, CreativeSize size, String file) {
        TemplateFile tf = new TemplateFile();
        tf.setCreativeSize(size);
        tf.setApplicationFormat(StringUtil.isPropertyEmpty(appFmt) ? applicationFormatTF.createPersistent() : applicationFormatTF.findByName(appFmt));
        tf.setTemplate(template);
        tf.setTemplateFile(file);
        tf.setType(type);
        return tf;
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    public TemplateFile createPersistentTemplateFile(Template template, TemplateFileType type, String file) {
        TemplateFile tf = createTemplateFile(template, type, file);
        persistTemplateFile(tf);
        return tf;
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    public TemplateFile createPersistentTemplateFile(Template template, TemplateFileType type, String appFmt,  CreativeSize size, String file) {
        TemplateFile tf = createTemplateFile(template, type, appFmt, size, file);
        persistTemplateFile(tf);
        return tf;
    }

    @Deprecated // Use Display/TextCreativeTemplateTestFactory
    public CreativeTemplate copy(CreativeTemplate template) {
        CreativeTemplate templateCopy = new CreativeTemplate();

        templateCopy.setId(template.getId());
        templateCopy.setDefaultName(template.getDefaultName());
        templateCopy.setOptionGroups(new HashSet<OptionGroup>(template.getOptionGroups()));
        templateCopy.setVersion(template.getVersion());
        
        return templateCopy;
    }
}
