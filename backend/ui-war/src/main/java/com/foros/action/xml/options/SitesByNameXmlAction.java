package com.foros.action.xml.options;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.site.SiteService;

import javax.ejb.EJB;
import java.util.Collection;

public class SitesByNameXmlAction extends AbstractOptionsAction<EntityTO> {
    @EJB
    private SiteService siteService;

    private String query;

    public SitesByNameXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(false));
    }

    @RequiredStringValidator(key = "errors.required", message = "query")
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    protected Collection<? extends EntityTO> getOptions() throws ProcessException {
        return siteService.searchByName(query, PAGE_SIZE);
    }
}
