package com.foros.action.reporting.audit;

import static com.foros.model.security.ObjectType.AccountType;
import static com.foros.model.security.ObjectType.Action;
import static com.foros.model.security.ObjectType.AdvertiserAccount;
import static com.foros.model.security.ObjectType.AgencyAccount;
import static com.foros.model.security.ObjectType.BehavioralChannel;
import static com.foros.model.security.ObjectType.BirtReport;
import static com.foros.model.security.ObjectType.CTRAlgorithmData;
import static com.foros.model.security.ObjectType.Campaign;
import static com.foros.model.security.ObjectType.CampaignCreativeGroup;
import static com.foros.model.security.ObjectType.CampaignCredit;
import static com.foros.model.security.ObjectType.CategoryChannel;
import static com.foros.model.security.ObjectType.CmpAccount;
import static com.foros.model.security.ObjectType.Colocation;
import static com.foros.model.security.ObjectType.Country;
import static com.foros.model.security.ObjectType.Creative;
import static com.foros.model.security.ObjectType.CreativeCategory;
import static com.foros.model.security.ObjectType.CreativeSize;
import static com.foros.model.security.ObjectType.CreativeTemplate;
import static com.foros.model.security.ObjectType.CurrencyExchange;
import static com.foros.model.security.ObjectType.DiscoverChannel;
import static com.foros.model.security.ObjectType.DiscoverTemplate;
import static com.foros.model.security.ObjectType.ExpressionChannel;
import static com.foros.model.security.ObjectType.FileManager;
import static com.foros.model.security.ObjectType.FraudCondition;
import static com.foros.model.security.ObjectType.GeoChannel;
import static com.foros.model.security.ObjectType.InternalAccount;
import static com.foros.model.security.ObjectType.IspAccount;
import static com.foros.model.security.ObjectType.KeywordChannel;
import static com.foros.model.security.ObjectType.NoAdvertisingChannel;
import static com.foros.model.security.ObjectType.NoTrackingChannel;
import static com.foros.model.security.ObjectType.Opportunity;
import static com.foros.model.security.ObjectType.PlacementsBlacklist;
import static com.foros.model.security.ObjectType.PredefinedReport;
import static com.foros.model.security.ObjectType.PublisherAccount;
import static com.foros.model.security.ObjectType.SearchEngine;
import static com.foros.model.security.ObjectType.Site;
import static com.foros.model.security.ObjectType.Tag;
import static com.foros.model.security.ObjectType.User;
import static com.foros.model.security.ObjectType.UserRole;
import static com.foros.model.security.ObjectType.WDFrequencyCap;
import static com.foros.model.security.ObjectType.WDTag;
import static com.foros.model.security.ObjectType.WalledGarden;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.action.IdNameBean;
import com.foros.framework.ReadOnly;
import com.foros.model.security.ObjectType;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.CurrentUserService;
import com.foros.session.security.AuditLogRecordTO;
import com.foros.session.security.auditLog.AuditReportParameters;
import com.foros.session.security.auditLog.SearchAuditService;
import com.foros.util.DateHelper;
import com.foros.util.StringUtil;
import com.foros.util.jpa.DetachedList;

public class AuditLogReportAction extends BaseActionSupport implements ModelDriven<AuditReportParameters>,ServletRequestAware {
    private static final String DATE_PROPERTY = "date";
    private static final String TODAY = "T";
    protected HttpServletRequest request;

    @EJB
    private SearchAuditService service;

    @EJB
    private CurrentUserService currentUserService;

    private DateTime fromDate;
    private DateTime toDate;
    private String fastChangeId = TODAY;

    private DetachedList<AuditLogRecordTO> logRecords;
    private AuditReportParameters reportForm = new AuditReportParameters();

    public DetachedList<AuditLogRecordTO> getLogRecords() {
        return logRecords;
    }

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'audit'")
    public String view() throws Exception {
        return SUCCESS;
    }

    @ReadOnly
    public String run() throws Exception {
        validate();
        if (hasFieldErrors()) {
            return SUCCESS;
        }

        extractDates();
        logRecords = service.searchLogs(getModel());

        return SUCCESS;
    }

    @Override
    public void validate() {
        validateDateTime(fromDate, "report.input.field.dateRange.dateFrom");
        validateDateTime(toDate, "report.input.field.dateRange.dateFrom");
    }

    private void extractDates() {
        Locale locale = CurrentUserSettingsHolder.getLocale();

        getModel().setDateFrom(fromDate.getDate(locale));
        getModel().setDateTo(toDate.getDate(locale));

        if (getModel().getDateTo().isAfter(getModel().getDateTo())) {
            addFieldError(DATE_PROPERTY, getText("report.invalid.range"));
        }
    }

