package com.foros.session.channel;

import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.channel.DiscoverChannelsAlreadyExistException;
import com.foros.service.mock.MockValidationServiceBean;
import com.foros.session.admin.categoryChannel.CategoryChannelService;
import com.foros.session.channel.service.DiscoverChannelListService;
import com.foros.session.channel.service.DiscoverChannelService;
import com.foros.test.factory.BehavioralParamsTestFactory;
import com.foros.test.factory.CategoryChannelTestFactory;
import com.foros.test.factory.CountryTestFactory;
import com.foros.test.factory.DiscoverChannelListTestFactory;
import com.foros.test.factory.DiscoverChannelTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationService;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import group.Db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class DiscoverChannelListServiceBeanIntegrationTest extends AbstractChannelServiceBeanIntegrationTest<DiscoverChannel> {
    @Autowired
    private DiscoverChannelListService dcListService;

    @Autowired
    private DiscoverChannelService dcService;

    @Autowired
    private DiscoverChannelListTestFactory dcListTF;

    @Autowired
    private DiscoverChannelTestFactory dcTF;

    @Autowired
    private BehavioralParamsTestFactory bparamsTF;

    @Autowired
    private CountryTestFactory countryTF;

    @Autowired
    private CategoryChannelTestFactory categoryChannelTF;

    @Autowired
    private CategoryChannelService categoryChannelSvc;

    @Autowired
    private InternalAccountTestFactory accountTF;

    @Autowired
    private ValidationService validationService;

    @EJB
    private InternalAccountTestFactory internalAccountTF;

    @Autowired
    private MockValidationServiceBean validationServiceBean;

    @Test
    public void testCreate() throws Exception {
        DiscoverChannelList dcList = dcListTF.create();
        dcList.setName(dcListTF.getTestEntityRandomName());
        dcList.setKeywordList("First word\nSecond word");
        dcListService.create(dcList);
        commitChanges();

        DiscoverChannelList persisted = dcListService.view(dcList.getId());
        getEntityManager().refresh(persisted);
        assertEquals(2, persisted.getChildChannels().size());
        DiscoverChannel chs[] = new DiscoverChannel[2];
        persisted.getChildChannels().toArray(chs);

        DiscoverChannel ch1, ch2;
        if (chs[0].getName().equals("child_First word")) {
            ch1 = chs[0];
            ch2 = chs[1];
        } else {
            ch1 = chs[1];
            ch2 = chs[0];
        }
        getEntityManager().refresh(persisted);
        assertEquals(ApproveStatus.APPROVED, persisted.getQaStatus());
        assertEquals(Status.ACTIVE, persisted.getStatus());
        assertEquals(DisplayStatus.Major.LIVE, persisted.getDisplayStatus().getMajor());

        assertPropertiesEquals(persisted, ch1);
        assertPropertiesEquals(persisted, ch2);
        assertNotNull(ch1.getId());
        assertNotNull(ch2.getId());
    }

    @Test
    public void testCreateWithEmptyKeywords() throws Exception {
        DiscoverChannelList dcList = dcListTF.create();
        dcList.setName(dcListTF.getTestEntityRandomName());
        dcListService.create(dcList, Collections.<DiscoverChannel>emptyList());
    }

    @Test
    public void testView() {
        DiscoverChannelList dcList = dcListTF.create();
        dcList.setChannelNameMacro(DiscoverChannelList.KEYWORD_TOKEN);
        dcList.setName(dcListTF.getTestEntityRandomName());
        dcList.setKeywordList("First111,dd,,,,blablabla");
        dcListService.create(dcList);
        commitChanges();

        clearContext();
        DiscoverChannelList view = dcListService.view(dcList.getId());
        Set<DiscoverChannel> children = view.getChildChannels();
        assertFalse(children.isEmpty());
    }

    @Test
    public void testUpdate() throws Exception {
        DiscoverChannelList dcList = dcListTF.create();
        dcList.setName(dcListTF.getTestEntityRandomName());
        dcList.setKeywordList("First\r\nSecond\nThird");
        dcListService.create(dcList);
        commitChanges();

        clearContext();
        DiscoverChannel ch3 = findChannelByKeyword("Third", dcList);

        DiscoverChannelList persisted = dcListService.find(dcList.getId());
        persisted.setKeywordList("Third\nFourth");
        persisted.setCountry(countryTF.findOrCreatePersistent("RU"));
        commitChanges();

        clearContext();
        dcListService.update(persisted);
        persisted = dcListTF.refresh(persisted);

        checkTriggerLists("Third\nFourth", persisted.getKeywordList());
        assertEquals("RU", persisted.getCountry().getCountryCode());

        assertEquals(2, persisted.getChildChannels().size());
        DiscoverChannel ch3New = findChannelByKeyword("Third", persisted);
        DiscoverChannel ch4 = findChannelByKeyword("Fourth", persisted);

        assertNotNull(ch3New.getId());
        assertNotNull(ch4.getId());
        assertEquals(ch3.getId(), ch3New.getId());

        assertEquals("Fourth", ch4.getBaseKeyword());
        assertPropertiesEquals(persisted, ch3New);
        assertPropertiesEquals(persisted, ch4);
    }

    @Test
    public void testUpdateMacro() throws Exception {
        DiscoverChannelList dcList = dcListTF.create();
        dcList.setName(dcListTF.getTestEntityRandomName());
        dcList.setKeywordList("First\r\nSecond");
        dcListService.create(dcList);
        commitChanges();

        clearContext();
        dcList = dcListService.findById(dcList.getId());
        dcList.setKeywordTriggerMacro("new_kwtrigger_" + DiscoverChannelList.KEYWORD_TOKEN);
        dcList.setChannelNameMacro("new_child_" + DiscoverChannelList.KEYWORD_TOKEN);
        dcList.setDiscoverQuery("new_query_" + DiscoverChannelList.KEYWORD_TOKEN);
        dcList.setDiscoverAnnotation("new_annotation_" + DiscoverChannelList.KEYWORD_TOKEN);
        dcList.setDescription("Ccc " + DiscoverChannelList.KEYWORD_TOKEN + " Ddd");
        dcListService.update(dcList);
        commitChanges();

        clearContext();
        DiscoverChannelList persisted = dcListService.findById(dcList.getId());
        checkTriggerLists("First\r\nSecond", persisted.getKeywordList());
        assertEquals("new_kwtrigger_" + DiscoverChannelList.KEYWORD_TOKEN, persisted.getKeywordTriggerMacro());
        assertEquals("new_child_" + DiscoverChannelList.KEYWORD_TOKEN, persisted.getChannelNameMacro());
        assertEquals("new_query_" + DiscoverChannelList.KEYWORD_TOKEN, persisted.getDiscoverQuery());
        assertEquals("new_annotation_" + DiscoverChannelList.KEYWORD_TOKEN, persisted.getDiscoverAnnotation());
        assertEquals("Ccc " + DiscoverChannelList.KEYWORD_TOKEN + " Ddd", persisted.getDescription());
    }

    @Test
    public void testCreateAndLink() throws Exception {
        Account account = accountTF.createPersistent();
        Country country = countryTF.createPersistent();

        DiscoverChannel dc1 = dcTF.create(account);
        dc1.setName("First");
        dc1.setCountry(country);
        dc1.getPageKeywords().setPositive("\"f1,f2\"");
        dc1.getSearchKeywords().setPositive("\"f1,f2\"");
        dcTF.persist(dc1);

        DiscoverChannel dc2 = dcTF.create(account);
        dc2.setName("Second");
        dc2.setCountry(country);
        dc2.getPageKeywords().setPositive("\"s1,s2,s3\"");
        dc2.getSearchKeywords().setPositive("\"s1,s2,s3\"");
        dcTF.persist(dc2);
        commitChanges();

        List<DiscoverChannel> existingChannels = new ArrayList<DiscoverChannel>();
        DiscoverChannel dc1Copy = new DiscoverChannel();
        dc1Copy.setId(dc1.getId());
        dc1Copy.setVersion(dc1.getVersion());
        existingChannels.add(dc1Copy);
        DiscoverChannel dc2Copy = new DiscoverChannel();
        dc2Copy.setId(dc2.getId());
        dc2Copy.setVersion(dc2.getVersion());
        existingChannels.add(dc2Copy);

        DiscoverChannelList dcList = dcListTF.create(account);
        dcList.setCountry(country);
        dcList.setChannelNameMacro(DiscoverChannelList.KEYWORD_TOKEN);
        dcList.setKeywordTriggerMacro(DiscoverChannelList.KEYWORD_TOKEN + "_1");
        dcList.setName(dcListTF.getTestEntityRandomName());
        dcList.setKeywordList("Third\r\nForth");

        dcListService.create(dcList, existingChannels);
        commitChanges();

        clearContext();
        dcList = dcListService.find(dcList.getId());
        dc1 = dcService.view(dc1.getId());
        dc2 = dcService.view(dc2.getId());
        assertEquals(4, dcList.getChildChannels().size());
        assertTrue(dcList.getChildChannels().contains(dc1));
        assertTrue(dcList.getChildChannels().contains(dc2));
        assertFalse(dc1.getPageKeywords().getPositiveString().contains("_1"));
        assertFalse(dc2.getPageKeywords().getPositiveString().contains("_1"));
        assertFalse(dc1.getSearchKeywords().getPositiveString().contains("_1"));
        assertFalse(dc2.getSearchKeywords().getPositiveString().contains("_1"));
    }

    @Test
    public void testInactivate() throws Exception {
        DiscoverChannelList dcList = dcListTF.create();
        dcList.setName(dcListTF.getTestEntityRandomName());
        dcList.setKeywordList("First\r\nSecond\nThird");
        dcListService.create(dcList);
        commitChanges();

        dcListService.inactivate(dcList.getId());
        commitChanges();

        clearContext();
        dcList = dcListService.find(dcList.getId());
        assertEquals("Child Channels was not declined", 3,
                jdbcTemplate.queryForInt(
                        "select count(*) from channel where CHANNEL_LIST_ID = ? and status = 'I'",
                        dcList.getId()));

        DiscoverChannelList persisted = dcListService.find(dcList.getId());
        assertEquals(Status.INACTIVE, persisted.getStatus());
    }

    @Test
    public void testUpdateCategories() throws Exception {
        DiscoverChannelList dcList = dcListTF.createPersistent();
        commitChanges();

        DiscoverChannelList detached = new DiscoverChannelList();
        detached.setId(dcList.getId());
        detached.setVersion(dcList.getVersion());

        CategoryChannel cc = categoryChannelTF.createPersistent();
        detached.setCategories(Collections.singleton(cc));
        commitChanges();

        categoryChannelSvc.updateDiscoverListCategories(detached);
        commitChanges();

        clearContext();
        DiscoverChannelList dcListWithCat = dcListService.find(dcList.getId());

        Set<CategoryChannel> cats = Collections.singleton(cc);
        assertEquals(cats, dcListWithCat.getCategories());
        for (DiscoverChannel dc : dcListWithCat.getChildChannels()) {
            assertEquals(cats, dc.getCategories());
        }
    }

    @Test
    public void testUnlink() throws Exception {
        DiscoverChannelList dcList = dcListTF.createPersistent();
        commitChanges();

        clearContext();
        DiscoverChannel dcToUnlink = findChannelByKeyword("Second", dcList);
        dcListService.unlink(dcToUnlink.getId());
        commitChanges();

        clearContext();
        DiscoverChannelList persistedList = dcListTF.refresh(dcList);
        assertEquals(2, persistedList.getChildChannels().size());
        checkTriggerLists("First\nThird", persistedList.getKeywordList());
        findChannelByKeyword("First", persistedList);
        findChannelByKeyword("Third", persistedList);
        assertFalse(dcList.getVersion().equals(persistedList.getVersion()));
    }

    @Test
    public void testUnlinkAlreadyUnlinked() {
        DiscoverChannelList dcList = dcListTF.createPersistent();
        commitChanges();

        DiscoverChannel dcToUnlink = findChannelByKeyword("Second", dcList);
        dcListService.unlink(dcToUnlink.getId());
        commitChanges();

        clearContext();
        try {
            validationService.validate("DiscoverChannelList.updateLinked", entityManager.find(DiscoverChannel.class, dcToUnlink.getId()) ).throwIfHasViolations();
            dcListService.unlink(dcToUnlink.getId());
            fail("ConstraintViolationException is expected");
        } catch (ConstraintViolationException e) {
            // expected
        }
    }

    @Test
    public void testLink() throws Exception {
        DiscoverChannelList dcList = dcListTF.createPersistent();

        clearContext();
        dcList = dcListService.findById(dcList.getId());
        CategoryChannel cc = categoryChannelTF.createPersistent();
        dcList.setCountry(countryTF.find("GB"));
        dcList.setKeywordTriggerMacro(DiscoverChannelList.KEYWORD_TOKEN + "_1");
        dcList.setCategories(new HashSet<CategoryChannel>(Arrays.asList(cc)));
        dcListService.update(dcList);
        commitChanges();

        DiscoverChannel dcChannel = dcTF.createPersistent(dcList.getAccount());
        dcChannel.getPageKeywords().setPositive("Forth");
        dcChannel.getSearchKeywords().setPositive("Forth");
        dcChannel.setDiscoverQuery("Forth_Query");
        dcChannel.setCountry(countryTF.findOrCreatePersistent("US"));

        DiscoverChannel dcToLink = new DiscoverChannel();
        dcToLink.setId(dcChannel.getId());
        dcToLink.setStatus(dcList.getStatus());
        dcToLink.setVersion(dcChannel.getVersion());

        dcListService.link(dcList.getId(), Arrays.asList(dcToLink));
        commitChanges();

        clearContext();
        assertLinked(dcChannel, dcList);
        DiscoverChannel linkedChannel = dcService.view(dcChannel.getId());
        assertFalse(linkedChannel.getPageKeywords().getPositiveString().contains("_1"));
        assertFalse(linkedChannel.getSearchKeywords().getPositiveString().contains("_1"));
        assertEquals(dcList.getStatus(), linkedChannel.getStatus());
    }

    @Test(expected = DiscoverChannelsAlreadyExistException.class)
    public void testLinkAlreadyLinkedDBConstraintProcessing() {
        DiscoverChannelList firstList = dcListTF.createPersistent();
        clearContext();

        validationServiceBean.savePoint();

        DiscoverChannelList secondList = dcListTF.create(firstList.getAccount(), firstList.getCountry());
        secondList.setKeywordList(firstList.getKeywordList());
        dcListService.create(secondList);
    }

    @Test
    public void testHugeChildrenAmountProcessing() {
        DiscoverChannelList firstList = createHugeChildrenAmountPersistent();
        clearContext();
        assertTrue("Test makes sense only when number of children > 1000", firstList.getChildChannels().size() > 1000);

        validationServiceBean.savePoint();

        DiscoverChannelList secondList = dcListTF.create(firstList.getAccount(), firstList.getCountry());
        secondList.setKeywordList(firstList.getKeywordList());
        try {
            dcListService.create(secondList);
        } catch (DiscoverChannelsAlreadyExistException e) {
            assertTrue(e.getExistingChannels().size() > 1000);
            return;
        }
        assertTrue("DiscoverChannelsAlreadyExistException was expected", true);
    }

    @Test
    public void testLinkUpdateTrigger() throws Exception {
        DiscoverChannelList dcList = dcListTF.createPersistent();

        clearContext();
        dcListTF.refresh(dcList);
        CategoryChannel cc = categoryChannelTF.createPersistent();
        dcList.setCountry(countryTF.find("GB"));
        dcList.setKeywordTriggerMacro(DiscoverChannelList.KEYWORD_TOKEN + "_1");
        dcList.setCategories(new HashSet<CategoryChannel>(Arrays.asList(cc)));
        dcListService.update(dcList);
        commitChanges();

        clearContext();
        dcListTF.refresh(dcList);
        DiscoverChannel dcChannel = dcTF.createPersistent(dcList.getAccount());
        dcChannel.setBaseKeyword("Forth");
        dcChannel.getPageKeywords().setPositive("Forth");
        dcChannel.getSearchKeywords().setPositive("Forth");
        dcChannel.setDiscoverQuery("Forth_Query");
        dcChannel.setCountry(countryTF.findOrCreatePersistent("US"));
        commitChanges();

        DiscoverChannel dcToLink = new DiscoverChannel();
        dcToLink.setId(dcChannel.getId());
        dcToLink.setStatus(dcList.getStatus());
        dcToLink.setVersion(dcChannel.getVersion());
        dcListService.link(dcList.getId(), Arrays.asList(dcToLink));
        commitChanges();

        clearContext();
        assertLinked(dcChannel, dcList);
        DiscoverChannel linkedChannel = dcService.view(dcChannel.getId());
        assertTrue(linkedChannel.getPageKeywords().getPositiveString().contains("_1"));
        assertTrue(linkedChannel.getSearchKeywords().getPositiveString().contains("_1"));
        assertEquals(dcList.getStatus(), linkedChannel.getStatus());
    }

    @Test
    public void testLinkUpdateStatus() throws Exception {
        DiscoverChannelList dcList = dcListTF.createPersistent();

        clearContext();
        dcList = dcListService.findById(dcList.getId());
        dcList.setCountry(countryTF.find("GB"));
        dcListService.update(dcList);
        commitChanges();

        clearContext();
        dcList = dcListTF.refresh(dcList);
        assertEquals(Status.ACTIVE, dcList.getStatus());
        dcListService.inactivate(dcList.getId());
        commitChanges();

        clearContext();
        dcList = dcListTF.refresh(dcList);
        assertEquals(Status.INACTIVE, dcList.getStatus());

        DiscoverChannel dcChannel = dcTF.createPersistent(dcList.getAccount());
        dcChannel.getPageKeywords().setPositive("Forth");
        dcChannel.getSearchKeywords().setPositive("Forth");
        dcChannel.setDiscoverQuery("Forth_Query");
        dcChannel.setCountry(countryTF.findOrCreatePersistent("US"));
        commitChanges();

        DiscoverChannel dcToLink = new DiscoverChannel();
        dcToLink.setId(dcChannel.getId());
        dcToLink.setStatus(Status.ACTIVE);
        dcToLink.setVersion(dcChannel.getVersion());

        dcListService.link(dcList.getId(), Arrays.asList(dcToLink));
        commitChanges();

        clearContext();
        dcChannel = dcTF.refresh(dcChannel);
        dcList = dcListTF.refresh(dcList);
        assertLinked(dcChannel, dcList);
        assertEquals(Status.INACTIVE, dcList.getStatus());
        assertEquals(dcList.getStatus(), dcToLink.getStatus());
    }

    @Test
    public void testUpdateWihBehavParams() throws Exception {
        DiscoverChannelList dcList = dcListTF.createPersistent();
        commitChanges();

        clearContext();
        dcList = dcListTF.refresh(dcList);
        BehavioralParametersList bparamsList = bparamsTF.createPersistent();
        dcList.setBehavParamsList(bparamsList);
        dcListService.update(dcList);
        commitChanges();

        clearContext();
        dcList = dcListService.find(dcList.getId());
        assertEquals(bparamsList, dcList.getBehavParamsList());
    }

    @Test
    public void testUpdateLinked() throws Exception {
        DiscoverChannelList dcList = dcListTF.createPersistent();
        DiscoverChannel dc2 = findChannelByKeyword("Second", dcList);
        dc2.setBaseKeyword("Second_new");
        dcListService.updateLinkedChannel(dc2);
        commitChanges();

        clearContext();
        DiscoverChannelList persisted = dcListService.view(dcList.getId());

        DiscoverChannel dc2Persisted = findChannelByKeyword("Second_new", persisted);

        checkTriggerLists("First\nSecond_new\nThird", persisted.getKeywordList());
        assertEquals(3, persisted.getChildChannels().size());
        assertPropertiesEquals(persisted, dc2Persisted);
    }

    @Test
    public void testKeywordTrimming() throws Exception {
        DiscoverChannelList dcList = dcListTF.create();
        dcList.setChannelNameMacro("a" + DiscoverChannelList.KEYWORD_TOKEN);
        dcList.setName(dcListTF.getTestEntityRandomName());
        dcList.setKeywordList("\" test\"");
        dcListService.create(dcList);
        commitChanges();

        clearContext();
        DiscoverChannelList persisted = dcListService.find(dcList.getId());
        getEntityManager().refresh(persisted);

        List<Channel> childChannels = new ArrayList<Channel>(persisted.getChildChannels());
        assertEquals("a test", childChannels.get(0).getName());
    }

    @Test
    public void testKeywordProcessing() throws Exception {
        DiscoverChannelList dcList = dcListTF.create();
        dcList.setChannelNameMacro(DiscoverChannelList.KEYWORD_TOKEN);
        dcList.setName(dcListTF.getTestEntityRandomName());
        dcList.setKeywordList("\"base keyword\",ptrigger,,strigger,,annotation");
        dcListService.create(dcList);

        clearContext();
        DiscoverChannelList persisted = dcListService.find(dcList.getId());
        getEntityManager().refresh(persisted);

        List<Channel> childChannels = new ArrayList<Channel>(persisted.getChildChannels());
        assertEquals("base keyword", childChannels.get(0).getName());
    }

    @Test
    public void testUpdateKeywords() throws Exception {
        DiscoverChannelList dcList = dcListTF.create();
        dcList.setName(dcListTF.getTestEntityRandomName());
        dcList.setKeywordList("Test");
        dcListService.create(dcList);
        commitChanges();

        clearContext();
        dcList = dcListService.findById(dcList.getId());
        assertFalse(dcList.getChildChannels().isEmpty());
    }

    @Test
    public void testDuplicateCreate() throws Exception {
        InternalAccount account = internalAccountTF.createPersistent();
        Country country = countryTF.findOrCreatePersistent("US");
        String dclName = dcListTF.getTestEntityRandomName();
        String keyword = dcListTF.getTestEntityRandomName();

        DiscoverChannelList dcList = dcListTF.create(account);
        dcList.setCountry(country);
        dcList.setName(dclName);
        dcList.setKeywordList(keyword);
        dcListService.create(dcList);
        commitChanges();
        clearContext();

        clearContext();
        dcListService.delete(dcList.getId());
        commitChanges();

        DiscoverChannelList dcList2 = dcListTF.create(account);
        dcList2.setCountry(country);
        dcList2.setName(dclName);
        dcList2.setKeywordList(keyword);
        dcListService.create(dcList2);
        commitChanges();
        clearContext();

        validationServiceBean.savePoint();

        DiscoverChannelList dcList3 = dcListTF.create(account);
        dcList3.setCountry(country);
        dcList3.setName(dclName);
        dcList3.setKeywordList(keyword);
        boolean duplicatedDCLName = false;
        try {
            dcListService.create(dcList3);
        } catch (ConstraintViolationException e) {
            assertTrue(e.getConstraintViolations().size() == 1);
            ConstraintViolation violation = e.getConstraintViolations().iterator().next();
            if (violation.getPropertyPath().toString().equals("name") &&
                    BusinessErrors.ENTITY_DUPLICATE.getCode()==violation.getError().getCode()) {
                duplicatedDCLName = true;
            }
        }
        assertTrue(duplicatedDCLName);
        clearContext();
    }

    @Test
    public void testDuplicateRename() throws Exception {
        InternalAccount account = internalAccountTF.createPersistent();
        Country country = countryTF.findOrCreatePersistent("US");
        String dclName1 = dcListTF.getTestEntityRandomName();
        String dclName2 = dcListTF.getTestEntityRandomName();
        String keyword1 = dcListTF.getTestEntityRandomName();
        String keyword2 = dcListTF.getTestEntityRandomName();

        DiscoverChannelList dcList = dcListTF.create(account);
        dcList.setCountry(country);
        dcList.setName(dclName1);
        dcList.setKeywordList(keyword1);
        dcListService.create(dcList);
        commitChanges();
        clearContext();

        clearContext();
        dcListService.delete(dcList.getId());
        commitChanges();

        DiscoverChannelList dcList2 = dcListTF.create(account);
        dcList2.setCountry(country);
        dcList2.setName(dclName1);
        dcList2.setKeywordList(keyword1);
        dcListService.create(dcList2);
        Long dcListId2 = dcList2.getId();
        commitChanges();
        clearContext();

        dcList = dcListTF.create(account);
        dcList.setCountry(country);
        dcList.setName(dclName2);
        dcList.setKeywordList(keyword2);
        dcListService.create(dcList);
        commitChanges();
        clearContext();

        validationServiceBean.savePoint();

        dcList = dcListService.findById(dcListId2);
        dcList.setName(dclName2);
        dcList.setKeywordList(keyword2);
        clearContext();
        boolean duplicatedDCLName = false;
        try {
            dcListService.update(dcList);
        } catch (ConstraintViolationException e) {
            assertTrue(e.getConstraintViolations().size() == 1);
            ConstraintViolation violation = e.getConstraintViolations().iterator().next();
            if (violation.getPropertyPath().toString().equals("name") &&
                    BusinessErrors.ENTITY_DUPLICATE.getCode()==violation.getError().getCode()) {
                duplicatedDCLName = true;
            }
        }
        assertTrue(duplicatedDCLName);
        clearContext();
    }

    private void assertLinked(DiscoverChannel dcChannel, DiscoverChannelList dcList) {
        dcChannel = dcTF.refresh(dcChannel);
        dcList = dcListTF.refresh(dcList);

        assertEquals(dcList, dcChannel.getChannelList());
        assertTrue(dcList.getChildChannels().contains(dcChannel));
        assertTrue(dcList.getKeywordList().contains("Forth"));
        assertEquals(dcList.getAccount(), dcChannel.getAccount());
        assertEquals(dcList.getBehavParamsList(), dcChannel.getBehavParamsList());
        assertEquals(dcList.getCountry(), dcChannel.getCountry());
        assertEquals(dcList.getLanguage(), dcChannel.getLanguage());
        assertEquals(dcList.getCategories(), dcChannel.getCategories());
    }

    private void checkTriggerLists(String expected, String actual) {
        Set<String> expectedKw = new LinkedHashSet<String>(
                Arrays.asList(StringUtil.splitAndTrim(expected)));
        Set<String> actualKw = new LinkedHashSet<String>(
                Arrays.asList(StringUtil.splitAndTrim(actual)));
        assertEquals(expectedKw, actualKw);
    }

    private void assertEqualsCaseless(String expected, String actual) {
        assertEquals(StringUtil.trimAndLower(expected), StringUtil.trimAndLower(actual));
    }

    private void assertPropertiesEquals(DiscoverChannelList dcList, DiscoverChannel ch) {
        assertNotNull(ch.getId());

        assertEqualsCaseless("child_" + ch.getBaseKeyword(), ch.getName());

        assertEqualsCaseless("kwtrigger_" + ch.getBaseKeyword(), ch.getPageKeywords().getPositiveString());

        assertEqualsCaseless("", ch.getPageKeywords().getNegativeString());

        assertEqualsCaseless("kwtrigger_" + ch.getBaseKeyword(), ch.getSearchKeywords().getPositiveString());

        assertEqualsCaseless("", ch.getSearchKeywords().getNegativeString());

        assertEquals("annotation_" + ch.getBaseKeyword(), ch.getDiscoverAnnotation());

        assertEquals("query_" + ch.getBaseKeyword(), ch.getDiscoverQuery());

        assertEquals("Aaa " + ch.getBaseKeyword() + " Bbb", ch.getDescription());

        assertEquals(dcList.getAccount(), ch.getAccount());
        assertEquals(dcList.getCountry(), ch.getCountry());

        assertEquals(dcList.getStatus(), ch.getStatus());
    }

    private DiscoverChannel findChannelByKeyword(String kw, DiscoverChannelList dcList) {
        for (DiscoverChannel dc : dcList.getChildChannels()) {
            if (kw.equals(dc.getBaseKeyword())) {
                return dc;
            }
        }
        fail("Channels with keyword: " + kw + " not found");
        throw new RuntimeException("Channels with keyword: " + kw + " not found");
    }

    private DiscoverChannelList createHugeChildrenAmountPersistent() {
        InternalAccount account = internalAccountTF.createPersistent();
        StringBuilder children = new StringBuilder();
        for (int i = 0; i <= 1000; i++) {
            children.append("child-");
            children.append(String.valueOf(i));
            children.append("\r\n");
        }
        return dcListTF.createPersistent(account, children.toString());
    }
}
