package com.foros.action.admin.country;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.restriction.annotation.Restrict;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;

public class EditCountryAction extends EditCountryActionSupport implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    @Restrict(restriction = "Country.update")
    public String edit() throws Exception {
        if (StringUtils.isBlank(id) || !CountrySource.getCountryCodes().contains(id)) {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        country = countryService.find(getId());

        prepareConfigValues();
        prepareAddressAndFile();
        breadcrumbs = new Breadcrumbs().add(new CountriesBreadcrumbsElement()).add(new CountryBreadcrumbsElement(country)).add(ActionBreadcrumbs.EDIT);

        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
