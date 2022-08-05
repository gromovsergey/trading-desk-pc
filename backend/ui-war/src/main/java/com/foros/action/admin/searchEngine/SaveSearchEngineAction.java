package com.foros.action.admin.searchEngine;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.admin.SearchEngine;
import com.foros.session.admin.searchEngine.SearchEngineService;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import javax.ejb.EJB;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "decodingDepth", key = "errors.field.integer")
        }
)
public class SaveSearchEngineAction extends BaseActionSupport implements ModelDriven<SearchEngine>, BreadcrumbsSupport {

    @EJB
    private SearchEngineService searchEngineService;

    private SearchEngine searchEngine = new SearchEngine();

    @Validate(validation = "SearchEngine.create", parameters = "#target.model")
    public String create() {
        searchEngineService.create(searchEngine);
        return SUCCESS;
    }

    @Validate(validation = "SearchEngine.update", parameters = "#target.model")
    public String update() {
        searchEngineService.update(searchEngine);
        return SUCCESS;
    }

    @Override
    public SearchEngine getModel() {
        return searchEngine;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs;
        if (searchEngine.getId() != null) {
            SearchEngine persistent = searchEngineService.findById(searchEngine.getId());
            breadcrumbs = new Breadcrumbs().add(new SearchEnginesBreadcrumbsElement()).add(new SearchEngineBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = new Breadcrumbs().add(new SearchEnginesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
