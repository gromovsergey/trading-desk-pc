package com.foros.rs.sandbox.factory;

import com.foros.model.account.PublisherAccount;
import com.foros.model.site.Site;
import com.foros.test.factory.SiteTestFactory;

public class SiteGeneratorFactory extends BaseGeneratorFactory<Site, SiteTestFactory> {
    private PublisherAccount publisherAccount;

    public SiteGeneratorFactory(SiteTestFactory siteTestFactory, PublisherAccount publisherAccount) {
        super(siteTestFactory);
        this.publisherAccount = publisherAccount;
    }

    @Override
    protected Site createDefault() {
        return factory.create(publisherAccount);
    }
}
