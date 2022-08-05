package com.foros.action.campaign;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.model.campaign.CampaignAllocationTO;
import com.foros.model.campaign.CampaignAllocationsTotalTO;
import com.foros.model.campaign.CampaignCreditAllocationTO;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.Channel;
import com.foros.model.finance.Invoice;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.campaign.CCGStatsTO;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.campaign.CampaignStatsTO;
import com.foros.session.campaign.CtrService;
import com.foros.session.campaignAllocation.CampaignAllocationService;
import com.foros.session.campaignCredit.CampaignCreditAllocationService;
import com.foros.session.campaignCredit.CampaignCreditService;
import com.foros.session.finance.AdvertisingFinanceService;
import com.foros.session.reporting.ReportHelper;
import com.foros.util.DateHelper;
import com.foros.util.PairUtil;
import com.foros.util.StringUtil;
import com.foros.util.context.RequestContexts;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;

import org.joda.time.LocalDate;

public class ViewCampaignAction extends CampaignActionSupport implements RequestContextsAware {
    @EJB
    private AdvertisingFinanceService advertisingFinanceService;

    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    private CampaignAllocationService campaignAllocationService;

    @EJB
    private CampaignCreditAllocationService campaignCreditAllocationService;

    @EJB
    private CampaignCreditService campaignCreditService;

    @EJB
    private CtrService ctrService;

    private Long id;

    private CampaignStatsTO campaignStats;
    private BigDecimal availableBudget;
    private BigDecimal availableCredit;
    private List<CCGStatsTO> groups;
    private Boolean availableCreditUsed;
    private List<Invoice> campaignInvoices;
    private CampaignCreditAllocationTO campaignCreditAllocation;
    private List<CampaignAllocationTO> campaignAllocations;
    private CampaignAllocationsTotalTO campaignAllocationsTotal;
    private BigDecimal calculatedDailyBudget;

    private String fromDateCCGS;
    private String toDateCCGS;
    private String fastChangeIdCCGS = "TOT";

    private String xSelect = "30days";
    private String y1Select = "imps";
    private String y2Select = "clicks";
    private boolean isShowUniqueUsers;
    private boolean isNoFreqCapsWarning;
    private boolean isShowPostImpConv;
    private boolean isShowPostClickConv;


    public boolean isShowPostImpConv() {
        return isShowPostImpConv;
    }

    public boolean isShowPostClickConv() {
        return isShowPostClickConv;
    }

    @ReadOnly
    public String view() {
        campaign = campaignService.view(id);
        setDeliverySchedule(!getScheduleSet().isEmpty());
        setNoFreqCapsWarning(campaignService.hasNoFreqCapWarningForCampaign(campaign));
        return SUCCESS;
    }

