package com.foros.session.campaign;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.ApproveStatus;
import com.foros.model.DisplayStatus;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.session.EntityTO;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.CreativeLinkSelector;
import com.foros.session.creative.CreativeService;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.DisplayCreativeLinkTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.util.EntityUtils;

import group.Db;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

@Category(Db.class)
public class CampaignCreativeServiceIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CampaignCreativeService campaignCreativeService;

    @Autowired
    private CampaignCreativeGroupService ccgService;

    @Autowired
    private DisplayCreativeLinkTestFactory creativeLinkTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTF;

    @Autowired
    private DisplayCreativeTestFactory displayCreativeTF;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Autowired
    private DisplayCreativeService displayCreativeService;

    @Autowired
    private CreativeService creativeService;

    private CampaignCreative campaignCreative;
    private CampaignCreativeGroup campaignCreativeGroup;
    private Creative creative;
    private CreativeSize creativeSize;
    private CreativeTemplate creativeTemplate;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        creativeSize = creativeSizeTF.create();
        creativeTemplate = creativeTemplateTF.create();
        creativeTemplate.setStatus(Status.ACTIVE);
        Campaign campaign = displayCampaignTF.createPersistent();
        creative = displayCreativeTF.create(campaign.getAccount(), creativeTemplate, creativeSize);
        creative.setDisplayStatus(Creative.PENDING_FOROS);
        creative.setQaStatus(ApproveStatus.HOLD);
        campaignCreative = creativeLinkTF.create(creative);
        campaignCreativeGroup = displayCCGTF.createPersistent(campaign);
        campaignCreativeGroup.setStatus(Status.INACTIVE);
        campaignCreativeGroup.getCampaignCreatives().add(campaignCreative);
        campaignCreative.setCreativeGroup(campaignCreativeGroup);
    }

    @Test
    public void testCreateUpdateDeleteWithApprovedCCG() throws Exception {
        testCreateUpdateDelete(ApproveStatus.APPROVED, ApproveStatus.APPROVED);
    }

    @Test
    public void testCreateUpdateDeleteWithDeclinedCCG() throws Exception {
        testCreateUpdateDelete(ApproveStatus.DECLINED, ApproveStatus.DECLINED);
    }

    @Test
    public void testActivateWithApprovedCCG() throws Exception {
        testActivate(ApproveStatus.APPROVED);
    }

    @Test
    public void testActivateWithDeclinedCCG() throws Exception {
        testActivate(ApproveStatus.DECLINED);
    }

    @Test
    public void testDeactivateWithApprovedCCG() throws Exception {
        testDeactivate(ApproveStatus.APPROVED);
    }

    @Test
    public void testDeactivateWithDeclinedCCG() throws Exception {
        testDeactivate(ApproveStatus.DECLINED);
    }

    private boolean ccExists(Collection<EntityTO> entities, Long ccId) {
        for (EntityTO entity : entities) {
            if (entity.getId().equals(ccId)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testSearchCreativesForCCG() {
        setDeletedObjectsVisible(true);
        preCreate();

        campaignCreativeService.create(campaignCreative);
        entityManager.flush();
        entityManager.refresh(campaignCreative);

        getEntityManager().clear();
        campaignCreativeService.delete(campaignCreative.getId());
        commitChanges();

        List<TreeFilterElementTO> creatives = campaignCreativeService.searchCreatives(campaignCreativeGroup.getId());
        assertTrue(creatives.size() == 1);

        setDeletedObjectsVisible(false);
        creatives = campaignCreativeService.searchCreatives(campaignCreativeGroup.getId());
        assertTrue(creatives.size() == 0);
    }

    @Test
    public void testCreativesForReport() {
        setDeletedObjectsVisible(true);
        preCreate();

        campaignCreativeService.create(campaignCreative);
        entityManager.flush();
        entityManager.refresh(campaignCreative);

        getEntityManager().clear();
        displayCreativeTF.delete(creative.getId());
        commitChanges();

        Collection<EntityTO> entities = creativeService.findCreativesForReport(creative.getAccount().getId(), null, null, null, 100);
        assertTrue(ccExists(entities, creative.getId()));

        setDeletedObjectsVisible(false);
        entities = creativeService.findCreativesForReport(creative.getAccount().getId(), null, null, null, 100);
        assertFalse(ccExists(entities, creative.getId()));
    }

    @Test
    public void testDisplayStatus() throws Exception {
        HashSet<DisplayStatus> displayStatuses = new HashSet<>(CampaignCreative.getAvailableDisplayStatuses());
        preCreate();

        campaignCreativeService.create(campaignCreative);
        entityManager.flush();

        displayCreativeService.approve(creative.getId());
        entityManager.flush();
        verifyAllStatuses(displayStatuses);

        displayCreativeService.decline(creative.getId(), "Reason");
        entityManager.flush();
        verifyAllStatuses(displayStatuses);

        displayCreativeService.inactivate(creative.getId());
        entityManager.flush();
        verifyAllStatuses(displayStatuses);

        // replace 1 with 0 when budget is implemented
        for (DisplayStatus displayStatus : displayStatuses) {
            System.out.println(displayStatus);
        }
        assertEquals(0, displayStatuses.size());
    }

    private void verifyCampaignCreativeDisplayStatus(CampaignCreative cc, HashSet<DisplayStatus> displayStatuses) {
        campaignCreativeService.update(cc);
        entityManager.flush();
        entityManager.refresh(cc);
        displayStatuses.remove(cc.getDisplayStatus());

        verifyBulkUpdateDisplayStatus(cc);
    }

    private void preCreate() {
        creativeSizeTF.persist(creativeSize);
        getEntityManager().persist(creativeTemplate);
        getEntityManager().persist(creative);

        getEntityManager().flush();
    }

    private void verifyAllStatuses(HashSet<DisplayStatus> displayStatuses) {
        for (Status status : EntityUtils.getAllowedStatuses(CampaignCreative.class)) {
            campaignCreative.setStatus(status);
            verifyCampaignCreativeDisplayStatus(campaignCreative, displayStatuses);
        }
    }

    private void testDeactivate(ApproveStatus ccgQaStatus) throws Exception {
        preCreate();

        Long ccId = campaignCreativeService.create(campaignCreative);
        entityManager.flush();
        entityManager.refresh(campaignCreative);

        // admin activation
        ccgService.activate(campaignCreativeGroup.getId());
        // external user activation
        // Commented for now. See OUI-25720
//        ccgService.activate(campaignCreativeGroup.getId());

        if (ccgQaStatus == ApproveStatus.DECLINED) {
            ccgService.decline(campaignCreativeGroup.getId(), "Testing");
        }

        campaignCreativeService.inactivate(ccId);
        entityManager.flush();

        entityManager.refresh(campaignCreative);
        assertEquals(Status.INACTIVE, campaignCreative.getStatus());

        entityManager.refresh(campaignCreativeGroup);
        assertEquals(Status.ACTIVE, campaignCreativeGroup.getStatus());
        assertEquals(ccgQaStatus, campaignCreativeGroup.getQaStatus());
    }

    private FrequencyCap findFrequencyCap(final CampaignCreative campaignCreative) {
        return jdbcTemplate.query(
                "select * from CAMPAIGNCREATIVE cc, FREQCAP fc where cc.FREQ_CAP_ID = fc.FREQ_CAP_ID and cc.CC_ID=?",
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, campaignCreative.getId());
                    }
                },

                new ResultSetExtractor<FrequencyCap>() {
                    @Override
                    public FrequencyCap extractData(ResultSet rs) throws SQLException, DataAccessException {
                        if (rs.next()) {
                            FrequencyCap loadedFrequencyCap = new FrequencyCap();
                            loadedFrequencyCap.setId(rs.getLong("FREQ_CAP_ID"));
                            loadedFrequencyCap.setPeriod(rs.getInt("PERIOD"));
                            return loadedFrequencyCap;
                        } else {
                            return null;
                        }
                    }
                });
    }

    private void testActivate(ApproveStatus ccgQaStatus) throws Exception {
        preCreate();
        Long ccId = campaignCreativeService.create(campaignCreative);
        entityManager.flush();
        entityManager.refresh(campaignCreative);

        campaignCreativeService.inactivate(ccId);
        entityManager.flush();
        entityManager.refresh(campaignCreative);
        //need to activate group otherwise can't activate campaign creative
        ccgService.activate(campaignCreative.getCreativeGroup().getId());

        if (ccgQaStatus == ApproveStatus.DECLINED) {
            ccgService.decline(campaignCreativeGroup.getId(), "Testing");
        }

        campaignCreativeService.activate(ccId);
        entityManager.flush();
        entityManager.refresh(campaignCreative);
        assertEquals(Status.ACTIVE, campaignCreative.getStatus());

        getEntityManager().refresh(campaignCreativeGroup);
        // Commented for now. See OUI-25720
//        assertEquals(Status.PENDING, campaignCreativeGroup.getStatus());
        assertEquals(ccgQaStatus, campaignCreativeGroup.getQaStatus());
    }

    private void testCreateUpdateDelete(ApproveStatus ccgQaStatus, ApproveStatus expected) throws Exception {
        preCreate();

        if (ccgQaStatus == ApproveStatus.DECLINED) {
            ccgService.decline(campaignCreativeGroup.getId(), "Testing");
        }
        // admin activation
        ccgService.activate(campaignCreativeGroup.getId());
        // external user activation
        // Commented for now. See OUI-25720
//        ccgService.activate(campaignCreativeGroup.getId());
        getEntityManager().flush();

        campaignCreativeService.create(campaignCreative);
        entityManager.flush();
        assertNotNull("CampaignCreative must be created", campaignCreative.getId());

        getEntityManager().refresh(campaignCreativeGroup);
        assertEquals(Status.ACTIVE, campaignCreativeGroup.getStatus());
        assertEquals(expected, campaignCreativeGroup.getQaStatus());

        entityManager.clear();

        final FrequencyCap frequencyCap = new FrequencyCap();
        frequencyCap.setPeriod(100);
        campaignCreative.setFrequencyCap(frequencyCap);
        campaignCreative.setWeight(500L);

        campaignCreativeService.update(campaignCreative);
        entityManager.flush();
        campaignCreative = campaignCreativeService.find(campaignCreative.getId());
        FrequencyCap foundFrequencyCap = findFrequencyCap(campaignCreative);

        assertEquals(foundFrequencyCap.getId(), campaignCreative.getFrequencyCap().getId());
        assertEquals(foundFrequencyCap.getPeriod(), campaignCreative.getFrequencyCap().getPeriod());

        entityManager.clear();

        campaignCreative.getFrequencyCap().setPeriod(200);
        campaignCreativeService.update(campaignCreative);
        entityManager.flush();
        campaignCreative = campaignCreativeService.find(campaignCreative.getId());
        foundFrequencyCap = findFrequencyCap(campaignCreative);
        assertEquals(foundFrequencyCap.getId(), campaignCreative.getFrequencyCap().getId());
        assertEquals(foundFrequencyCap.getPeriod(), campaignCreative.getFrequencyCap().getPeriod());

        campaignCreative.setFrequencyCap(null);
        campaignCreativeService.update(campaignCreative);
        entityManager.flush();
        entityManager.refresh(campaignCreative);
        foundFrequencyCap = findFrequencyCap(campaignCreative);
        assertNull(foundFrequencyCap);
        assertNull(campaignCreative.getFrequencyCap());

        campaignCreativeGroup = ccgService.find(campaignCreativeGroup.getId());
        assertEquals(Status.ACTIVE, campaignCreativeGroup.getStatus());
        assertEquals(expected, campaignCreativeGroup.getQaStatus());

        campaignCreativeService.delete(campaignCreative.getId());
        entityManager.flush();
        entityManager.refresh(campaignCreative);
        campaignCreative = campaignCreativeService.find(campaignCreative.getId());

        assertEquals(Status.DELETED, campaignCreative.getStatus());

        entityManager.refresh(campaignCreativeGroup);
        assertEquals(Status.ACTIVE, campaignCreativeGroup.getStatus());
        assertEquals(ccgQaStatus, campaignCreativeGroup.getQaStatus());
    }

    @Test
    public void testMoveToExistingSet() {
        CampaignCreative campaignCreative = creativeLinkTF.createPersistent();
        commitChanges();
        assertEquals(1, campaignCreative.getSetNumber().longValue());

        List<Long> ids = new ArrayList<>();
        Long ccId = campaignCreative.getId();
        ids.add(ccId);
        campaignCreativeService.moveCreativesToNewSet(campaignCreative.getCreativeGroup().getId(), ids, campaignCreative.getVersion(), 2L);
        commitChanges();
        clearContext();

        CampaignCreative updatedCampaignCreative = campaignCreativeService.find(ccId);
        assertEquals(2, updatedCampaignCreative.getSetNumber().longValue());

        campaignCreativeService.moveCreativesToExistingSet(campaignCreative.getCreativeGroup().getId(), ids, updatedCampaignCreative.getVersion(), 1L);
        commitChanges();
        clearContext();

        assertEquals(1, campaignCreativeService.find(ccId).getSetNumber().longValue());

    }

    @Test
    public void testMoveToNewSet() {
        CampaignCreative campaignCreative = creativeLinkTF.createPersistent();
        commitChanges();
        assertEquals(1, campaignCreative.getSetNumber().longValue());

        List<Long> ids = new ArrayList<>();
        Long ccId = campaignCreative.getId();
        ids.add(ccId);
        campaignCreativeService.moveCreativesToNewSet(campaignCreative.getCreativeGroup().getId(), ids, new Timestamp((new Date()).getTime()), 2L);
        commitChanges();
        clearContext();

        assertEquals(2, campaignCreativeService.find(ccId).getSetNumber().longValue());
    }

    @Test
    public void testGetCreativesMaxVersionByCcgId() {
        assertEquals(campaignCreative.getVersion(), campaignCreativeService.getCreativesMaxVersionByCcgId(campaignCreative.getCreativeGroup().getId()));
    }

    @Test
    public void testGetCreativeSetCount() {
        CampaignCreative campaignCreative = creativeLinkTF.createPersistent();
        commitChanges();
        clearContext();
        assertNotNull(campaignCreativeService.getCreativeSetCountByCcgId(campaignCreative.getCreativeGroup().getId()));
    }

    @Test
    public void testCreateAll() {
        Creative persistentCreative = displayCreativeTF.createPersistent();
        Campaign persistentCampaign = displayCampaignTF.createPersistent(persistentCreative.getAccount());
        List<Long> creativeIds = new ArrayList<>();
        creativeIds.add(persistentCreative.getId());

        int size = 10;
        Collection<Long> ccgIds = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ccgIds.add(displayCCGTF.createPersistent(persistentCampaign).getId());
        }
        commitChanges();
        clearContext();

        campaignCreativeService.createAll(persistentCampaign.getAccount().getId(), creativeIds, ccgIds, true);
        commitChanges();
        clearContext();

        for (Long ccgId : ccgIds) {
            CampaignCreativeGroup group = ccgService.view(ccgId);
            assertTrue(group.getCampaignCreatives().size() == 1);
            assertEquals(group.getCampaignCreatives().iterator().next().getCreative().getId(), persistentCreative.getId());
        }
    }

    @Test
    public void testGet() {
        CampaignCreative campaignCreative = creativeLinkTF.createPersistent();
        CreativeLinkSelector selector = new CreativeLinkSelector();
        selector.setAdvertiserIds(Collections.singletonList(campaignCreative.getAccount().getId()));
        Result<CampaignCreative> res = campaignCreativeService.get(selector);

        assertEquals(1, res.getEntities().size());

        CampaignCreative cc = res.getEntities().get(0);
        assertEquals(campaignCreative.getId(), cc.getId());
        assertEquals(campaignCreative.getVersion(), cc.getVersion());
        assertEquals(campaignCreative.getStatus(), cc.getStatus());
        assertEquals(campaignCreative.getAccount().getId(), cc.getAccount().getId());
        assertEquals(campaignCreative.getCreativeGroup().getId(), cc.getCreativeGroup().getId());
        assertEquals(campaignCreative.getCreative().getId(), cc.getCreative().getId());
    }
}
