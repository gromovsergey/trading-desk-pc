package com.foros.action.campaign;

import com.foros.action.BaseActionSupport;
import com.foros.security.principal.SecurityContext;
import com.foros.session.campaign.CampaignService;
import com.foros.util.context.AdvertiserContext;
import com.foros.util.context.SessionContexts;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

public class StatusCampaignAction extends BaseActionSupport implements ServletRequestAware {
    @EJB
    private CampaignService campaignService;

    private Long id;
    private Long advertiserId;

    private HttpServletRequest request;

    public String activate() {
        campaignService.activate(id);

        return SUCCESS;
    }

    public String inactivate() {
        campaignService.inactivate(id);

        return SUCCESS;
    }

    public String delete() {
        campaignService.delete(id);

        if (!SecurityContext.isInternal()) {
            AdvertiserContext advertiserContext = SessionContexts.getSessionContexts(request).getAdvertiserContext();

            if (advertiserContext.isAgencyAdvertiserSet()) {
                advertiserId = advertiserContext.getAgencyAdvertiserId();
            }
        }

        return SUCCESS;
    }

    public String undelete() throws Exception {
        campaignService.undelete(id);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
}
