package com.foros.action.creative;

import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.ApplicationFormat;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionType;
import com.foros.model.template.TemplateFile;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.ServiceLocator;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.site.creativeApproval.CreativeSiteApprovalTO;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalService;
import com.foros.session.template.HtmlOptionHelper;
import com.foros.session.template.OptionGroupStateHelper;
import com.foros.util.CollectionUtils;
import com.foros.util.DateHelper;
import com.foros.util.StringUtil;
import com.foros.util.UrlUtil;
import com.foros.util.bean.Filter;
import com.foros.util.context.RequestContexts;
import com.foros.util.preview.PreviewHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.ejb.EJB;

public class ViewCreativeAction extends CreativeActionSupport implements RequestContextsAware {

    private Long id;

    private Collection<Option> options;

    private Boolean optionsDisplayed;

    private List<CampaignCreative> campaignCreativesList;

    private Map<Long, Integer> campaignRowspans;

    private Map<Long, Integer> groupRowspans;

    private SiteCreativeApprovalService.CreativeSiteApprovals siteApprovals;

    private Set<CreativeSize> previewTagSizes;

    private Boolean isShowSecureAdServing;

    private Boolean isSecureAdServing;

    @EJB
    protected CampaignCreativeService campaignCreativeService;

    @EJB
    private ConfigService configService;

