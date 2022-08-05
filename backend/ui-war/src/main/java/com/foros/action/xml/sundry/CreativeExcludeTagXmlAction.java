package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.session.creative.DisplayCreativeService;

import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class CreativeExcludeTagXmlAction extends AbstractXmlAction<List<CreativeCategory>> {
    @EJB
    private DisplayCreativeService displayCreativeService;

    private String query;

    @RequiredStringValidator(key = "errors.required", message = "query")
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<CreativeCategory> generateModel() throws ProcessException {
        return displayCreativeService.searchCategory(CreativeCategoryType.TAG, query, false, AUTOCOMPLETE_SIZE);
    }

}
