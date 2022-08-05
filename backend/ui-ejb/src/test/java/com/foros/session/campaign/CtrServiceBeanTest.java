package com.foros.session.campaign;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.test.factory.TextCCGTestFactory;

import group.Db;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class })
public class CtrServiceBeanTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private CtrService ctrService;

    @Autowired
    private TextCCGTestFactory textCCGTestFactory;

    @Test
    public void testResetCtr() {
        CampaignCreativeGroup group = textCCGTestFactory.createPersistent();
        commitChanges();

        ctrService.resetCtr(group.getId(), group.getVersion());
        commitChanges();
        group = textCCGTestFactory.refresh(group);
        assertEquals(Long.valueOf(1L), group.getCtrResetId());

        ctrService.resetCtr(group.getId(), group.getVersion());
        commitChanges();
        group = textCCGTestFactory.refresh(group);
        assertEquals(Long.valueOf(2L), group.getCtrResetId());
    }


}
