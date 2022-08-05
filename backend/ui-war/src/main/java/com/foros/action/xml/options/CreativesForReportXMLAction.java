package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.creative.CreativeService;
import com.foros.util.EntityUtils;

import java.util.Collection;
import javax.ejb.EJB;

public class CreativesForReportXMLAction extends AbstractOptionsAction<EntityTO>{

    @EJB
    private CreativeService creativeService;

    private Long accountId;

    private Long campaignId;

    private Long groupId;

    private String name;

    public CreativesForReportXMLAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(true));
    }

    @Override
    protected Collection<? extends EntityTO> getOptions() throws ProcessException {
        return EntityUtils.sortByStatus(creativeService.findCreativesForReport(accountId, campaignId, groupId, name, AUTOCOMPLETE_SIZE));
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
