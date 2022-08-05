package com.foros.action.admin.walledGarden;

import com.foros.model.account.AgencyAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.admin.WalledGarden;
import com.foros.session.account.AccountService;
import com.foros.validation.annotation.Validate;

import javax.ejb.EJB;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;

public class SaveWalledGardenAction extends EditSaveWalledGardenActionBase implements BreadcrumbsSupport {

    @EJB
    private AccountService accountService;

    private String countryCode;

    public SaveWalledGardenAction() {
        entity = new WalledGarden();
    }

    @Validations(
        requiredStrings = {
            @RequiredStringValidator(key="errors.field.required", fieldName="countryCode")
        }
    )
    @Validate(validation = "WalledGarden.create", parameters = "#target.model")
    public String saveNew() {
        if (entity.getAgency() != null && entity.getAgency().getId() != null) {
            entity.setAgency((AgencyAccount) accountService.view(entity.getAgency().getId()));
        }
        if (entity.getPublisher() != null && entity.getPublisher().getId() != null) {
            entity.setPublisher((PublisherAccount) accountService.view(entity.getPublisher().getId()));
        }
        walledGardenService.create(entity);

        return SUCCESS;
    }

    @Validate(validation = "WalledGarden.update", parameters = "#target.model")
    public String save() {
        WalledGarden existing = walledGardenService.view(entity.getId());
        entity.setAgency(existing.getAgency());
        entity.setPublisher(existing.getPublisher());

        walledGardenService.update(entity);

        return SUCCESS;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs().add(new WalledGardenBreadcrumbsElement());
        if (entity.getId() != null) {
            breadcrumbs.add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs.add(ActionBreadcrumbs.CREATE);
        }
        return breadcrumbs;
    }
}
