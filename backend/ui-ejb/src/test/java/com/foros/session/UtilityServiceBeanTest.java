package com.foros.session;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.campaign.Campaign;
import com.foros.model.security.AccountType;
import com.foros.test.factory.InternalAccountTypeTestFactory;
import com.foros.test.factory.TextCampaignTestFactory;

import group.Db;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class UtilityServiceBeanTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private UtilityService utilityService;

    @Autowired
    private TextCampaignTestFactory campaignTF;

    @Autowired
    private InternalAccountTypeTestFactory accountTypeTF;

    private int MAX_NAME_LENGTH = 100;

    private Campaign createCampaign(String name) {
        Campaign campaign = campaignTF.create();
        campaign.setName(name);
        return campaign;
    }

    @Test
    public void testCalculateNameForCopyShortName() {
        final String name = "UtilitySvc Int. Test Campaign";
        final String expectedCopyName = "Copy of UtilitySvc Int. Test Campaign";

        Campaign campaign = createCampaign(name);
        String nameCopy = utilityService.calculateNameForCopy(campaign, MAX_NAME_LENGTH);
        assertEquals(expectedCopyName, nameCopy);
    }

    @Test
    public void testCalculateNameForCopyExisting() {
        final String name = "Copy of UtilitySvc Int. Test Campaign";
        final String expectedCopyName = "Copy of UtilitySvc Int. Test Campaign (1)";

        Campaign campaign = createCampaign(name);
        campaignTF.persist(campaign);

        String nameCopy = utilityService.calculateNameForCopy(campaign, MAX_NAME_LENGTH);
        assertEquals(expectedCopyName, nameCopy);
    }

    @Test
    public void testCalculateNameForCopyExisting2() {
        final String name = "Copy of UtilitySvc Int. Test Campaign (1)";
        final String expectedCopyName = "Copy of UtilitySvc Int. Test Campaign (2)";

        Campaign campaign = createCampaign(name);
        campaignTF.persist(campaign);

        String nameCopy = utilityService.calculateNameForCopy(campaign, MAX_NAME_LENGTH);
        assertEquals(expectedCopyName, nameCopy);
    }

    @Test
    public void testCalculateNameForCopyLongName() {
        final String name = "My Utility Service Integration Test To Calculate Name For Copy Testing Long Name With Campaign (1)";
        final String expectedCopyName = "Copy of My Utility Service Integration Test To Calculate Name For Copy Testing Long Name With Campai";

        Campaign campaign = createCampaign(name);
        campaignTF.persist(campaign);

        String nameCopy = utilityService.calculateNameForCopy(campaign, MAX_NAME_LENGTH);
        assertEquals(expectedCopyName, nameCopy);
    }

    @Test
    public void testCalculateNameForCopyLongName2() {
        final String name = "Copy of Utility Service Integration Test To Calculate Name For Copy Testing Long Name With Camp (1)";
        final String expectedCopyName = "Copy of Utility Service Integration Test To Calculate Name For Copy Testing Long Name With Camp (2)";

        Campaign campaign = createCampaign(name);
        campaignTF.persist(campaign);

        String nameCopy = utilityService.calculateNameForCopy(campaign, MAX_NAME_LENGTH);
        assertEquals(expectedCopyName, nameCopy);
    }

    @Test
    public void testCalculateNameForCopyLongName3() {
        final String name1 = "Utility Service Integration Test To Calculate Name For Copy Testing Long Name With Campaign";
        final String name2 = "Copy of Utility Service Integration Test To Calculate Name For Copy Testing Long Name With Campaign";
        final String expectedCopyName = "Copy of Utility Service Integration Test To Calculate Name For Copy Testing Long Name With Campa (1)";

        Campaign campaign1 = createCampaign(name1);
        campaignTF.persist(campaign1);

        Campaign campaign2 = createCampaign(name2);
        campaignTF.persist(campaign2);

        String nameCopy = utilityService.calculateNameForCopy(campaign1, MAX_NAME_LENGTH);
        assertEquals(expectedCopyName, nameCopy);
    }

    @Test
    public void testExtractNextCopyNumber() {
        UtilityServiceBean instance = new UtilityServiceBean();

        List<String> testDataList = new ArrayList<String>();
        testDataList.add("Copy of gdgsggs  gsdg sh");
        testDataList.add("Copy of gdgsggs  gsdg sh (2)");
        testDataList.add("Copy of gdgsggs  gsdg sh (7)");

        int result = instance.calculateNextCopyNumber(testDataList);
        assertEquals(1, result);
    }

    @Test
    public void testGetEntityTextList() {
        AccountType accountType = accountTypeTF.createPersistent();
        List<String> res = utilityService.getEntityTextList(AccountType.class, Arrays.asList(accountType.getId()));
        assertNotNull(res);
        assertEquals(1, res.size());
    }
}
