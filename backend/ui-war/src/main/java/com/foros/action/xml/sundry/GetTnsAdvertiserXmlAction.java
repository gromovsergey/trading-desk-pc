package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.model.account.TnsAdvertiser;
import com.foros.session.account.yandex.advertiser.YandexTnsAdvertiserService;

import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class GetTnsAdvertiserXmlAction extends AbstractXmlAction<List<TnsAdvertiser>> {
    @EJB
    private YandexTnsAdvertiserService yandexTnsAdvertiserService;

    private String query;

    @RequiredStringValidator(key = "errors.required", message = "query")
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public List<TnsAdvertiser> generateModel() throws ProcessException {
        return yandexTnsAdvertiserService.searchAdvertisers(query, AUTOCOMPLETE_SIZE);
    }

}