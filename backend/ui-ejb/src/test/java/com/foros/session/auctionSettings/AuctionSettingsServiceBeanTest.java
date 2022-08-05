package com.foros.session.auctionSettings;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AccountAuctionSettings;
import com.foros.model.account.InternalAccount;
import com.foros.model.site.Tag;
import com.foros.model.site.TagAuctionSettings;
import com.foros.test.factory.InternalAccountTestFactory;
import com.foros.test.factory.TagsTestFactory;

import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AuctionSettingsServiceBeanTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    InternalAccountTestFactory internalAccountTF;

    @Autowired
    TagsTestFactory tagsTF;

    @Autowired
    AuctionSettingsService auctionSettingsService;

    @Test
    public void testCreateAccountAuctionSettings() {
        InternalAccount account = internalAccountTF.createPersistent();
        AccountAuctionSettings auctionSettings = auctionSettingsService.findByAccountId(account.getId());
        assertNotNull(auctionSettings);
    }

    @Test
    public void testUpdateAccountAuctionSettings() {
        InternalAccount account = internalAccountTF.createPersistent();
        AccountAuctionSettings auctionSettings = auctionSettingsService.findByAccountId(account.getId());

        BigDecimal maxEcpmShare = auctionSettings.getMaxEcpmShare().subtract(BigDecimal.valueOf(50));
        auctionSettings.setMaxEcpmShare(maxEcpmShare);
        auctionSettingsService.update(auctionSettings);

        commitChanges();
        clearContext();

        auctionSettings = auctionSettingsService.findByAccountId(account.getId());
        assertTrue(auctionSettings.getMaxEcpmShare().compareTo(maxEcpmShare) == 0);
    }

    @Test
    public void testFind() {
        Tag tag = tagsTF.createPersistent();
        TagAuctionSettings tagAuctionSettings = auctionSettingsService.findByTagId(tag.getId());
        assertNull(tagAuctionSettings);
        AccountAuctionSettings accountAuctionSettings = auctionSettingsService.findDefaultByTagId(tag.getId());
        assertNotNull(accountAuctionSettings);
        List<TagAuctionSettings> nonDefaultTags =
                auctionSettingsService.findNonDefaultTags(tag.getSite().getAccount().getInternalAccount().getId());
        assertNotNull(nonDefaultTags);
    }
}
