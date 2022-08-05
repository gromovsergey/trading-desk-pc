package com.foros.action.xml.options;

import com.foros.util.helper.IndexHelper;
import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.site.SiteService;

import java.util.Collection;
import javax.ejb.EJB;

public class SitesByIdXmlAction extends AbstractOptionsByAccountAction<EntityTO> {

    @EJB
    private SiteService siteService;

    private String publisherId;

    public SitesByIdXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(true));
    }

    @AccountId
    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        return IndexHelper.getSitesList(accountId);
    }

}
