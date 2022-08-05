package com.foros.session.auctionSettings;

import com.foros.model.account.AccountAuctionSettings;
import com.foros.model.site.TagAuctionSettings;

import java.util.List;
import javax.ejb.Local;

@Local
public interface AuctionSettingsService {

    AccountAuctionSettings findByAccountId(Long id);

    TagAuctionSettings findByTagId(Long id);

    AccountAuctionSettings findDefaultByTagId(Long id);

    List<TagAuctionSettings> findNonDefaultTags(Long accountId);

    void update(AccountAuctionSettings auctionSettings);

    void update(TagAuctionSettings auctionSettings);
}