    private void validateDateTime(DateTime date, String propertyNameKey) {
        TimeZone timeZone = CurrentUserSettingsHolder.getTimeZone();
        Locale locale = CurrentUserSettingsHolder.getLocale();

        String propertyName = StringUtil.getLocalizedString(propertyNameKey, locale);
        if (StringUtils.isBlank(date.getDatePart())) {
            addFieldError(DATE_PROPERTY, getText("errors.required.datePart", propertyName));
        }

        if (StringUtils.isBlank(date.getTimePart())) {
            addFieldError(DATE_PROPERTY, getText("errors.required.timePart", propertyName));
        }

        try {
            DateHelper.parseTime(date.getTimePart(), DateFormat.SHORT, timeZone, locale);
        } catch (ParseException e) {
            addFieldError(DATE_PROPERTY, getText("errors.field.time"));
        }

        try {
            DateHelper.parseDate(date.getDatePart(), DateFormat.SHORT, timeZone, locale);
        } catch (ParseException e) {
            addFieldError(DATE_PROPERTY, getText("errors.field.date"));
        }
    }

    public DateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(DateTime fromDate) {
        this.fromDate = fromDate;
    }

    public DateTime getToDate() {
        return toDate;
    }

    public void setToDate(DateTime toDate) {
        this.toDate = toDate;
    }

    public String getFastChangeId() {
        return fastChangeId;
    }

    public void setFastChangeId(String fastChangeId) {
        this.fastChangeId = fastChangeId;
    }

    public String getAccountType() {
        List<Long> accountRoleIds = getModel().getAccountRoleIds();
        if (accountRoleIds == null) {
            return "";
        }

        return StringUtils.join(accountRoleIds, ',');
    }

    public void setAccountType(String accountType) {
        List<Long> ids;
        if (StringUtil.isPropertyEmpty(accountType)) {
            ids = null;
        } else {
            String[] strings = StringUtils.split(accountType, ',');
            ids = new ArrayList<Long>(strings.length);
            for (String string : strings) {
                ids.add(Long.valueOf(string));
            }
        }
        getModel().setAccountRoleIds(ids);
    }

    public int getPageSize() {
        return SearchAuditService.SEARCH_PAGE_SIZE;
    }

    public Collection<IdNameBean> getObjectTypeIndex(){
        Collection<ObjectType> types = new LinkedList<ObjectType>();
        if (isInternal()) {
            types.add(InternalAccount);
        }
        types.add(AdvertiserAccount);
        types.add(AgencyAccount);
        types.add(PublisherAccount);
        types.add(IspAccount);
        types.add(CmpAccount);
        types.add(User);
        types.add(Campaign);
        types.add(CampaignCredit);
        types.add(CampaignCreativeGroup);
        types.add(Creative);
        types.add(Action);
        types.add(Opportunity);
        types.add(Site);
        types.add(Tag);
        types.add(WDTag);
        types.add(Colocation);
        types.add(ExpressionChannel);
        types.add(DiscoverChannel);
        types.add(BehavioralChannel);
        types.add(CategoryChannel);
        types.add(KeywordChannel);
        types.add(GeoChannel);

        types.add(UserRole);
        types.add(CreativeCategory);
        types.add(AccountType);
        types.add(CreativeTemplate);
        types.add(CreativeSize);
        types.add(DiscoverTemplate);
        types.add(Country);
        types.add(CurrencyExchange);
        types.add(FraudCondition);
        types.add(NoTrackingChannel);
        types.add(NoAdvertisingChannel);
        types.add(PlacementsBlacklist);
        types.add(WDFrequencyCap);
        types.add(CTRAlgorithmData);
        types.add(PredefinedReport);
        types.add(BirtReport);
        types.add(SearchEngine);
        types.add(WalledGarden);
        types.add(FileManager);

        Collection<IdNameBean> result = new ArrayList<IdNameBean>(types.size());
        for (ObjectType type : types) {
            result.add(new IdNameBean(String.valueOf(type.getId()), StringUtil.getLocalizedString("report.input.field.objectType." + type.getName())));
        }
        return result;

    }

    @Override
    public void setServletRequest(HttpServletRequest httpservletrequest) {
        this.request = httpservletrequest;
    }

    @Override
    public AuditReportParameters getModel() {
        return reportForm;
    }

    public void setReportForm(AuditReportParameters reportForm) {
        this.reportForm = reportForm;
    }
}
