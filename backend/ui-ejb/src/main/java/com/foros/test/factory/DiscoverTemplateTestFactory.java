package com.foros.test.factory;

import com.foros.model.template.*;
import com.foros.session.template.ApplicationFormatService;
import com.foros.session.template.TemplateService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.*;

@Stateless
@LocalBean
public class DiscoverTemplateTestFactory extends TestFactory<DiscoverTemplate> {
    @EJB
    private TemplateService templateService;

    @EJB
    private ApplicationFormatService applicationFormatService;

    @EJB
    private CreativeSizeTestFactory creativeSizeTF;

    @EJB
    private ApplicationFormatTestFactory applicationFormatTF;

    public void populate(DiscoverTemplate template) {
        template.setDefaultName(getTestEntityRandomName());

        ApplicationFormat[] formats = new ApplicationFormat[3];
        formats[0] = applicationFormatService.findByName(ApplicationFormat.DISCOVER_TAG_FORMAT);
        formats[1] = applicationFormatService.findByName(ApplicationFormat.DISCOVER_CUSTOMIZATION_FORMAT);
        formats[2] = applicationFormatService.findByName(ApplicationFormat.PREVIEW_FORMAT);

        Set<TemplateFile> files = new LinkedHashSet<TemplateFile>();
        for (int i = 0 ; i < formats.length ; i ++) {
            TemplateFile file = new TemplateFile();
            file.setTemplate(template);
            file.setTemplateFile("/filename.xml");
            file.setType(TemplateFileType.TEXT);
            file.setApplicationFormat(formats[i]);
            files.add(file);
        }

        template.setTemplateFiles(files);
    }

    @Override
    public DiscoverTemplate create() {
        DiscoverTemplate template = new DiscoverTemplate();
        populate(template);
        return template;
    }

    public DiscoverTemplate create(TemplateFile... files) {
        DiscoverTemplate template = create();
        if (files != null) {
            Set<TemplateFile> templateFiles = new LinkedHashSet<TemplateFile>();
            template.setTemplateFiles(templateFiles);
            for (TemplateFile file : files) {
                template.getTemplateFiles().add(file);
            }
        }
        return template;
    }

    public DiscoverTemplate createText() {
        DiscoverTemplate template = new DiscoverTemplate();
        populate(template);
        template.setDefaultName(DiscoverTemplate.TEXT_TEMPLATE);
        return template;
    }

    @Override
    public void persist(DiscoverTemplate template) {
        templateService.create(template);
    }

    public void update(DiscoverTemplate template) {
        templateService.update(template);
    }

    @Override
    public DiscoverTemplate createPersistent() {
        DiscoverTemplate template = create();
        persist(template);
        return template;
    }

    public DiscoverTemplate findText() {
        DiscoverTemplate template = findAny(DiscoverTemplate.class, new QueryParam("defaultName", DiscoverTemplate.TEXT_TEMPLATE));

        // may be only one text discover template per table
        // so create it when not found
        if (template == null) {
            template = createText();
            persist(template);
        }

        return template;
    }

    public void persistTemplateFile(TemplateFile tf) {
        templateService.createTemplateFile(tf);
    }

    public TemplateFile createTemplateFile(Template template, TemplateFileType type, String file) {
        TemplateFile tf = new TemplateFile();
        tf.setCreativeSize(creativeSizeTF.createPersistent());
        tf.setApplicationFormat(applicationFormatTF.createPersistent());
        tf.setTemplate(template);
        tf.setTemplateFile(file);
        tf.setType(type);
        return tf;
    }

    public DiscoverTemplate copy(DiscoverTemplate template) {
        DiscoverTemplate copy = new DiscoverTemplate();

        copy.setId(template.getId());
        copy.setDefaultName(template.getDefaultName());
        copy.setOptionGroups(new HashSet<OptionGroup>(template.getOptionGroups()));
        copy.setVersion(template.getVersion());

        return copy;
    }
}
