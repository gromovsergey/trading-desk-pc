package com.foros.session.creative;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeOptGroupStatePK;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeOptionValuePK;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.RTBCategory;
import com.foros.model.creative.YandexCreativeTO;
import com.foros.model.security.AccountType;
import com.foros.model.template.ApplicationFormat;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionType;
import com.foros.model.template.TemplateFile;
import com.foros.session.EntityTO;
import com.foros.session.StatusAction;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.CreativeSelector;
import com.foros.session.campaign.bulk.YandexCreativeSelector;
import com.foros.session.status.ApprovalAction;
import com.foros.session.template.HtmlOptionHelper;
import com.foros.test.factory.CreativeCategoryTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.OptionTestFactory;
import com.foros.test.factory.TextCreativeTestFactory;

import group.Db;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class CreativeServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    private static final long TEST_TEMPLATE_ID = 6L;
    private static final long TEST_SIZE_ID = 4L;

    @Autowired
    private DisplayCreativeService displayCreativeService;

    @Autowired
    private CreativeService creativeService;

    @Autowired
    private DisplayCreativeTestFactory displayCreativeTF;

    @Autowired
    private TextCreativeTestFactory textCreativeTF;

    @Autowired
    private OptionGroupTestFactory optionGroupTF;

    @Autowired
    private OptionTestFactory optionTF;

    @Autowired
    private CreativeCategoryTestFactory creativeCategoryTF;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Test
    public void testCreate() throws Exception {
        Creative creative = displayCreativeTF.createPersistent();

        assertNotNull("ID wasn't set", creative.getId());
        assertEquals("Status wasn't set", Status.ACTIVE, creative.getStatus());
        assertEquals(ApproveStatus.APPROVED, creative.getQaStatus());
    }

    @Test
    public void testUpdate() throws Exception {
        Creative creative = displayCreativeTF.createPersistent();
        optionGroupTF.createPersistent(creative.getTemplate());
        appendStates(creative, creative.getTemplate());
        commitChanges();

        String newCreativeName = displayCreativeTF.getTestEntityRandomName();
        creative.setName(newCreativeName);
        CreativeTemplate template = creativeTemplateTF.createPersistent();
        optionGroupTF.createPersistent(template);
        commitChanges();

        clearContext();
        creative.setTemplate(template);
        appendStates(creative, template);
        displayCreativeTF.update(creative);
        assertEquals(1, creative.getGroupStates().size());

        assertEquals("Creative wasn't found in the DB", 1,
            (int) jdbcTemplate.queryForObject("select count(*) from CREATIVE where name = ?", Integer.class,
                newCreativeName));
        assertEquals("Creative template doesn't assigned", creative.getTemplate().getId().longValue(),
            (long) jdbcTemplate.queryForObject("select TEMPLATE_ID from CREATIVE c where c.name = ? ", Long.class,
                newCreativeName));

        clearContext();
        Creative persisted = displayCreativeService.find(creative.getId());
        assertEquals(creative.getGroupStates().size(), persisted.getGroupStates().size());
    }

    @Test
    public void testDelete() throws Exception {
        Creative creative = displayCreativeTF.createPersistent();

        displayCreativeService.delete(creative.getId());
        commitChanges();

        clearContext();
        creative = displayCreativeService.find(creative.getId());
        assertEquals("Creative status is incorect", Status.DELETED, creative.getStatus());
        assertEquals("Creative status is incorect", "D",
            jdbcTemplate.queryForObject("select c.status from CREATIVE c where name = ?", String.class,
                creative.getName()));
    }

    @Test
    public void testUndelete() throws Exception {
        Creative creative = displayCreativeTF.createPersistent();

        displayCreativeService.delete(creative.getId());
        commitChanges();

        displayCreativeService.undelete(creative.getId());
        commitChanges();

        clearContext();
        creative = displayCreativeService.find(creative.getId());
        assertEquals("Creative status is incorect", Status.INACTIVE, creative.getStatus());
        assertEquals("Creative status is incorect", "I",
            jdbcTemplate.queryForObject("select c.status from CREATIVE c where name = ?", String.class,
                creative.getName()));
    }

    @Test
    public void testBulkApprovalActions() throws Exception {
        Creative creative1 = displayCreativeTF.createPersistent();
        Creative creative2 = displayCreativeTF.createPersistent();
        Creative textCreative = textCreativeTF.createPersistent();

        creativeService.bulkUpdateStatus(Arrays.asList(creative1.getId(), creative2.getId(), textCreative.getId()), StatusAction.ACTIVATE.name(), "");
        creative1 = displayCreativeTF.find(creative1.getId());
        creative2 = displayCreativeTF.find(creative2.getId());
        textCreative = textCreativeTF.find(textCreative.getId());
        assertEquals("Wrong status", Status.ACTIVE, creative1.getStatus());
        assertEquals("Wrong status", Status.ACTIVE, creative2.getStatus());
        assertEquals("Wrong status", Status.ACTIVE, textCreative.getStatus());

        creativeService.bulkUpdateStatus(Arrays.asList(creative1.getId(), textCreative.getId()), ApprovalAction.APPROVE.name(), "");
        creativeService.bulkUpdateStatus(Arrays.asList(creative2.getId()), ApprovalAction.DECLINE.name(), "test");
        creative1 = displayCreativeTF.find(creative1.getId());
        creative2 = displayCreativeTF.find(creative2.getId());
        textCreative = textCreativeTF.find(textCreative.getId());
        assertEquals("Wrong status", ApproveStatus.APPROVED, creative1.getQaStatus());
        assertEquals("Wrong status", ApproveStatus.APPROVED, textCreative.getQaStatus());
        assertEquals("Wrong status", ApproveStatus.DECLINED, creative2.getQaStatus());

        creativeService.bulkUpdateStatus(Arrays.asList(creative1.getId(), creative2.getId(), textCreative.getId()), StatusAction.DELETE.name(), "");
        commitChanges();
        clearContext();
        creative1 = displayCreativeService.find(creative1.getId());
        creative2 = displayCreativeService.find(creative2.getId());
        textCreative = textCreativeTF.find(textCreative.getId());
        assertEquals("Creative status is incorect", Status.DELETED, creative1.getStatus());
        assertEquals("Creative status is incorect", Status.DELETED, creative2.getStatus());
        assertEquals("Creative status is incorect", Status.DELETED, textCreative.getStatus());

        creativeService.bulkUpdateStatus(Arrays.asList(creative1.getId()), ApprovalAction.DECLINE.name(), "test");
        assertEquals("Wrong status", ApproveStatus.APPROVED, creative1.getQaStatus());
        creativeService.bulkUpdateStatus(Arrays.asList(creative2.getId()), ApprovalAction.APPROVE.name(), "");
        assertEquals("Wrong status", ApproveStatus.DECLINED, creative2.getQaStatus());
    }

    @Test
    public void testBulkInternal() throws Exception {
        Creative creative1 = displayCreativeTF.createPersistent();
        creative1.setStatus(Status.INACTIVE);
        displayCreativeTF.update(creative1);
        Creative creative2 = displayCreativeTF.createPersistent();
        creative2.setStatus(Status.INACTIVE);
        displayCreativeTF.update(creative2);
        Creative textCreative = textCreativeTF.createPersistent();
        textCreative.setStatus(Status.INACTIVE);

        creativeService.bulkUpdateStatus(Arrays.asList(creative1.getId(), creative2.getId(), textCreative.getId()), StatusAction.ACTIVATE.name(), "");
        creative1 = displayCreativeTF.find(creative1.getId());
        creative2 = displayCreativeTF.find(creative2.getId());
        textCreative = textCreativeTF.find(textCreative.getId());
        // Commented for now. See OUI-25720
        //        assertEquals("Wrong status", Status.PENDING, creative1.getStatus());
        //        assertEquals("Wrong status", Status.PENDING, creative2.getStatus());
        //        assertEquals("Wrong status", Status.PENDING, textCreative1.getStatus());
        assertEquals("Wrong status", Status.ACTIVE, creative1.getStatus());
        assertEquals("Wrong status", Status.ACTIVE, creative2.getStatus());
        assertEquals("Wrong status", Status.ACTIVE, textCreative.getStatus());

        creativeService.bulkUpdateStatus(Arrays.asList(creative1.getId(), creative2.getId(), textCreative.getId()), ApprovalAction.APPROVE.name(), "");
        creative1 = displayCreativeTF.find(creative1.getId());
        creative2 = displayCreativeTF.find(creative2.getId());
        textCreative = textCreativeTF.find(textCreative.getId());
        assertEquals("Wrong status", ApproveStatus.APPROVED, creative1.getQaStatus());
        assertEquals("Wrong status", ApproveStatus.APPROVED, creative2.getQaStatus());
        assertEquals("Wrong status", ApproveStatus.APPROVED, textCreative.getQaStatus());

        creativeService.bulkUpdateStatus(Arrays.asList(creative1.getId(), creative2.getId(), textCreative.getId()), StatusAction.DELETE.name(), "");
        commitChanges();
        clearContext();
        creative1 = displayCreativeService.find(creative1.getId());
        creative2 = displayCreativeService.find(creative2.getId());
        textCreative = textCreativeTF.find(textCreative.getId());
        assertEquals("Creative status is incorect", Status.DELETED, creative1.getStatus());
        assertEquals("Creative status is incorect", Status.DELETED, creative2.getStatus());
        assertEquals("Creative status is incorect", Status.DELETED, textCreative.getStatus());
    }

    @Test
    public void testActivateInactiveInternal() throws Exception {
        Creative creative = displayCreativeTF.createPersistent();
        creative.setStatus(Status.INACTIVE);
        displayCreativeTF.update(creative);

        displayCreativeService.activate(creative.getId());

        creative = displayCreativeTF.find(creative.getId());
        // Commented for now. See OUI-25720
        //        assertEquals("Wrong status", Status.PENDING, creative.getStatus());
        assertEquals("Wrong status", Status.ACTIVE, creative.getStatus());
    }

    @Test
    public void testFindCreativeSizeOrApplicationFormatLinkedCreatives() {
        TemplateFile templateFile = new TemplateFile(1035L);
        templateFile.setCreativeSize(new CreativeSize(12L));
        templateFile.setTemplate(new CreativeTemplate(525L));
        templateFile.setApplicationFormat(new ApplicationFormat(1L));
        List<EntityTO> cts = displayCreativeService.getCreativeSizeOrApplicationFormatLinkedCreatives(templateFile);

        int totalRecordsExpected = jdbcTemplate.queryForObject(
            "select distinct count(distinct c.creative_id) from creative c " +
                    "Inner Join template ct on c.template_id = ct.template_id " +
                    "inner Join templatefile ctf on ct.template_id = ctf.template_id " +
                    "Inner Join ACCOUNTTYPECREATIVETEMPLATE act on act.template_id = ct.template_id " +
                    "Inner Join appformat af on af.app_format_id = ctf.app_format_id " +
                    "where c.status <> 'D' and c.template_id = " + templateFile.getTemplate().getId() +
                    " and af.name in ('js', 'html') and c.size_id = " + templateFile.getCreativeSize().getId() +
                    " and af.app_format_id = " + templateFile.getApplicationFormat().getId(), Integer.class);
        assertEquals("JDBC query must show the same number of CreativeTemplate", totalRecordsExpected, cts.size());
    }

    @Test
    public void testUpdateCreativeCategoriesAsInternal() throws Exception {
        Creative creative = displayCreativeTF.createPersistent();
        clearContext();

        creative.setQaStatus(ApproveStatus.APPROVED);
        Set<CreativeCategory> categories = creative.getCategories();
        CreativeCategory approvedTag = new CreativeCategory(1L, "A Tag");
        approvedTag.setQaStatus('A');
        approvedTag.setType(CreativeCategoryType.TAG);
        CreativeCategory pendingTag = new CreativeCategory(2L, "H Tag");
        pendingTag.setType(CreativeCategoryType.TAG);
        pendingTag.setQaStatus('H');
        getEntityManager().persist(approvedTag);
        getEntityManager().persist(pendingTag);
        categories.add(approvedTag);
        categories.add(pendingTag);

        displayCreativeService.update(creative);

        assertEquals(creative.getCategories(), categories);
        assertEquals(approvedTag.getQaStatus(), 'A');
        assertEquals(pendingTag.getQaStatus(), 'A');
    }

    @Test
    public void testSearchCategory() throws Exception {
        CreativeCategory tagA = creativeCategoryTF.createPersistent(CreativeCategoryType.TAG, ApproveStatus.APPROVED);
        tagA.setDefaultName("CamelcAsE" + tagA.getDefaultName());
        entityManager.merge(tagA);

        CreativeCategory tagH = creativeCategoryTF.createPersistent(CreativeCategoryType.TAG, ApproveStatus.HOLD);
        tagH.setDefaultName("CamelcAsE" + tagH.getDefaultName());

        entityManager.merge(tagH);
        commitChanges();

        assertEquals(2, displayCreativeService.searchCategory(CreativeCategoryType.TAG, "CAMElcase", true, 20).size());
        assertEquals(1, displayCreativeService.searchCategory(CreativeCategoryType.TAG, "camelcase", false, 20).size());

        assertEquals(displayCreativeService.searchCategory(CreativeCategoryType.TAG, "", false, 20).size(),
            displayCreativeService.searchCategory(CreativeCategoryType.TAG, null, false, 20).size());
    }

    private void appendStates(Creative creative, CreativeTemplate template) {
        creative.getGroupStates().clear();
        for (OptionGroup group : template.getOptionGroups()) {
            CreativeOptGroupState state = new CreativeOptGroupState();
            state.setId(new CreativeOptGroupStatePK(group.getId(), creative.getId()));
            state.setEnabled(true);
            state.setCollapsed(false);
            entityManager.persist(state);
            creative.getGroupStates().add(state);
        }
    }

    @Test
    public void testGet() throws Exception {
        Creative displayCreative = setNotDefaultOptions(displayCreativeTF.create());
        displayCreativeTF.persist(displayCreative);
        AdvertiserAccount account = displayCreative.getAccount();
        Creative textCreative = setNotDefaultOptions(textCreativeTF.create(account));
        textCreativeTF.persist(textCreative);

        commitChanges();
        clearContext();

        CreativeSelector creativeSelector = new CreativeSelector();
        creativeSelector.setAdvertiserIds(Collections.singletonList(account.getId()));
        Result<Creative> res = displayCreativeService.get(creativeSelector);
        assertNotNull(res);
        List<Creative> entities = res.getEntities();
        assertNotNull(entities);
        assertEquals(2, entities.size());
        Creative original;
        for (Creative creative : entities) {
            original = null;
            if (creative.getId().equals(textCreative.getId())) {
                original = textCreative;
            }

            if (creative.getId().equals(displayCreative.getId())) {
                original = displayCreative;
            }

            assertNotNull(original);

            assertEquals(original.getTemplate().getId(), creative.getTemplate().getId());
            assertEquals(original.getSize().getId(), creative.getSize().getId());
            assertEquals(original.getName(), creative.getName());
            assertAdvertiserOptionsEqual(original, creative);
        }
    }

    @Test
    public void testFindCreatives() {
        Creative creative = displayCreativeTF.createPersistent();
        commitChanges();
        clearContext();

        List<CreativeTO> creatives = creativeService.findCreatives(
            creative.getAccount().getId(),
            null,
            null,
            creative.getSize().getId(),
            false,
            false,
            0,
            99,
            CreativeSortType.ATOZ
            );
        assertNotNull(creatives);
        assertEquals(1, creatives.size());

        int count = creativeService.findCreativesCount(
            creative.getAccount().getId(),
            null,
            null,
            creative.getSize().getId(),
            false,
            false
            );
        assertEquals(creatives.size(), count);
    }

    @Test
    public void testPerformByToken() {
        Creative creative = displayCreativeTF.create();
        creative.setTemplate(entityManager.find(CreativeTemplate.class, TEST_TEMPLATE_ID));
        creative.setSize(entityManager.find(CreativeSize.class, TEST_SIZE_ID));
        AccountType accountType = creative.getAccount().getAccountType();
        accountType.getTemplates().add(creative.getTemplate());
        accountType.getCreativeSizes().add(creative.getSize());
        commitChanges();
        clearContext();

        Collection<Option> options = new ArrayList<>();
        options.addAll(creative.getTemplate().getAdvertiserOptions());
        options.addAll(creative.getSize().getAdvertiserOptions());
        Set<CreativeOptionValue> optionValues = creative.getOptions();
        for (Option option : options) {
            CreativeOptionValue cov = new CreativeOptionValue();
            cov.setOption(new Option(option.getToken()));
            cov.setValue(optionTF.newOptionValue(option));
            optionValues.add(cov);
        }

        creative.getCategories().add(creativeCategoryTF.createPersistent(CreativeCategoryType.VISUAL, ApproveStatus.APPROVED));
        creative.getCategories().add(creativeCategoryTF.createPersistent(CreativeCategoryType.CONTENT, ApproveStatus.APPROVED));

        Operation<Creative> operation = new Operation<>();
        operation.setEntity(creative);
        operation.setOperationType(OperationType.CREATE);
        Operations<Creative> operations = new Operations<>();
        operations.setOperations(Collections.singletonList(operation));

        OperationsResult res = displayCreativeService.perform(operations);
        assertNotNull(res);
        assertNotNull(res.getIds());
        assertEquals(1, res.getIds().size());
        Long id = res.getIds().get(0);

        clearContext();

        Creative persisted = displayCreativeService.find(id);
        Set<CreativeOptionValue> persistedOptionValues = persisted.getOptions();
        assertEquals(optionValues.size(), persistedOptionValues.size());
    }

    private Creative setNotDefaultOptions(Creative creative) {
        for (CreativeOptionValue optionValue : creative.getOptions()) {
            switch (optionValue.getOption().getType()) {
            case INTEGER:
                optionValue.setValue(String.valueOf(Integer.valueOf(optionValue.getValue()) + 1));
                break;
            case STRING:
                optionValue.setValue(optionValue.getValue() + "-Test");
                break;
            default:
                break;
            }
        }

        return creative;
    }

    private void assertAdvertiserOptionsEqual(Creative expected, Creative actual) {
        Map<Long, CreativeOptionValue> expectedValues = new HashMap<>();
        for (CreativeOptionValue optionValue : expected.getOptions()) {
            Option option = optionValue.getOption();
            if (OptionGroupType.Advertiser == option.getOptionGroup().getType()) {
                expectedValues.put(option.getId(), optionValue);
            }
        }
        int optionsCount = 0;
        for (CreativeOptionValue optionValue : actual.getOptions()) {
            Option option = optionValue.getOption();
            if (OptionGroupType.Advertiser == option.getOptionGroup().getType()) {
                optionsCount++;
                CreativeOptionValue expectedValue = expectedValues.get(option.getId());
                assertNotNull("Saved creative option value not found", expectedValue);
                assertEquals(expectedValue.getValue(), optionValue.getValue());
            }
        }
        assertEquals(expectedValues.size(), optionsCount);
    }

    @Test
    public void testVersionIncrement() {
        Creative creative = displayCreativeTF.create();

        CreativeTemplate template = creative.getTemplate();
        OptionGroup og = new OptionGroup();
        og.setDefaultName("test");
        og.setTemplate(template);
        og.setType(OptionGroupType.Advertiser);
        entityManager.persist(og);
        template.getOptionGroups().add(og);

        Option option = new Option();
        option.setDefaultName("test");
        option.setType(OptionType.STRING);
        option.setToken("TEST");
        option.setOptionGroup(og);
        entityManager.persist(option);

        CreativeOptionValue cov = new CreativeOptionValue();
        cov.setValue("original value");
        cov.setId(new CreativeOptionValuePK(0, option.getId()));
        creative.getOptions().add(cov);

        displayCreativeService.create(creative);
        commitChanges();
        clearContext();

        Timestamp version = creative.getVersion();
        cov.setValue("updated value");
        displayCreativeService.update(creative);
        commitChanges();
        clearContext();

        creative = displayCreativeTF.refresh(creative);

        assertEquals("Version should be increased", -1, version.compareTo(creative.getVersion()));
    }

    @Test
    public void testGetYandexCreativeTO() {
        CreativeCategory contentCategory = creativeCategoryTF.create(CreativeCategoryType.CONTENT);
        creativeCategoryTF.persist(contentCategory);
        commitChanges();

        RTBCategory rtbCategory = new RTBCategory();
        rtbCategory.setRtbConnector(creativeCategoryTF.findConnector("YANDEX"));
        rtbCategory.setName("7");
        rtbCategory.setCreativeCategory(contentCategory);
        contentCategory.getRtbCategories().add(rtbCategory);
        creativeCategoryTF.update(contentCategory);
        commitChanges();

        Creative creative = displayCreativeTF.create();
        creative.getCategories().add(contentCategory);
        displayCreativeTF.persist(creative);
        commitChanges();

        YandexCreativeSelector selector = new YandexCreativeSelector();
        selector.setCreatives(Arrays.asList(creative.getId()));

        Result<YandexCreativeTO> res = displayCreativeService.getYandexCreativeTO(selector);
        List<YandexCreativeTO> entities = res.getEntities();
        assertEquals(1, entities.size());
        YandexCreativeTO yandexCreativeTO = entities.get(0);
        assertEquals(creative.getId(), yandexCreativeTO.getCreativeId());
        assertEquals(1, yandexCreativeTO.getTnsArticles().size());
        assertEquals(rtbCategory.getName(), yandexCreativeTO.getTnsArticles().iterator().next());
    }

    @Test
    public void testHttpSafeOption() {
        CreativeTemplate template = creativeTemplateTF.createPersistent();
        OptionGroup group = optionGroupTF.createPersistent(template, OptionGroupType.Hidden);

        Option httpSafeOption = optionTF.createPersistent(group, OptionType.STRING);
        httpSafeOption.setToken(HtmlOptionHelper.HTTPS_SAFE_TOKEN);
        httpSafeOption.setDefaultValue(HtmlOptionHelper.HTTP_ONLY);
        optionTF.update(httpSafeOption);

        OptionGroup group1 = optionGroupTF.createPersistent(template, OptionGroupType.Advertiser);
        Option urlOption = optionTF.createPersistent(group1, OptionType.URL);
        String urlToken = "HTTPS_SAFE_URL";
        urlOption.setToken(urlToken);
        urlOption.setDefaultValue("http://ya.ru");
        optionTF.update(urlOption);

        Creative persistentCreative = displayCreativeTF.createPersistent(template, creativeSizeTF.createPersistent());

        CreativeOptionValue urlOptionValue = new CreativeOptionValue();
        urlOptionValue.setId(new CreativeOptionValuePK(persistentCreative.getId(), urlOption.getId()));
        urlOptionValue.setCreative(persistentCreative);
        urlOptionValue.setOption(urlOption);
        urlOptionValue.setValue("https://ya.ru");
        persistentCreative.getOptions().add(urlOptionValue);
        displayCreativeService.update(persistentCreative);
        commitChanges();

        Number count = jdbcTemplate.queryForObject("select count(*) from CreativeOptionValue where option_id = " + httpSafeOption.getId(), Number.class);
        assertEquals(1, count.intValue());

        String value = jdbcTemplate.queryForObject("select value from CreativeOptionValue where option_id = " + httpSafeOption.getId(), String.class);
        assertEquals(HtmlOptionHelper.HTTPS_SAFE, value);

        persistentCreative = displayCreativeService.find(persistentCreative.getId());
        for (CreativeOptionValue creativeOptionValue : persistentCreative.getOptions()) {
            if (creativeOptionValue.getOption().getToken().equals(urlToken)) {
                creativeOptionValue.setValue(urlOption.getDefaultValue());
                break;
            }
        }

        displayCreativeService.update(persistentCreative);
        commitChanges();
        count = jdbcTemplate.queryForObject("select count(*) from CreativeOptionValue where option_id = " + httpSafeOption.getId(), Number.class);
        assertEquals(0, count.intValue());

    }
}
