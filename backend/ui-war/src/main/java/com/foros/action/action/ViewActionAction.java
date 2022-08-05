package com.foros.action.action;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.DisplayStatus;
import com.foros.model.action.Action;
import com.foros.session.action.ActionService;
import com.foros.session.admin.country.CountryService;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class ViewActionAction extends BaseActionSupport implements RequestContextsAware, ModelDriven<Action> {
    @EJB
    private ActionService actionService;

    @EJB
    private CountryService countryService;

    // parameter
    private Long id;

    // model
    private Action action;

    @ReadOnly
    public String view() {
        action = EntityUtils.applyOwnerStatusRule(actionService.view(id));
        return SUCCESS;
    }

    public String getConversionTrackingPixelCode() {
        String template = countryService.getConversionTrackingPixelCode(getModel().getAccount().getCountry(), getModel().getAccount().getId(), getModel().getId());
        return template;
    }

    public String getConversionTrackingNoAudiencePixelCode() {
        String template = countryService.getConversionTrackingNoAudiencePixelCode(getModel().getAccount().getCountry(), getModel().getAccount().getId(), getModel().getId());
        return template;
    }

    public String getImagePixel() {
        String template = countryService.getImagePixel(getModel().getAccount().getCountry(), getModel().getAccount().getId(), getModel().getId());
        return template;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatusHint() {
        DisplayStatus displayStatus = getModel().getDisplayStatus();
        if (Action.LIVE.equals(displayStatus) || Action.DELETED.equals(displayStatus)) {
            return "";
        }
        return getText(displayStatus.getDescription() + ".hint");
    }

    @Override
    public Action getModel() {
        return action;
    }

    @Override
    public void switchContext(RequestContexts context) {
        context.getAdvertiserContext().switchTo(action.getAccount());
    }

}
