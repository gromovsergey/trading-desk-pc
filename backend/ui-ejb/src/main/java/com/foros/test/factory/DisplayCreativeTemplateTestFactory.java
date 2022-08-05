package com.foros.test.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.model.template.TemplateFileType;
import com.foros.session.template.TemplateService;
import com.foros.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Stateless
@LocalBean
public class DisplayCreativeTemplateTestFactory extends TestFactory<CreativeTemplate> {
    @Autowired
    private TemplateService templateService;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private ApplicationFormatTestFactory applicationFormatTF;

    @Autowired
    private AdvertiserAccountTypeTestFactory advertiserAccountTypeTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    public CreativeTemplate create(AdvertiserAccount account) {
        CreativeTemplate template = new CreativeTemplate();
        template.setDefaultName(getTestEntityRandomName());
        Set<TemplateFile> templateFiles = new LinkedHashSet<TemplateFile>();
        template.setTemplateFiles(templateFiles);

        CreativeSize size = creativeSizeTF.createPersistent();
        TemplateFile templateFile = createTemplateFile(template, TemplateFileType.TEXT, "html", size, "/tmp/file.html");
        template.getTemplateFiles().add(templateFile);

        account.getAccountType().getCreativeSizes().add(size);
        account.getAccountType().getTemplates().add(template);
        return template;
    }

    @Override
    public CreativeTemplate create() {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setCPAFlag(CCGType.DISPLAY, true);
        advertiserAccountTypeTF.persist(accountType);
        AdvertiserAccount account = advertiserAccountTF.createPersistent(accountType);
        return create(account);
    }

    @Override
    public void persist(CreativeTemplate template) {
        templateService.create(template);
    }

    public void update(CreativeTemplate template) {
        templateService.update(template);
    }

    @Override
    public CreativeTemplate createPersistent() {
        CreativeTemplate template = create();
        persist(template);
        return template;
    }

    public CreativeTemplate createPersistent(AdvertiserAccount account) {
        CreativeTemplate template = create(account);
        persist(template);
        return template;
    }

    public void persistTemplateFile(TemplateFile tf) {
        templateService.createTemplateFile(tf);
    }

    public TemplateFile createTemplateFile(Template template, TemplateFileType type, String file) {
        return createTemplateFile(template, type, null, creativeSizeTF.createPersistent(), file);
    }

    public TemplateFile createTemplateFile(Template template, TemplateFileType type, String appFmt, CreativeSize size, String file) {
        TemplateFile tf = new TemplateFile();
        tf.setCreativeSize(size);
        tf.setApplicationFormat(StringUtil.isPropertyEmpty(appFmt) ? applicationFormatTF.createPersistent() : applicationFormatTF.findByName(appFmt));
        tf.setTemplate(template);
        tf.setTemplateFile(file);
        tf.setType(type);
        return tf;
    }

    public TemplateFile createPersistentTemplateFile(Template template, TemplateFileType type, String file) {
        TemplateFile tf = createTemplateFile(template, type, file);
        persistTemplateFile(tf);
        return tf;
    }

    public TemplateFile createPersistentTemplateFile(Template template, TemplateFileType type, String appFmt,  CreativeSize size, String file) {
        TemplateFile tf = createTemplateFile(template, type, appFmt, size, file);
        persistTemplateFile(tf);
        return tf;
    }

    public CreativeTemplate copy(CreativeTemplate template) {
        CreativeTemplate templateCopy = new CreativeTemplate();

        templateCopy.setId(template.getId());
        templateCopy.setDefaultName(template.getDefaultName());
        templateCopy.setOptionGroups(new HashSet<OptionGroup>(template.getOptionGroups()));
        templateCopy.setVersion(template.getVersion());

        return templateCopy;
    }
}
