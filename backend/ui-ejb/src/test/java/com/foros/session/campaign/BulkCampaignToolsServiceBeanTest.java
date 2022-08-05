package com.foros.session.campaign;

import static com.foros.config.ConfigParameters.TEXT_AD_IMAGES_FOLDER;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.config.MockConfigService;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.security.AccountType;
import com.foros.session.TooManyRowsException;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.campaign.bulk.BulkParseResult;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.TextCCGTestFactory;
import com.foros.test.factory.TextCampaignTestFactory;
import com.foros.util.UploadUtils;

import group.Db;
import group.Validation;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class BulkCampaignToolsServiceBeanTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private BulkCampaignToolsService bulkCampaignToolsService;

    @Autowired
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @Autowired
    private TextCampaignTestFactory textCampaignTF;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private TextCCGTestFactory textCCGTF;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    public AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private MockConfigService configService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        configService.set(TEXT_AD_IMAGES_FOLDER, "TextAdImages");
    }

    @Test
    public void testFindForExport() throws TooManyRowsException {
        CampaignCreativeGroup group = textCCGTF.create();
        group.setCampaign(textCampaignTF.createPersistent());
        group.setDateStart(group.getCampaign().getDateStart());
        campaignCreativeGroupService.create(group);
        persist(group);

        entityManager.clear();
        CampaignCreativeGroup found = campaignCreativeGroupService.find(group.getId());

        Collection<Campaign> campaigns = bulkCampaignToolsService.findForExport(
                found.getCampaign().getAccount().getId(), TGTType.KEYWORD, Collections.singleton(found.getCampaign().getId()), 2);

        assertNotNull(campaigns);
        assertFalse(campaigns.isEmpty());
    }

    @Test
    public void testValidate() throws IOException {
        Campaign campaign = textCampaignTF.createPersistent();
        CampaignCreativeGroup group = textCCGTF.create(campaign);
        commitChanges();
        campaign.getCreativeGroups().add(group);

        UploadUtils.setRowNumber(campaign, 1L);

        clearContext();
        ValidationResultTO res = bulkCampaignToolsService.validateAll(campaign.getAccount().getId(), TGTType.KEYWORD, new BulkParseResult(Arrays.asList(group.getCampaign()), Collections.EMPTY_LIST));
        assertNotNull(res);
        assertNotNull(res.getId());

        Collection<Campaign> validated = bulkCampaignToolsService.getValidatedResults(campaign.getAccount().getId(), res.getId()).getCampaigns();
        assertNotNull(validated);
        assertEquals(1, validated.size());
        Campaign validatedCampaign = validated.iterator().next();
        assertEquals(Long.valueOf(1L), UploadUtils.getRowNumber(validatedCampaign));
    }

    @Test
    public void testValidateBudget() throws IOException {
        Campaign campaign = textCampaignTF.create();
        campaign.setBudget(BigDecimal.ZERO);
        textCampaignTF.persist(campaign);
        CampaignCreativeGroup group = textCCGTF.create(campaign);
        campaign.getCreativeGroups().add(group);
        AccountType accountType = campaign.getAccount().getAccountType();
        accountType.setIoManagement(true);
        entityManager.merge(accountType);

        UploadUtils.setRowNumber(campaign, 1L);

        commitChanges();
        clearContext();

        //Setting new budget
        campaign.setBudget(BigDecimal.TEN);
        ValidationResultTO res = bulkCampaignToolsService.validateAll(campaign.getAccount().getId(), TGTType.KEYWORD, new BulkParseResult(Arrays.asList(campaign), Collections.EMPTY_LIST));
        assertNotNull(res);
        assertNotNull(res.getId());

        Collection<Campaign> validated = bulkCampaignToolsService.getValidatedResults(campaign.getAccount().getId(), res.getId()).getCampaigns();
        assertNotNull(validated);
        assertEquals(1, validated.size());

        Campaign validatedCampaign = validated.iterator().next();
        assertEquals(Long.valueOf(1L), UploadUtils.getRowNumber(validatedCampaign));
        UploadContext uploadStatus = UploadUtils.getUploadContext(validatedCampaign);
        assertEquals(uploadStatus.getStatus(), UploadStatus.REJECTED);
        assertTrue(uploadStatus.getWrongPaths().contains("budget"));
    }

    @Test
    public void testValidateNotModifiedBudget() throws IOException {
        Campaign campaign = textCampaignTF.create();
        campaign.setBudget(BigDecimal.ZERO);
        textCampaignTF.persist(campaign);
        CampaignCreativeGroup group = textCCGTF.create(campaign);
        campaign.getCreativeGroups().add(group);
        AccountType accountType = campaign.getAccount().getAccountType();
        accountType.setIoManagement(true);
        entityManager.merge(accountType);

        UploadUtils.setRowNumber(campaign, 1L);

        commitChanges();
        clearContext();

        //Leaving budget as is
        campaign.setBudget(null);
        ValidationResultTO res = bulkCampaignToolsService.validateAll(campaign.getAccount().getId(), TGTType.KEYWORD, new BulkParseResult(Arrays.asList(campaign), Collections.EMPTY_LIST));
        assertNotNull(res);
        assertNotNull(res.getId());

        Collection<Campaign> validated = bulkCampaignToolsService.getValidatedResults(campaign.getAccount().getId(), res.getId()).getCampaigns();
        assertNotNull(validated);
        assertEquals(1, validated.size());

        Campaign validatedCampaign = validated.iterator().next();
        assertEquals(Long.valueOf(1L), UploadUtils.getRowNumber(validatedCampaign));
        UploadContext uploadStatus = UploadUtils.getUploadContext(validatedCampaign);
        assertEquals(uploadStatus.getStatus(), UploadStatus.UPDATE);
    }

    @Test
    public void testValidateNotModifiedBudgetCreate() throws IOException {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        AccountType accountType = account.getAccountType();
        accountType.setIoManagement(true);
        entityManager.merge(accountType);

        Campaign campaign = textCampaignTF.create(account);
        Set<CampaignCreativeGroup> creativeGroups = new HashSet<CampaignCreativeGroup>();
        creativeGroups.add(textCCGTF.create(campaign));
        campaign.setCreativeGroups(creativeGroups);
        campaign.setBudget(null);

        UploadUtils.setRowNumber(campaign, 1L);

        ValidationResultTO res = bulkCampaignToolsService.validateAll(campaign.getAccount().getId(), TGTType.KEYWORD, new BulkParseResult(Arrays.asList(campaign), Collections.EMPTY_LIST));
        assertNotNull(res);
        assertNotNull(res.getId());

        Collection<Campaign> validated = bulkCampaignToolsService.getValidatedResults(campaign.getAccount().getId(), res.getId()).getCampaigns();
        assertNotNull(validated);
        assertEquals(1, validated.size());

        Campaign validatedCampaign = validated.iterator().next();
        assertEquals(Long.valueOf(1L), UploadUtils.getRowNumber(validatedCampaign));
        UploadContext uploadStatus = UploadUtils.getUploadContext(validatedCampaign);
        assertEquals(UploadStatus.NEW, uploadStatus.getStatus());
    }

    @Test
    public void testValidateDisplayCampaign() throws IOException {
        Campaign campaign = displayCampaignTF.createPersistent();
        CampaignCreativeGroup group = displayCCGTF.create(campaign);
        campaign.getCreativeGroups().add(group);
        commitChanges();

        UploadUtils.setRowNumber(campaign, 1L);

        clearContext();
        ValidationResultTO res = bulkCampaignToolsService.validateAll(campaign.getAccount().getId(), TGTType.KEYWORD, new BulkParseResult(Arrays.asList(group.getCampaign()), Collections.EMPTY_LIST));
        assertNotNull(res);
        assertNotNull(res.getId());
        assertEquals(2, res.getLineWithErrors());

        Collection<Campaign> validated = bulkCampaignToolsService.getValidatedResults(campaign.getAccount().getId(), res.getId()).getCampaigns();
        assertNotNull(validated);
        assertEquals(1, validated.size());
        Campaign validatedCampaign = validated.iterator().next();
        assertEquals(Long.valueOf(1L), UploadUtils.getRowNumber(validatedCampaign));
        UploadContext uploadStatus = UploadUtils.getUploadContext(validatedCampaign);
        assertEquals(uploadStatus.getStatus(), UploadStatus.REJECTED);
    }
}
