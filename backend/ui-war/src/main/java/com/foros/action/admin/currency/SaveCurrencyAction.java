package com.foros.action.admin.currency;

import com.foros.action.Invalidable;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.currency.Currency;
import com.foros.model.currency.Source;
import com.foros.validation.annotation.Validate;

import java.io.IOException;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;


@Validations(
        conversionErrorFields =
        {@ConversionErrorFieldValidator(fieldName="rate", shortCircuit=true, key="errors.field.number")}
) 
public class SaveCurrencyAction extends CurrencyActionSupport implements Invalidable,ModelDriven<Currency>, BreadcrumbsSupport {

    private Currency currency = new Currency();

    @Validate(validation = "Currency.create", parameters = "#target.model")
    public String create() throws IOException {
        currencyService.create(currency);
        return SUCCESS;
    }

    @Validate(validation = "Currency.update", parameters = "#target.model")
    public String update() throws IOException {
        currencyService.update(currency);
        return SUCCESS;
    }

    @Override
    public Currency getModel() {
        return currency;
    }

    @Override
    public void invalid() throws Exception {
        if (hasFieldErrors() && getFieldErrors().containsKey("source")) {
            valueDoesntExistInFeed = true;
            getModel().setSource(Source.MANUAL);
            if (getModel().getId() != null) {
                Currency existing = currencyService.findById(getModel().getId());
                getModel().setRate(existing.getRate());
            }
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CurrenciesBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
    }
}
