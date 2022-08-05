package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.site.TagsService;
import com.foros.util.EntityUtils;
import com.foros.util.StringUtil;

import java.util.Collection;
import java.util.Collections;

import javax.ejb.EJB;

public class TagsByIdXmlAction extends AbstractOptionsAction<EntityTO> {
    @EJB
    private TagsService tagsService;

    private String siteId;

    public TagsByIdXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(true));
    }

    @Override
    protected Collection<? extends EntityTO> getOptions() throws ProcessException {
        if (StringUtil.isPropertyNotEmpty(getSiteId())) {
            return EntityUtils.sortByStatus(tagsService.getList(StringUtil.toLong(getSiteId())));
        } else {
            return Collections.emptyList();
        }
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}
