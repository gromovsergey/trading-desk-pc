package com.foros.session.creative;

import static com.foros.test.CustomAsserts.assertEqualsBean;
import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.config.MockConfigService;
import com.foros.model.Status;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.model.template.TemplateFileType;
import com.foros.model.template.TemplateTO;
import com.foros.session.BusinessException;
import com.foros.session.template.TemplateService;
import com.foros.test.factory.AdvertiserAccountTypeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;

import group.Db;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class CreativeTemplateServiceIntegrationTest extends AbstractServiceBeanIntegrationTest {
    private static final String FILE_NAME = "/file.foros-ui";
    private static final String FILE_NAME2 = "/modifiedFile.js";

    @Autowired
    private TemplateService templateService;

    @Autowired
    private MockConfigService mockConfigService;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private AdvertiserAccountTypeTestFactory accountTypeTF;

    @Test
    public void testFindAllNonDeleted() {
        List<TemplateTO> all = templateService.findAllNonDeletedCreativeTemplates();
        assertEquals("JDBC query must show the same number of CreativeTemplate",
                (int) (jdbcTemplate.queryForObject("SELECT COUNT(0) FROM TEMPLATE WHERE TEMPLATE_TYPE='CREATIVE' AND NOT STATUS = 'D'", Integer.class)),
                all.size());
    }

    @Test
    public void testCreate() {
        CreativeTemplate e = creativeTemplateTF.createPersistent();

        CreativeTemplate found = (CreativeTemplate) templateService.findById(e.getId());
        assertSame("Entity is not created properly", e, found);
    }

    /*@Test todo: OUI-21610 */
    public void update() {
        CreativeTemplate template = creativeTemplateTF.createPersistent();

        Option co = new Option(3L);
        Collection<Option> opts = template.getAllOptions();
        Option coRemoved = opts.iterator().next();
        opts.remove(coRemoved);

        int len = opts.size();

        Option to = new Option(co.getId());
        template.getAllOptions().add(to);
        template.setDefaultName("NewName");

        templateService.update(template);
        Template eUpdated = templateService.findById(template.getId());

        assertEqualsBean(template, eUpdated);
        assertEquals(eUpdated.getName().getDefaultName(), "NewName");
        assertEquals(eUpdated.getAllOptions().size(), len + 1);
        assertTrue(eUpdated.getAllOptions().contains(to));
        assertFalse(eUpdated.getAllOptions().contains(coRemoved));
    }

    @Test
    public void testDeleteUndelete() {
        CreativeTemplate template = creativeTemplateTF.createPersistent();
        templateService.delete(template.getId());

        assertEquals(Status.DELETED, templateService.findById(template.getId()).getStatus());

        templateService.undelete(template.getId());
        assertEquals(Status.ACTIVE, templateService.findById(template.getId()).getStatus());
    }

    @Test
    public void testFindJsHTMLTemplatesBySize() {
        AccountType at = accountTypeTF.createPersistent();
        // TODO: Add sizes and accounts to the the account type
        Long sizeId = 1L;

        List<Template> cts = templateService.findJsHTMLTemplatesBySize(at.getId(), sizeId);
        int totalRecordsExpected = jdbcTemplate.queryForInt(
                "select count(distinct ct.template_id) from template ct " +
                        "inner Join templatefile ctf on ct.template_id = ctf.template_id " +
                        "Inner Join ACCOUNTTYPECREATIVETEMPLATE act on act.template_id = ct.template_id " +
                        "Inner Join appformat af on af.app_format_id = ctf.app_format_id " +
                        "where act.ACCOUNT_TYPE_ID = " + at.getId() + " and ct.name <> 'Text' and " +
                        " af.name in ('js', 'html') and ctf.size_id = " + sizeId);
        assertEquals("JDBC query must show the same number of CreativeTemplate", totalRecordsExpected, cts.size());
    }

    @Test
    public void testCreativeSizeLinkedToCreatives() {
        Long templateFileId = 464L;
        Long templateId = 42L;
        Long sizeId = 12L;
        TemplateFile templateFile = new TemplateFile(templateFileId);
        templateFile.setCreativeSize(new CreativeSize(sizeId));
        templateFile.setTemplate(new CreativeTemplate(templateId));

        boolean linkedCreatives = templateService.isCreativeSizeLinkedToCreatives(templateFile);
        int totalRecordsExpected = jdbcTemplate.queryForInt(
                "select count(distinct ct.template_id) from template ct " +
                        "inner Join creative c on c.template_id = ct.template_id " +
                        "inner Join templatefile ctf on ct.template_id = ctf.template_id " +
                        "Inner Join appformat af on af.app_format_id = ctf.app_format_id " +
                        "where ctf.template_file_id = " + templateFileId + " and ct.name <> 'Text' and " +
                        " af.name in ('js', 'html') and c.size_id = " + sizeId + " and c.template_id = " + templateId);
        assertEquals(linkedCreatives, totalRecordsExpected > 0 );
    }

    @Test
    public void testCreativeTemplateLinkedToCreatives() {
        Long templateId = (long) 42;
        boolean linkedCreatives = templateService.isCreativeTemplateLinkedToCreatives(templateId);
        int totalRecordsExpected = jdbcTemplate.queryForInt(
                "select count(distinct ct.template_id) from template ct " +
                        "inner join creative c on c.template_id = ct.template_id " +
                        "where ct.template_id = " + templateId + " and c.status <> 'D' ");
        assertEquals(linkedCreatives, totalRecordsExpected > 0 );
    }

    @Test
    public void testCreativeTemplateLinkedToCreativeSize() {
        Long accountId = 46L;
        Long sizeId = 1L;
        Long templateId = 2L;
        boolean linkedRecords = templateService.isTemplateLinkedToCreativeSize(accountId, sizeId, templateId);
        int totalRecordsExpected = jdbcTemplate.queryForInt(
                "select count(distinct ct.template_id) from template ct " +
                        "inner Join templatefile ctf on ct.template_id = ctf.template_id " +
                        "Inner Join ACCOUNTTYPECREATIVETEMPLATE act on act.template_id = ct.template_id " +
                        "Inner Join appformat af on af.app_format_id = ctf.app_format_id " +
                        "Inner Join Account a on a.account_type_id = act.account_type_id " +
                        "where a.account_id = " + accountId + " and ct.name <> 'Text' and " +
                        " af.name in ('js', 'html') and ctf.size_id = " + sizeId +
                        " and ct.template_id = " + templateId);
        assertEquals(linkedRecords, totalRecordsExpected > 0);
    }

    @Test
    public void testFindTextTemplate() {
        Template creativeTemplate = templateService.findTextTemplate();
        assertEquals("JDBC query must show the same CreativeTemplate",
                1,
                jdbcTemplate.queryForInt("SELECT COUNT(0) FROM TEMPLATE WHERE NAME = ?", creativeTemplate.getName().getDefaultName()));
    }

    @Test
    public void testCreateTemplateFile() {
        CreativeTemplate ct = creativeTemplateTF.createPersistent();
        TemplateFile tf = creativeTemplateTF.createPersistentTemplateFile(ct, TemplateFileType.TEXT, FILE_NAME);
        getEntityManager().flush();
        assertNotNull(tf.getId());

        Template found = templateService.findById(ct.getId());
        List<TemplateFile> templateFiles = new ArrayList<TemplateFile>(found.getTemplateFiles());
        assertTrue("Template file hasn't been added to creative template", templateFiles.contains(tf));
    }

    @Test
    public void testDeleteTemplateFile() {
        CreativeTemplate ct = creativeTemplateTF.createPersistent();
        TemplateFile tf = creativeTemplateTF.createPersistentTemplateFile(ct, TemplateFileType.TEXT, FILE_NAME);
        resetEm();

        templateService.deleteTemplateFile(tf.getId());

        Template found = templateService.findById(ct.getId());
        assertFalse(found.getTemplateFiles().contains(tf));
    }

    @Test
    public void testFindAvailableCreativeTemplates() throws Exception {
        Collection<TemplateTO> result = templateService.findAvailableCreativeTemplates(null);
        TemplateTO textTemplate = new TemplateTO(creativeTemplateTF.findText());
        assertFalse(result.contains(textTemplate));

        CreativeTemplate deletedAttached = creativeTemplateTF.createPersistent();
        CreativeTemplate deletedStandalone = creativeTemplateTF.createPersistent();
        CreativeTemplate activeAttached = creativeTemplateTF.createPersistent();

        AccountType at = accountTypeTF.create();
        at.getTemplates().add(activeAttached);
        at.getTemplates().add(deletedAttached);
        accountTypeTF.persist(at);

        deletedAttached.setStatus(Status.DELETED);
        creativeTemplateTF.update(deletedAttached);
        deletedStandalone.setStatus(Status.DELETED);
        creativeTemplateTF.update(deletedStandalone);

        resetEm();

        deletedAttached = (CreativeTemplate) templateService.view(deletedAttached.getId());
        assertEquals(Status.DELETED,deletedAttached.getStatus());
        deletedStandalone = (CreativeTemplate) templateService.view(deletedStandalone.getId());
        assertEquals(Status.DELETED,deletedStandalone.getStatus());

        result = templateService.findAvailableCreativeTemplates(at.getId());

        assertFalse(result.contains(textTemplate));
        assertFalse(result.contains(new TemplateTO(deletedStandalone)));
        assertTrue(result.contains(new TemplateTO(activeAttached)));
        assertTrue(result.contains(new TemplateTO(deletedAttached)));

    }

    private void resetEm() {
        getEntityManager().flush();
        getEntityManager().clear();
    }

    @Test
    public void testFileWithoutTemplate() {
        try {
            creativeTemplateTF.createPersistentTemplateFile(null, TemplateFileType.TEXT, FILE_NAME);
            getEntityManager().flush();
            fail("File couldn't be created without a template reference");
        } catch (BusinessException ex) {
            assertTrue(ex.getEntityErrors().size() == 1);
            assertEquals(ex.getEntityErrors().get(0), "Creative template is not set for template file");
        }
    }

    @Test
    public void testFileWithNotExistentTemplate() {
        TemplateFile tf = creativeTemplateTF.createTemplateFile(null, TemplateFileType.TEXT, FILE_NAME);
        CreativeTemplate ct = creativeTemplateTF.create();
        Long randomTemplageId = Long.MAX_VALUE;

        tf.setTemplate(ct);
        ct.setId(randomTemplageId);

        try {
            templateService.createTemplateFile(tf);
            fail("File couldn't be created with a not existent template");
        } catch (BusinessException ex) {
            assertTrue(ex.getEntityErrors().size() == 1);
            assertEquals(ex.getEntityErrors().get(0), "Template[id=" + randomTemplageId + "] not found");
        }
    }

    @Test
    public void testUpdateNotExistentFile() {
        CreativeTemplate ct = creativeTemplateTF.createPersistent();
        Long ctId = ct.getId();
        TemplateFile tf = creativeTemplateTF.createTemplateFile(null, TemplateFileType.TEXT, FILE_NAME);

        tf.setId(Long.MAX_VALUE);
        ct.setTemplateFiles(new LinkedHashSet<TemplateFile>());
        ct.getTemplateFiles().add(new TemplateFile(4L));
        tf.setTemplate(ct);

        try {
            templateService.updateTemplateFile(tf);
            fail("Update of non-existent file should not be successful");
        } catch (BusinessException ex) {
            assertTrue(ex.getEntityErrors().size() == 1);
        }
    }

    @Test
    public void testUpdateFile() {
        //create creative template
        CreativeTemplate ct = creativeTemplateTF.createPersistent();
        //create creative template file
        TemplateFile tf = creativeTemplateTF.createPersistentTemplateFile(ct, TemplateFileType.TEXT, FILE_NAME);
        getEntityManager().flush();
        //make sure template file was created
        assertNotNull(tf.getId());

        //modify template file
        tf.setTemplateFile(FILE_NAME2);
        //update creative template file
        templateService.updateTemplateFile(tf);

        Template found = templateService.findById(ct.getId());
        List<TemplateFile> templateFiles = new ArrayList<TemplateFile>(found.getTemplateFiles());

        assertTrue("Failed to update template file.", templateFiles.contains(tf));
    }

    @Test
    public void testDeleteNotExistentFile() {
        Long id = Long.MAX_VALUE;

        try {
            templateService.deleteTemplateFile(id);
            fail("Delete of non-existent file should not be successful");
        } catch (EntityNotFoundException ex) {
            assertEquals(ex.getMessage(), "Template file [id=" + id + "] not found");
        }
    }
}
