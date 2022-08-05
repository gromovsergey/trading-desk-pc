package com.foros.action.channel;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.campaign.CampaignAssociationTO;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.DateHelper;

import java.util.Collection;
import java.util.Locale;

import javax.ejb.EJB;

import org.joda.time.LocalDate;

public class ChannelCampaignAssociationsAction extends BaseActionSupport {

    @EJB
    protected SearchChannelService searchChannelService;

    private Long id;
    private String fromDateCCGS;
    private String toDateCCGS;

    private Collection<CampaignAssociationTO> campaignAssociations;

    @ReadOnly
    public String loadCampaignAssociations() {
        LocalDate fromDateDisplay = null;
        LocalDate toDateDisplay = null;

        if (fromDateCCGS != null && toDateCCGS != null) {
            Locale locale = CurrentUserSettingsHolder.getLocale();
            fromDateDisplay = DateHelper.parseLocalDate(fromDateCCGS, locale);
            toDateDisplay = DateHelper.parseLocalDate(toDateCCGS, locale);
        } else {
            fromDateDisplay = DateHelper.yesterday(CurrentUserSettingsHolder.getTimeZone());
            toDateDisplay = fromDateDisplay;
        }

        campaignAssociations = searchChannelService.findCampaignAssociations(id, fromDateDisplay, toDateDisplay);

        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFromDateCCGS(String fromDateCCGS) {
        this.fromDateCCGS = fromDateCCGS;
    }

    public void setToDateCCGS(String toDateCCGS) {
        this.toDateCCGS = toDateCCGS;
    }

    public Collection<CampaignAssociationTO> getCampaignAssociations() {
        return campaignAssociations;
    }
}
