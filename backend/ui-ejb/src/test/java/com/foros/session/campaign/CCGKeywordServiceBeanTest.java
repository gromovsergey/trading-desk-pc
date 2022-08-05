package com.foros.session.campaign;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;
import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.Status;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.session.UploadStatus;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.CCGKeywordSelector;
import com.foros.test.factory.TextCCGTestFactory;
import com.foros.test.factory.TextCampaignTestFactory;
import com.foros.util.CollectionUtils;
import com.foros.util.bean.Filter;

import group.Db;
import group.Validation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class CCGKeywordServiceBeanTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CCGKeywordService ccgKeywordService;

    @Autowired
    private TextCCGTestFactory textCCGTF;

    @Autowired
    private TextCampaignTestFactory textCampaignTF;

    @Test
    public void testUpdate() {
        CampaignCreativeGroup textCcg = createPersistedCCG();
        commitChanges();

        // add two keywords
        clearContext();
        Set<CCGKeyword> ccgKeywords = new HashSet<CCGKeyword>();
        ccgKeywords.add(textCCGTF.createCcgKeyword("foo"));
        ccgKeywords.add(textCCGTF.createCcgKeyword("bar"));
        ccgKeywordService.update(ccgKeywords, textCcg.getId(), textCcg.getVersion());
        commitChanges();

        clearContext();
        textCcg = getEntityManager().find(CampaignCreativeGroup.class, textCcg.getId());
        assertEquals(2, textCcg.getCcgKeywords().size());

        // add two keywords and set deleted status to one
        clearContext();
        ccgKeywords = new HashSet<CCGKeyword>();
        ccgKeywords.add(textCCGTF.createCcgKeyword("foo"));
        ccgKeywords.add(textCCGTF.createCcgKeyword(UUID.randomUUID().toString()));
        ccgKeywords.add(textCCGTF.createCcgKeyword(UUID.randomUUID().toString()));
        ccgKeywordService.update(ccgKeywords, textCcg.getId(), textCcg.getVersion());
        commitChanges();

        clearContext();
        textCcg = getEntityManager().find(CampaignCreativeGroup.class, textCcg.getId());
        assertEquals(4, textCcg.getCcgKeywords().size());

        //count non deleted
        CollectionUtils.filter(textCcg.getCcgKeywords(), new Filter<CCGKeyword>() {
            @Override
            public boolean accept(CCGKeyword element) {
                return element.getStatus() != Status.DELETED;
            }
        });
        assertEquals(3, textCcg.getCcgKeywords().size());
    }

    @Test
    public void testValidateAll() {
        Campaign campaign = textCampaignTF.createPersistent();

        CampaignCreativeGroup ccg = textCCGTF.create(campaign);
        ccg.setTgtType(TGTType.KEYWORD);
        ccg.setCampaign(campaign);
        textCCGTF.persist(ccg);

        Set<CCGKeyword> ccgKeywords = new HashSet<CCGKeyword>();

        // update CCGKeyword
        CCGKeyword keywordUpdated = createCCGKeywordWithValidFields("keywordUpdated");
        keywordUpdated.setCreativeGroup(ccg);
        ccgKeywords.add(keywordUpdated);
        ccgKeywordService.update(ccgKeywords, ccg.getId(), ccg.getVersion());

        // new CCGKeyword
        CCGKeyword keywordNew = createCCGKeywordWithValidFields("keywordNew");
        keywordNew.setCreativeGroup(ccg);
        ccgKeywords.add(keywordNew);

        ccgKeywordService.validateAll(ccg, ccgKeywords, TGTType.KEYWORD);

        assertEquals(UploadStatus.UPDATE, keywordUpdated.getProperty(UPLOAD_CONTEXT).getStatus());
        assertEquals(UploadStatus.NEW, keywordNew.getProperty(UPLOAD_CONTEXT).getStatus());
    }

    @Test
    public void testPerform() {
        Operations<CCGKeyword> operations = new Operations<CCGKeyword>();
        CampaignCreativeGroup textCcg = createPersistedCCG();
        commitChanges();

        CCGKeyword keywordUpdated = createCCGKeywordWithValidFields("keywordUpdated");

        ccgKeywordService.update(Arrays.asList(keywordUpdated), textCcg.getId(), textCcg.getVersion());

        CCGKeyword keywordCreated = createCCGKeywordWithValidFields("keywordCreated");
        keywordCreated.setCreativeGroup(new CampaignCreativeGroup(textCcg.getId()));

        operations.getOperations().add(createOperation(keywordUpdated, OperationType.UPDATE));
        operations.getOperations().add(createOperation(keywordCreated, OperationType.CREATE));
        commitChanges();

        OperationsResult result = ccgKeywordService.perform(operations);

        assertEquals(2, result.getIds().size());
        assertEquals(keywordUpdated.getId(), result.getIds().get(0));
        assertNotNull(result.getIds().get(1));
    }

    private CCGKeyword createCCGKeywordWithValidFields(String keyword) {
        CCGKeyword ccgKeyword = textCCGTF.createCcgKeyword(keyword);
        ccgKeyword.setStatus(Status.ACTIVE);
        return ccgKeyword;
    }

    private CampaignCreativeGroup createPersistedCCG() {
        Campaign campaign = textCampaignTF.createPersistent();
        CampaignCreativeGroup textCcg = textCCGTF.create(campaign);
        textCcg.setTgtType(TGTType.KEYWORD);
        textCCGTF.persist(textCcg);
        return textCcg;
    }

    private Operation<CCGKeyword> createOperation(CCGKeyword keyword, OperationType type) {
        Operation<CCGKeyword> operation = new Operation<CCGKeyword>();
        operation.setEntity(keyword);
        operation.setOperationType(type);
        return operation;
    }

    @Test
    public void testFindCCGKeywords() {
        CampaignCreativeGroup textCcg = createPersistedCCG();
        commitChanges();

        // add two keywords
        clearContext();
        Set<CCGKeyword> ccgKeywords = new HashSet<CCGKeyword>();
        ccgKeywords.add(textCCGTF.createCcgKeyword("foo"));
        ccgKeywords.add(textCCGTF.createCcgKeyword("bar"));
        ccgKeywordService.update(ccgKeywords, textCcg.getId(), textCcg.getVersion());
        commitChanges();
        clearContext();

        List<EditCCGKeywordTO> res = ccgKeywordService.findCCGKeywords(textCcg.getId());
        assertNotNull(res);
        assertEquals(2, res.size());
    }

    @Test
    public void testGet() throws Exception {
        CampaignCreativeGroup textCcg = createPersistedCCG();
        commitChanges();
        clearContext();

        Set<CCGKeyword> ccgKeywords = new HashSet<>();
        ccgKeywords.add(textCCGTF.createCcgKeyword("foo"));
        ccgKeywords.add(textCCGTF.createCcgKeyword("bar"));
        ccgKeywordService.update(ccgKeywords, textCcg.getId(), textCcg.getVersion());
        commitChanges();
        clearContext();

        CCGKeywordSelector selector = new CCGKeywordSelector();
        selector.setCreativeGroups(Arrays.asList(textCcg.getId()));
        Result<CCGKeyword> res = ccgKeywordService.get(selector);
        assertNotNull(res);

        List<CCGKeyword> entities = res.getEntities();
        assertNotNull(entities);
        assertEquals(2, entities.size());
    }
}
