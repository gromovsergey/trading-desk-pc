package com.foros.action.site;

import com.foros.action.BaseActionSupport;
import com.foros.model.site.Site;
import com.foros.model.site.WDTag;
import com.foros.session.site.WDTagService;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ModelDriven;

public class WDTagActionSupport extends BaseActionSupport implements ServletRequestAware, ModelDriven<WDTag> {

    @EJB
    protected WDTagService wdTagService;

    protected WDTag wdTag = emptyWdTag();
    protected HttpServletRequest request;

    public String getEntityType() {
        return "WDTag";
    }

    @Override
    public WDTag getModel() {
        return wdTag;
    }

    public WDTag getEntity() {
        return wdTag;
    }

    public void setEntity(WDTag newWDTag) {
        wdTag = newWDTag;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    private WDTag emptyWdTag() {
        WDTag wdTag = new WDTag();
        wdTag.setSite(new Site());
        return wdTag;
    }

}
