package com.foros.session.security;

import com.foros.model.AuditLogRecord;
import com.foros.model.EntityBase;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.channel.Channel;
import com.foros.model.isp.Colocation;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.model.security.OwnedEntity;
import com.foros.model.security.OwnedStatusable;
import com.foros.model.security.User;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.session.CurrentUserService;
import com.foros.session.UtilityService;
import com.foros.session.account.AccountRestrictions;
import com.foros.session.admin.FileManagerRestrictions;
import com.foros.session.admin.WDFrequencyCapsRestrictions;
import com.foros.session.admin.accountType.AccountTypeRestrictions;
import com.foros.session.admin.bannedChannel.BannedChannelRestrictions;
import com.foros.session.admin.country.CountryRestrictions;
import com.foros.session.admin.currencyExchange.CurrencyExchangeRestrictions;
import com.foros.session.admin.fraudConditions.FraudConditionsRestrictions;
import com.foros.session.admin.searchEngine.SearchEngineRestrictions;
import com.foros.session.admin.userRole.UserRoleRestrictions;
import com.foros.session.admin.walledGarden.WalledGardenRestrictions;
import com.foros.session.birt.BirtReportRestrictions;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.campaignAllocation.CampaignAllocationRestrictions;
import com.foros.session.campaignCredit.CampaignCreditRestrictions;
import com.foros.session.channel.geo.GeoChannelRestrictions;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.channel.service.ChannelRestrictions;
import com.foros.session.channel.service.PlacementsBlacklistRestrictions;
import com.foros.session.colocation.ColocationRestrictions;
import com.foros.session.creative.CreativeCategoryRestrictions;
import com.foros.session.creative.CreativeSizeRestrictions;
import com.foros.session.opportunity.OpportunityRestrictions;
import com.foros.session.reporting.ReportRestrictions;
import com.foros.session.reporting.ReportType;
import com.foros.session.site.PublisherEntityRestrictions;
import com.foros.session.template.TemplateRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityNotFoundException;

@LocalBean
@Stateless
@Restrictions
public class AuditLogRestrictions {

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private CampaignAllocationRestrictions campaignAllocationRestrictions;

    @EJB
    private CampaignCreditRestrictions campaignCreditRestrictions;

    @EJB
    private PublisherEntityRestrictions publisherEntityRestrictions;

    @EJB
    private ColocationRestrictions colocationRestrictions;

    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private UserRestrictions userRestrictions;

    @EJB
    private ChannelRestrictions channelRestrictions;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @EJB
    private GeoChannelRestrictions geoChannelRestrictions;

    @EJB
    private CurrencyExchangeRestrictions currencyExchangeRestrictions;

    @EJB
    private UserRoleRestrictions userRoleRestrictions;

    @EJB
    private ReportRestrictions reportRestrictions;

    @EJB
    private BirtReportRestrictions birtReportRestrictions;

    @EJB
    private TemplateRestrictions templateRestrictions;

    @EJB
    private UtilityService utilityService;

    @EJB
    private AccountTypeRestrictions accountTypeRestrictions;

    @EJB
    private CreativeCategoryRestrictions creativeCategoryRestrictions;

    @EJB
    private CreativeSizeRestrictions creativeSizeRestrictions;

    @EJB
    private FraudConditionsRestrictions fraudConditionsRestrictions;

    @EJB
    private CountryRestrictions countryRestrictions;

    @EJB
    private WDFrequencyCapsRestrictions wdFrequencyCapsRestrictions;

    @EJB
    private BannedChannelRestrictions bannedChannelRestrictions;

    @EJB
    private PlacementsBlacklistRestrictions placementsBlacklistRestrictions;

    @EJB
    private OpportunityRestrictions opportunityRestrictions;

    @EJB
    private SearchEngineRestrictions searchEngineRestrictions;

    @EJB
    private WalledGardenRestrictions walledGardenRestrictions;

    @EJB
    private FileManagerRestrictions fileManagerRestrictions;

    @Restriction
    public boolean canView(EntityBase entity) {
        return canView(ObjectType.valueOf(entity.getClass()), null, entity);
    }

    @Restriction
    public boolean canView(ObjectType ot, ActionType at, Long objectId) {
        Object obj;
        if (ot.isEntity() && objectId != null) {
            obj = utilityService.find(ot.getObjectClass(), objectId);
        } else {
            obj = objectId;
        }
        return canView(ot, at, obj);
    }