    @ReadOnly
    public String view() {
        creative = displayCreativeService.view(id);
        options = PreviewHelper.getOptionsList(creative.getTemplate(), creative.getSize(), OptionGroupType.Advertiser);
        CollectionUtils.filter(options, new Filter<Option>() {
            @Override
            public boolean accept(Option option) {
                CreativeOptGroupState groupState = getGroupStateValues().get(option.getOptionGroup().getId());
                return OptionGroupStateHelper.isGroupEnabled(option.getOptionGroup(), groupState);
            }
        });

        if (isInternal()) {
            siteApprovals = siteCreativeApprovalService.sitesByCreative(id);
        }

        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getOptionsDisplayed() throws Exception {
        if (optionsDisplayed == null) {
            optionsDisplayed = false;
            for (Option option : getOptions()) {
                CreativeOptionValue cov = getOptionValues().get(option.getId());
                if (cov != null) {
                    if (StringUtil.isPropertyNotEmpty(cov.getValue())) {
                        optionsDisplayed = true;
                        break;
                    }
                } else if (StringUtil.isPropertyNotEmpty(option.getDefaultValue())) {
                    optionsDisplayed = true;
                    break;
                }
            }
        }
        return optionsDisplayed;
    }

    public Collection<Option> getOptions() {
        return options;
    }

    public Collection<CampaignCreative> getCampaignCreativesList() {
        if (campaignCreativesList == null) {
            campaignCreativesList = new LinkedList<CampaignCreative>(displayCreativeService.findCampaignCreatives(creative.getId()));
            Collections.sort(campaignCreativesList, new Comparator<CampaignCreative>() {
                @Override
                public int compare(CampaignCreative o1, CampaignCreative o2) {
                    Long id1 = o1.getCreativeGroup().getCampaign().getId();
                    Long id2 = o2.getCreativeGroup().getCampaign().getId();

                    int campaign = id1.compareTo(id2);
                    if (campaign == 0) {
                        return o1.getCreativeGroup().getId().compareTo(o2.getCreativeGroup().getId());
                    }

                    return campaign;
                }
            });

        }
        return campaignCreativesList;
    }

    public Map<Long, Integer> getCampaignRowspans() {
        initRowspans();
        return campaignRowspans;
    }

    public Map<Long, Integer> getGroupRowspans() {
        initRowspans();
        return groupRowspans;
    }

    private void initRowspans() {
        if (campaignRowspans == null) {
            campaignRowspans = new HashMap<Long, Integer>();
            groupRowspans = new HashMap<Long, Integer>();
            for (CampaignCreative cc : getCampaignCreativesList()) {
                Long campaignId = cc.getCreativeGroup().getCampaign().getId();
                if (campaignRowspans.containsKey(campaignId)) {
                    campaignRowspans.put(campaignId, campaignRowspans.get(campaignId) + 1);
                } else {
                    campaignRowspans.put(campaignId, 1);
                }

                Long creativeGroupId = cc.getCreativeGroup().getId();
                if (groupRowspans.containsKey(creativeGroupId)) {
                    groupRowspans.put(creativeGroupId, groupRowspans.get(creativeGroupId) + 1);
                } else {
                    groupRowspans.put(creativeGroupId, 1);
                }
            }
        }
    }

    public String getDateInfoForCC(CampaignCreative cc) {
        Date startDate = cc.getCreativeGroup().getCalculatedStartDate();

        TimeZone timeZone = CurrentUserSettingsHolder.getTimeZone();
        Locale locale = CurrentUserSettingsHolder.getLocale();
        String formattedStartDate = DateHelper.formatDateTime(startDate, timeZone, locale);
        Date endDate = cc.getCreativeGroup().getCalculatedEndDate();

        if (endDate != null) {
            String formattedEndDate = DateHelper.formatDateTime(endDate, timeZone, locale);
            return formattedStartDate + " - " + formattedEndDate;
        } else {
            return StringUtil.getLocalizedString("campaign.dates.start") + ": " + formattedStartDate;
        }
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(creative.getAccount());
    }

    public String fileUrl(CreativeOptionValue cov) {
        Config config = ServiceLocator.getInstance().lookup(ConfigService.class).detach();
        return UrlUtil.formatFileUrl(cov.getValue(), config);
    }

    public String enumName(Option option, String value) {
        if (option.getType() != OptionType.ENUM) {
            return null;
        }

        for (OptionEnumValue enumValue : option.getValues()) {
            if (enumValue.getValue().equals(value)) {
                return enumValue.getName();
            }
        }
        return null;
    }

    public List<CreativeSiteApprovalTO> getSiteApprovals() {
        return siteApprovals.asList();
    }

    public boolean getHasRejected() {
        return siteApprovals.hasRejected();
    }

    public Set<CreativeSize> getPreviewTagSizes() {
        if (previewTagSizes == null) {
            previewTagSizes = loadPreviewTagSizes();
        }
        return previewTagSizes;
    }

    private Set<CreativeSize> loadPreviewTagSizes() {
        Set<CreativeSize> tagSizes = campaignCreativeService.getEffectiveTagSizes(creative);

        CollectionUtils.filter(tagSizes, new Filter<CreativeSize>() {
            @Override
            public boolean accept(CreativeSize size) {
                for (TemplateFile file : creative.getTemplate().getTemplateFiles()) {
                    if (file.getApplicationFormat().getName().equals(ApplicationFormat.PREVIEW_FORMAT)
                            && file.getCreativeSize().getId().equals(size.getId())) {
                        return true;
                    }
                }
                return false;
            }
        });
        return tagSizes;
    }

    public boolean isShowAdvertiserTnsBrand() {
        return configService.get(ConfigParameters.YANDEX_TEMPLATE_NAMES).contains(creative.getTemplate().getDefaultName()) && creative.getAccount().getTnsBrand() != null;
    }

    public boolean isShowSecureAdServing() {
        if (isShowSecureAdServing == null) {
            for (Option option : creative.getTemplate().getHiddenOptions()) {
                if (HtmlOptionHelper.HTTPS_SAFE_TOKEN.equals(option.getToken())) {
                    isShowSecureAdServing = true;
                    break;
                }
            }

            if (isShowSecureAdServing == null) {
                isShowSecureAdServing = false;
            }
        }
        return isShowSecureAdServing;
    }

    public boolean isSecureAdServing() {
        if (isSecureAdServing == null) {
            for (CreativeOptionValue optionValue : creative.getOptions()) {
                if (HtmlOptionHelper.HTTPS_SAFE_TOKEN.equals(optionValue.getOption().getToken())) {
                    isSecureAdServing = true;
                    break;
                }
            }

            if (isSecureAdServing == null) {
                isSecureAdServing = false;
            }
        }
        return isSecureAdServing;
    }

}
