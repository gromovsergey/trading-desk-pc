package com.foros.session.admin;

import static com.foros.model.creative.CreativeCategoryType.VISUAL;
import com.foros.AbstractValidationsTest;
import com.foros.model.ApproveStatus;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.session.creative.DisplayCreativeServiceBean;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.template.OptionGroupService;
import com.foros.session.template.TemplateService;
import com.foros.test.factory.CreativeCategoryTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DiscoverTemplateTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;

import group.Db;
import group.Validation;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class TemplateValidationsTest  extends AbstractValidationsTest {

    @Autowired
    protected TemplateService templateService;

    @Autowired
    protected OptionGroupService optionGroupService;

    @Autowired
    private DisplayCreativeTestFactory creativeTF;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private DiscoverTemplateTestFactory discoverTemplateTF;

    @Autowired
    public PathProviderService pathProviderService;

    @Autowired
    private CreativeCategoryTestFactory creativeCategoryTF;

    @Autowired
    private CreativeSizeTestFactory sizeTF;

    @Autowired
    private DisplayCreativeServiceBean displayCreativeService;

    @Test
    public void testValidateCreate() throws Exception {
        DiscoverTemplate template = discoverTemplateTF.create();

        validate("Template.create", template);
    }

    @Test
    public void testValidateUpdate() throws Exception {
        DiscoverTemplate template = discoverTemplateTF.createPersistent();
        validate("Template.update", template);
    }

    @Test
    public void testValidateUpdateInvalid() throws Exception {
        FileSystem fs = pathProviderService.getTemplates().createFileSystem();
        try (OutputStream os = fs.openFile("filename.xml")) {
            os.write(0);
        }

        DiscoverTemplate template = discoverTemplateTF.createPersistent();

        template.setDefaultName("");
        template.getTemplateFiles().iterator().next().setTemplateFile("");

        validate("Template.update", template);
        assertHasViolation("files[0].templateFile");
        assertHasViolation("defaultName");
        assertViolationsCount(2);

        fs.delete("filename.xml");
    }

    @Test
    public void testValidateUpdateNotExists() throws Exception {
        DiscoverTemplate template = discoverTemplateTF.createPersistent();

        validate("Template.update", template);

        assertHasViolation("files[1].templateFile");
        assertHasViolation("files[2].templateFile");
        assertViolationsCount(template.getTemplateFiles().size());
    }

    @Test
    public void testUpdateTemplateRemovalOfLinkedVisualCategories() {
        CreativeTemplate template = creativeTemplateTF.create();
        Set<CreativeCategory> creativeCategorySet = new HashSet<CreativeCategory>();
        CreativeCategory category1 = creativeCategoryTF.createPersistent(VISUAL, ApproveStatus.APPROVED);
        creativeCategorySet.add(category1);
        creativeCategorySet.add(creativeCategoryTF.createPersistent(VISUAL, ApproveStatus.APPROVED));
        creativeCategorySet.add(creativeCategoryTF.createPersistent(VISUAL, ApproveStatus.APPROVED));
        template.setCategories(creativeCategorySet);
        creativeTemplateTF.persist(template);

        Creative creative = creativeTF.createPersistent();
        CreativeSize size = sizeTF.createPersistent();
        creative.setTemplate(template);
        creative.setSize(size);
        displayCreativeService.update(creative);

        CreativeTemplate existingTemplate = creativeTemplateTF.copy(template);
        Set<CreativeCategory> newCreativeCategorySet = new HashSet<CreativeCategory>(template.getCategories());
        newCreativeCategorySet.remove(category1);
        existingTemplate.setCategories(newCreativeCategorySet);
        validate("Template.update", existingTemplate);
        assertViolationsCount(0);

        existingTemplate = creativeTemplateTF.copy(template);
        existingTemplate.setCategories(new HashSet<CreativeCategory>());
        validate("Template.update", existingTemplate);
        assertViolationsCount(1);
    }
}