    @ReadOnly
    public String groupStats() throws Exception {
        campaign = campaignService.view(id);
        return SUCCESS;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromDateCCGS() {
        return fromDateCCGS;
    }

    public void setFromDateCCGS(String fromDateCCGS) {
        this.fromDateCCGS = fromDateCCGS;
    }

    public String getToDateCCGS() {
        return toDateCCGS;
    }

    public void setToDateCCGS(String toDateCCGS) {
        this.toDateCCGS = toDateCCGS;
    }

    public String getFastChangeIdCCGS() {
        return fastChangeIdCCGS;
    }

    public void setFastChangeIdCCGS(String fastChangeIdCCGS) {
        this.fastChangeIdCCGS = fastChangeIdCCGS;
    }

    public String getXSelect() {
        return xSelect;
    }

    public void setXSelect(String xSelect) {
        this.xSelect = xSelect;
    }

    public String getY1Select() {
        return y1Select;
    }

    public void setY1Select(String y1Select) {
        this.y1Select = y1Select;
    }

    public String getY2Select() {
        return y2Select;
    }

    public void setY2Select(String y2Select) {
        this.y2Select = y2Select;
    }

    public CampaignStatsTO getCampaignStats() {
        if (campaignStats != null) {
            return campaignStats;
        }

        campaignStats = campaignService.getStats(id);

        return campaignStats;
    }

    public BigDecimal getAvailableBudget() {
        if (availableBudget == null) {
            availableBudget = getCampaignStats().getAvailableBudget();
        }

        return availableBudget;
    }

    public BigDecimal getAvailableCredit() {
        if (availableCredit != null) {
            return availableCredit;
        }

        AdvertisingAccountBase account = campaign.getAccount();
        if (!account.isStandalone() && account.getAccountType().isAgencyFinancialFieldsFlag()) {
            account = ((AdvertiserAccount) account).getAgency();
        }

        // at this step account.isFinancialFieldsPresent() must be true, if not - lets investigate (pgdb exception will be thrown)
        availableCredit = advertisingFinanceService.getCreditBalance(account.getId());

        return availableCredit;
    }

    public List<CCGStatsTO> getGroups() throws Exception {
        if (groups != null) {
            return groups;
        }

        return groups = loadCCGStats();
    }

    public boolean isAvailableCreditUsed() throws Exception {
        if (availableCreditUsed == null) {
            availableCreditUsed = false;
            for (CCGStatsTO to : getGroups()) {
                if (to.getCreditUsed() != null && to.getCreditUsed().compareTo(BigDecimal.ZERO) != 0) {
                    availableCreditUsed = true;
                    break;
                }
            }
        }
        return availableCreditUsed;
    }

    private List<CCGStatsTO> loadCCGStats() throws ParseException {
        LocalDate fromDateDisplay;
        LocalDate toDateDisplay;

        Locale locale = CurrentUserSettingsHolder.getLocale();

        if (StringUtil.isPropertyNotEmpty(fastChangeIdCCGS) && !"TOT".equals(fastChangeIdCCGS)) {
            // date parameters are already in account time zone
            fromDateDisplay = DateHelper.parseLocalDate(fromDateCCGS, locale);
            toDateDisplay = DateHelper.parseLocalDate(toDateCCGS, locale);
            isShowUniqueUsers = ReportHelper.isLessThanMonth(fromDateDisplay.toDateTimeAtStartOfDay(), toDateDisplay.toDateTimeAtStartOfDay());
        } else {
            fromDateDisplay = null;
            toDateDisplay = null;
            isShowUniqueUsers = true;
        }

        CampaignService.CCGStatsTOList result = campaignService.getCCGStats(campaign.getId(), fromDateDisplay, toDateDisplay);
        isShowPostClickConv = result.isShowPostClickConv();
        isShowPostImpConv = result.isShowPostImpConv();
        return result;
    }

    public boolean isShowUniqueUsers() {
        return isShowUniqueUsers;
    }

    public boolean isHasChannelTargetedGroup() throws Exception {
        if( getGroups() == null) {
            return false;
        }

        for (CCGStatsTO ccgTO : getGroups()) {
            if (ccgTO.getTgtType() == TGTType.CHANNEL) {
                return true;
            }
        }
        return false;
    }

    public String getAgencyPair() {
        if (campaign.getAccount().isInAgencyAdvertiser()) {
            AgencyAccount agency = campaign.getAccount().getAgency();

            return PairUtil.createAsString(agency.getId(), agency.getName());
        }
        return null;
    }

    @Deprecated
    /**
     * @deprecated OUI-28825
     */
    public List<Invoice> getCampaignInvoices() {
        if (campaignInvoices != null) {
            return campaignInvoices;
        }

        campaignInvoices = advertisingFinanceService.findInvoicesByCampaign(id);

        return campaignInvoices;
    }

    public CampaignCreditAllocationTO getCampaignCreditAllocation() {
        if (campaignCreditAllocation == null) {
            campaignCreditAllocation = campaignCreditAllocationService.findCreditAllocationForCampaign(id);
        }

        return campaignCreditAllocation;
    }

    public List<CampaignAllocationTO> getCampaignAllocations() {
        if (campaignAllocations == null) {
            campaignAllocations = getCampaignAllocationsTotal().getAllocations();
        }
        return campaignAllocations;
    }

    public CampaignAllocationsTotalTO getCampaignAllocationsTotal() {
        if (campaignAllocationsTotal == null) {
            campaignAllocationsTotal = campaignAllocationService.getCampaignAllocationsTotal(id);
        }
        return campaignAllocationsTotal;
    }

    public BigDecimal getCalculatedDailyBudget() {
        if (calculatedDailyBudget != null) {
            return calculatedDailyBudget;
        }

        calculatedDailyBudget = campaignService.calculateDailyBudget(id);
        return calculatedDailyBudget;
    }

    public boolean isNoFreqCapsWarning() {
        return isNoFreqCapsWarning;
    }

    public void setNoFreqCapsWarning(boolean isNoFreqCapsWarning) {
        this.isNoFreqCapsWarning = isNoFreqCapsWarning;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        Long accountId = campaign.getAccount().getId();
        contexts.getAdvertiserContext().switchTo(accountId);
    }

    public Collection<Channel> getExcludedChannels() {
        return campaignService.getExcludedChannels(campaign.getId());
    }
}
