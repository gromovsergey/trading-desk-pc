package com.foros.action.xml.options;

import com.foros.util.helper.IndexHelper;
import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.site.SiteService;

import java.util.Collection;

import javax.ejb.EJB;

public class SitesXmlAction extends AbstractOptionsByAccountAction<EntityTO> {
    @EJB
    private SiteService siteService;

    private String publisherPair;

    public SitesXmlAction() {
        super(new NamedTOConverter(true), new OptionStatusFilter(true));
    }

    @AccountId
    public String getPublisherPair() {
        return publisherPair;
    }

    public void setPublisherPair(String publisherPair) {
        this.publisherPair = publisherPair;
    }

    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        return IndexHelper.getSitesList(accountId);
    }

}
