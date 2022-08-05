package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.site.TagsService;
import com.foros.util.EntityUtils;
import com.foros.util.PairUtil;
import com.foros.util.StringUtil;

import java.util.Collection;
import java.util.Collections;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;

public class TagsXmlAction extends AbstractOptionsAction<EntityTO> {
    @EJB
    private TagsService tagsService;

    private String sitePair;

    public TagsXmlAction() {
        super(new NamedTOConverter(true), new OptionStatusFilter(true));
    }

    // todo decomment it and change logic for sending empty requests
    //@RequiredFieldValidator(key = "errors.required", message = "sitePair")
    @CustomValidator(type = "pair", key = "errors.pair", message = "sitePair")
    public String getSitePair() {
        return sitePair;
    }

    public void setSitePair(String sitePair) {
        this.sitePair = sitePair;
    }

    @Override
    protected Collection<? extends EntityTO> getOptions() throws ProcessException {
        if (StringUtil.isPropertyNotEmpty(getSitePair())) {
            return EntityUtils.sortByStatus(tagsService.getList(PairUtil.fetchId(getSitePair())));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public NamedTOConverter getConverter() {
        return (NamedTOConverter) super.getConverter();
    }

    public void setConcatResultForValue(boolean concatResultForValue) {
        getConverter().setConcatForValue(concatResultForValue);
    }
}
