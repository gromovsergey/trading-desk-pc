package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.model.campaign.TGTType;
import com.foros.session.EntityTO;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.util.EntityUtils;
import com.foros.util.PairUtil;
import com.foros.util.StringUtil;

import java.util.Collection;
import java.util.Collections;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;

public class GroupsXmlAction extends AbstractOptionsAction<EntityTO> {
    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    private String campaignPair;
    private String targetType;

    public GroupsXmlAction() {
        super(new NamedTOConverter(true), new OptionStatusFilter(true));
    }

    @RequiredFieldValidator(key = "errors.required", message = "campaignPair")
    @CustomValidator(type = "pair", key = "errors.pair", message = "campaignPair")
    public String getCampaignPair() {
        return campaignPair;
    }

    public void setCampaignPair(String campaignPair) {
        this.campaignPair = campaignPair;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    @Override
    public NamedTOConverter getConverter() {
        return (NamedTOConverter) super.getConverter();
    }

    public void setConcatResultForValue(boolean concatResultForValue) {
        getConverter().setConcatForValue(concatResultForValue);
    }

    @Override
    protected Collection<? extends EntityTO> getOptions() throws ProcessException {
        if (StringUtil.isPropertyNotEmpty(getCampaignPair())) {
            Long id = PairUtil.fetchId(getCampaignPair());
            Collection<EntityTO> groups;
            if (StringUtil.isPropertyNotEmpty(getTargetType())) {
                TGTType tgtType = TGTType.valueOfString(getTargetType());
                groups = campaignCreativeGroupService.getIndexByTargetType(id, tgtType);
            } else {
                groups = campaignCreativeGroupService.getIndex(id);
            }

            return EntityUtils.sortByStatus(groups);
        }

        return Collections.emptyList();
    }
}
