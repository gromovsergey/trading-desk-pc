package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.model.account.TnsBrand;
import com.foros.session.account.yandex.brand.YandexTnsBrandService;

import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class GetTnsBrandXmlAction extends AbstractXmlAction<List<TnsBrand>> {
    @EJB
    private YandexTnsBrandService yandexTnsBrandService;

    private String query;

    @RequiredStringValidator(key = "errors.required", message = "query")
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public List<TnsBrand> generateModel() throws ProcessException {
        return yandexTnsBrandService.searchBrands(query, AUTOCOMPLETE_SIZE);
    }
}