    private boolean canView(ObjectType ot, ActionType at, Object object) {
        if (!currentUserService.isInternal()) {
            return false;
        }

        if (ot == null && at == null) {
            return false;
        }

        if (ot == null) {
            // non entity related records are only available via audit report
            return at.isObjectless() && reportRestrictions.canRun("audit");
        }

        switch (ot) {
            case AgencyAccount:
            case AdvertiserAccount:
            case IspAccount:
            case InternalAccount:
            case PublisherAccount:
            case CmpAccount:
                return accountRestrictions.canView((Account) object);
            case User:
                return userRestrictions.canView((User) object);
            case Campaign:
            case CampaignCreativeGroup:
            case CampaignCreative:
            case Creative:
            case Action:
                return advertiserEntityRestrictions.canView((OwnedStatusable) object);
            case CampaignCredit:
                return campaignCreditRestrictions.canView((AdvertisingAccountBase) object);
            case KeywordChannel:
            case CategoryChannel:
            case DeviceChannel:
            case DiscoverChannel:
            case DiscoverChannelList:
                return channelRestrictions.canView((Channel) object);
            case BehavioralChannel:
            case AudienceChannel:
            case ExpressionChannel:
                return advertisingChannelRestrictions.canViewContent((Channel) object);
            case GeoChannel:
                return geoChannelRestrictions.canViewLog();
            case Site:
            case Tag:
            case WDTag:
                return publisherEntityRestrictions.canView((OwnedStatusable) object);
            case Colocation:
                return colocationRestrictions.canView((Colocation) object);
            case CurrencyExchange:
                return currencyExchangeRestrictions.canView();
            case UserRole:
                return userRoleRestrictions.canView();
            case AccountType:
                return accountTypeRestrictions.canView();
            case CreativeCategory:
                return creativeCategoryRestrictions.canView();
            case CreativeTemplate:
            case DiscoverTemplate:
                return templateRestrictions.canView();
            case CreativeSize:
            case SizeType:
                return creativeSizeRestrictions.canView();
            case Country:
            case CTRAlgorithmData:
                return countryRestrictions.canView();
            case NoTrackingChannel:
            case NoAdvertisingChannel:
                return bannedChannelRestrictions.canView();
            case PlacementsBlacklist:
                return placementsBlacklistRestrictions.canView();
            case FraudCondition:
                return fraudConditionsRestrictions.canView();
            case WDFrequencyCap:
                return wdFrequencyCapsRestrictions.canView();
            case PredefinedReport:
                return reportRestrictions.canRun(convertToReportName((Long) object));
            case BirtReport:
                return canViewBirtReport(object);
            case Opportunity:
                return opportunityRestrictions.canView((OwnedEntity) object);
            case SearchEngine:
                return searchEngineRestrictions.canView();
            case WalledGarden:
                return walledGardenRestrictions.canView();
            case FileManager:
                return fileManagerRestrictions.canManage() || templateRestrictions.canViewFileManager();

        }

        return false;
    }

    private boolean canViewBirtReport(Object entity) {
        Long reportId = null;
        if (entity instanceof BirtReport) {
            reportId = ((BirtReport) entity).getId();
        } else if (entity instanceof Long) {
            reportId = (Long) entity;
        }
        return reportId != null ? birtReportRestrictions.canGet(reportId) || birtReportRestrictions.canUpdate(reportId) : birtReportRestrictions.canViewAuditLog();
    }

    private String convertToReportName(Long entity) {
        try {
            ReportType reportType = ReportType.byId(entity);
            return reportType.getName();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new EntityNotFoundException("Object Type with id = " + (entity == null ? "null" : entity) + " not found");
        }
    }

    @Restriction
    public boolean canView(AuditLogRecord alr) {
        Object entity = null;
        if (ObjectType.PredefinedReport == alr.getObjectType() || ObjectType.BirtReport == alr.getObjectType() || ObjectType.PlacementsBlacklist == alr.getObjectType()) {
            entity = alr.getObjectId();
        } else if (alr.getObjectId() != null && alr.getObjectType() != null && alr.getObjectType().getObjectClass() != null) {
            entity = utilityService.find((Class<? extends EntityBase>) alr.getObjectType().getObjectClass(), alr.getObjectId());
        }
        return canView(alr.getObjectType(), alr.getActionType(), entity);
    }
}
