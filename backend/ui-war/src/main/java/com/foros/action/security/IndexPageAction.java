package com.foros.action.security;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.security.User;
import com.foros.model.security.UserRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.security.UserService;
import com.foros.web.taglib.RestrictionTools;

import javax.ejb.EJB;

public class IndexPageAction extends BaseActionSupport {
    @EJB
    private UserService userSvc;

    @ReadOnly
    public String dispatch() throws Exception {
        if (SecurityContext.isAuthenticatedAndNotAnonymous()) {
            return SecurityContext.getAccountRole().getWebName();
        } else {
            return "anonymous";
        }
    }

    @ReadOnly
    public String index() throws Exception {
        User currentUser = userSvc.getMyUser();
        UserRole role = currentUser.getRole();

        // Internal, if Advertiser & Agency account manager
        if (SecurityContext.isInternal() && role.isAdvertiserAccountManager()
                && RestrictionTools.isPermitted("Context.switch", "Advertiser")) {
            // ADVERTISERS
            return "advertisers";
        }

        // Internal, if Publisher account manager
        if (SecurityContext.isInternal() && role.isPublisherAccountManager()
                && RestrictionTools.isPermitted("Context.switch", "Publisher")) {
            return "publishers";
        }

        // Internal, if ISP account manager
        if (SecurityContext.isInternal() && role.isISPAccountManager()
                && RestrictionTools.isPermitted("Context.switch", "ISP")) {
            return "isps";
        }

        // Internal, if CMP account manager
        if (SecurityContext.isInternal() && role.isCMPAccountManager()
                && RestrictionTools.isPermitted("Context.switch", "CMP")) {
            return "cmps";
        }

        // Internal, if permission adOpsDashboard present
        if (SecurityContext.isInternal() && RestrictionTools.isPermitted("AdopsDashboard.run")) {
            return "adOpsDashboard";
        }

        // Advertiser, if permission advertiser_entity present
        if (SecurityContext.isAdvertiser() && RestrictionTools.isPermitted("AdvertiserEntity.view")) {
            return "campaigns";
        }

        // Agency, if permission advertising_account present
        if (SecurityContext.isAgency() && RestrictionTools.isPermitted("AdvertiserEntity.view")) {
            return "agency";
        }

        // Publisher, if permission publisher_entity present
        if (SecurityContext.isPublisher() && RestrictionTools.isPermitted("PublisherEntity.view")) {
            return "sites";
        }

        // ISP
        if (SecurityContext.isIsp()) {
            if (RestrictionTools.isPermitted("Report.run", "ISP") || RestrictionTools.isPermitted("Report.run", "ISPEarnings") || RestrictionTools.isPermitted("Report.run", "webwise")
                    || RestrictionTools.isPermitted("Report.AdvancedISPReports.run", SecurityContext.getPrincipal().getAccountId())) {
                return "reports";
            }
        }

        // CMP, if permission cmp_advertising_channel present
        if (SecurityContext.isCmp() && RestrictionTools.isPermitted("AdvertisingChannel.view")) {
            return "channels";
        }

        // any other
        return "mySettings";
    }
}
