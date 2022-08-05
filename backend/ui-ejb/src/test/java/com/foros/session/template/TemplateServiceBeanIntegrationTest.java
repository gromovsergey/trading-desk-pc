package com.foros.session.template;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.ApproveStatus;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.model.template.TemplateTO;
import com.foros.test.factory.ApplicationFormatTestFactory;
import com.foros.test.factory.CreativeCategoryTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;

import group.Db;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class TemplateServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private OptionService optionService;
    @Autowired
    private CreativeCategoryTestFactory creativeCategoryTestFactory;
    @Autowired
    private ApplicationFormatTestFactory applicationFormatTestFactory;
    @Autowired
    private CreativeSizeTestFactory creativeSizeTestFactory;

    @Autowired
    private CreativeTemplateTestFactory templateTF;

    @Autowired
    private OptionGroupTestFactory optionGroupTF;

    @Test
    public void testCreativeTemplateCreateCopy() throws Exception {
        CreativeCategory category = creativeCategoryTestFactory.createPersistent(CreativeCategoryType.VISUAL, ApproveStatus.APPROVED);

        CreativeTemplate template = new CreativeTemplate();
        template.setDefaultName("automated test template name");
        template.setCategories(new HashSet<CreativeCategory>());
        template.getCategories().add(category);
        templateService.create(template);
        entityManager.flush();
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        createOptionPersisted(optionGroup);

        TemplateFile tmplFile = new TemplateFile();
        tmplFile.setCreativeSize(creativeSizeTestFactory.createPersistent());
        tmplFile.setApplicationFormat(applicationFormatTestFactory.createPersistent());
        tmplFile.setTemplate(template);
        tmplFile.setTemplateFile("/1.txt");
        templateService.createTemplateFile(tmplFile);
        entityManager.flush();
        Template copy = templateService.createCopy(template.getId());
        assertNotNull(copy.getId());
        assertEquals("Copy of automated test template name", copy.getDefaultName());
        assertEquals("Status wasn't set", template.getStatus(), copy.getStatus());
        assertEquals("Option Group names doesn't equals each other", template.getOptionGroups().iterator().next().getDefaultName(), copy.getOptionGroups().iterator().next().getDefaultName());
        assertNotSame("Option Group ids equals each other", template.getOptionGroups().iterator().next().getId(), copy.getOptionGroups().iterator().next().getId());
        assertEquals("Option names doesn't equals each other", template.getAllOptions().iterator().next().getDefaultName(), copy.getAllOptions().iterator().next().getDefaultName());
        assertNotSame("Option ids equals each other", template.getAllOptions().iterator().next().getId(), copy.getAllOptions().iterator().next().getId());
    }

    @Test
    public void testDiscoverTemplateCreateCopy() throws Exception {
        DiscoverTemplate template = new DiscoverTemplate();
        template.setDefaultName("automated test template name");
        template.setTemplateFiles(new HashSet<TemplateFile>());

        templateService.create(template);
        entityManager.flush();
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        createOptionPersisted(optionGroup);

        Template copy = templateService.createCopy(template.getId());
        assertNotNull(copy.getId());
        assertEquals("Copy of automated test template name", copy.getDefaultName());
        assertEquals("Status wasn't set", template.getStatus(), copy.getStatus());
        assertEquals("Option Group names doesn't equals each other", template.getOptionGroups().iterator().next().getDefaultName(), copy.getOptionGroups().iterator().next().getDefaultName());
        assertNotSame("Option Group ids equals each other", template.getOptionGroups().iterator().next().getId(), copy.getOptionGroups().iterator().next().getId());
        assertEquals("Option names doesn't equals each other", template.getAllOptions().iterator().next().getDefaultName(), copy.getAllOptions().iterator().next().getDefaultName());
        assertNotSame("Option ids equals each other", template.getAllOptions().iterator().next().getId(), copy.getAllOptions().iterator().next().getId());
    }


    private void createOptionPersisted(OptionGroup optionGroup) {
        Option option = new Option();
        option.setDefaultLabel("automated test option label");
        option.setDefaultName("automated test option name");
        option.setOptionGroup(optionGroup);
        option.setSortOrder(0);
        option.setType(OptionType.STRING);
        option.setToken("automated test token");
        optionService.create(option);
    }

    @Test
    public void testSearch() {
        CreativeTemplate creativeTemplate = templateTF.createPersistent();
        templateService.delete(creativeTemplate.getId());
        DiscoverTemplate discoverTemplate = new DiscoverTemplate();
        discoverTemplate.setDefaultName("automated test template name");
        discoverTemplate.setTemplateFiles(new HashSet<TemplateFile>());
        templateService.create(discoverTemplate);
        templateService.delete(discoverTemplate.getId());
        commitChanges();

        setDeletedObjectsVisible(true);
        List<TemplateTO> result = templateService.findAllCreativeTemplates();
        assertTrue(containsTemplate(result, creativeTemplate));
        result = templateService.findAllDiscoverTemplates();
        assertTrue(containsTemplate(result, discoverTemplate));
        setDeletedObjectsVisible(false);
        result = templateService.findAllNonDeletedCreativeTemplates();
        assertTrue(!containsTemplate(result, creativeTemplate));
        result = templateService.findAllNonDeletedDiscoverTemplates();
        assertTrue(!containsTemplate(result, discoverTemplate));
    }

    private boolean containsTemplate(List<TemplateTO> templates, Template template) {
        for (TemplateTO templateTo : templates) {
            if (templateTo.getId().equals(template.getId())) {
                return true;
            }
        }
        return false;
    }
}
