package com.foros.action.security.auditLog;

import com.foros.action.BaseActionSupport;
import com.foros.action.IdNameBean;
import com.foros.action.IdNameForm;
import com.foros.action.account.InternalAccountBreadcrumbsElement;
import com.foros.action.account.InternalAccountsBreadcrumbsElement;
import com.foros.action.action.ActionBreadcrumbsElement;
import com.foros.action.admin.accountType.AccountTypeBreadcrumbsElement;
import com.foros.action.admin.accountType.AccountTypesBreadcrumbsElement;
import com.foros.action.admin.bannedChannel.NoAdvChannelBreadcrumbsElement;
import com.foros.action.admin.bannedChannel.NoTrackChannelBreadcrumbsElement;
import com.foros.action.admin.country.CountriesBreadcrumbsElement;
import com.foros.action.admin.country.CountryBreadcrumbsElement;
import com.foros.action.admin.country.ctra.CTRAlgorithmBreadcrumbsElement;
import com.foros.action.admin.country.placementsBlacklist.EntityPlacementsBlacklistBreadcrumbsElement;
import com.foros.action.admin.creativeCategories.CreativeCategoriesBreadcrumbsElement;
import com.foros.action.admin.creativeSize.CreativeSizeBreadcrumbsElement;
import com.foros.action.admin.creativeSize.CreativeSizesBreadcrumbsElement;
import com.foros.action.admin.creativeSize.SizeTypeBreadcrumbsElement;
import com.foros.action.admin.creativeSize.SizeTypesBreadcrumbsElement;
import com.foros.action.admin.currencyExchange.CurrencyExchangesBreadcrumbsElement;
import com.foros.action.admin.fraudConditions.FraudConditionBreadcrumbsElement;
import com.foros.action.admin.searchEngine.SearchEngineBreadcrumbsElement;
import com.foros.action.admin.searchEngine.SearchEnginesBreadcrumbsElement;
import com.foros.action.admin.template.creative.CreativeTemplateBreadcrumbsElement;
import com.foros.action.admin.template.creative.CreativeTemplatesBreadcrumbsElement;
import com.foros.action.admin.template.discover.DiscoverTemplateBreadcrumbsElement;
import com.foros.action.admin.template.discover.DiscoverTemplatesBreadcrumbsElement;
import com.foros.action.admin.userRole.UserRoleBreadcrumbsElement;
import com.foros.action.admin.userRole.UserRolesBreadcrumbsElement;
import com.foros.action.admin.walledGarden.WalledGardenBreadcrumbsElement;
import com.foros.action.admin.wdFrequencyCaps.WDFrequencyCapsBreadcrumbsElement;
import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.action.campaign.campaignCredit.CampaignCreditBreadcrumbsElement;
import com.foros.action.campaign.campaignCredit.ManageCampaignCreditBreadcrumbsElement;
import com.foros.action.campaign.campaignGroup.CampaignGroupBreadcrumbsElement;
import com.foros.action.channel.ChannelBreadcrumbsElement;
import com.foros.action.colocation.ColocationBreadcrumbsElement;
import com.foros.action.creative.display.CreativeBreadcrumbsElement;
import com.foros.action.opportunity.OpportunityBreadcrumbsElement;
import com.foros.action.site.SiteBreadcrumbsElement;
import com.foros.action.site.TagBreadcrumbsElement;
import com.foros.action.site.WDTagBreadcrumbsElement;
import com.foros.action.user.ExternalUserBreadcrumbsElement;
import com.foros.action.user.InternalUserBreadcrumbsElement;
import com.foros.action.user.InternalUserStandaloneBreadcrumbsElement;
import com.foros.action.user.InternalUsersBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.SimpleTextBreadcrumbsElement;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.AuditLogRecord;
import com.foros.model.Country;
import com.foros.model.EntityBase;
import com.foros.model.VersionEntityBase;
import com.foros.model.account.Account;
import com.foros.model.account.InternalAccount;
import com.foros.model.action.Action;
import com.foros.model.admin.SearchEngine;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.channel.Channel;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.ctra.CTRAlgorithmData;
import com.foros.model.isp.Colocation;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.security.AccountType;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.model.security.OwnedEntity;
import com.foros.model.security.User;
import com.foros.model.security.UserRole;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.site.WDTag;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Template;
import com.foros.restriction.RestrictionService;
import com.foros.security.AccountRole;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.security.auditLog.SearchAuditService;
import com.foros.util.BeanUtils;
import com.foros.util.context.Contexts;
import com.foros.util.jpa.DetachedList;

import java.text.NumberFormat;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ModelDriven;

public class ViewAuditLogAction extends BaseActionSupport implements ModelDriven<AuditLogForm>, ServletRequestAware, BreadcrumbsSupport {
    private static final int MAX_AUDITLOG_ROWS = 100;
    private static final int MAX_AUDITLOG_ROWS_CURRENCY_EXCHANGE = 10;
    private static final int MAX_AUDITLOG_ROWS_NO_ADVERTISING_CHANNEL = 5;
    private static final int MAX_AUDITLOG_ROWS_NO_TRACKING_CHANNEL = 5;

    @EJB
    private SearchAuditService service;

    @EJB
    private RestrictionService restrictionService;

    private HttpServletRequest request;
    private AuditLogForm form = new AuditLogForm();
    private String name;

    @Override
    public void setServletRequest(HttpServletRequest httpservletrequest) {
        this.request = httpservletrequest;
    }

    public String getName() {
        return name;
    }

    @ReadOnly
    public String view() throws Exception {
        if (form.getId() != null) {
            ObjectType objectType = form.getObjectType();
            EntityBase entity = objectType.isEntity() ? service.findEntity(objectType, form.getId()) : null;

            setBreadcrumbsInfo(objectType, entity);
            setAccountContext(entity);

            name = service.getObjectName(objectType, form.getId());
        }

        return SUCCESS;
    }

    @ReadOnly
    public String viewRecords() {
        ObjectType objectType = form.getObjectType();
        ActionType actionType = form.getActionType();

        DetachedList<AuditLogRecord> logRecords =
                service.getHistory(objectType, actionType, form.getId(), form.getFirstResultCount(), form.getPageSize());

        form.setLogRecords(logRecords);
        form.setTotal((long)logRecords.getTotal());

        return SUCCESS;
    }

    private void setBreadcrumbsInfo(ObjectType objectType, EntityBase entity) throws Exception {
        if (objectType.isEntity()) {
            setBreadcrumbsInfo(entity);
        } else if (ObjectType.PlacementsBlacklist == objectType) {
            setCountryEntity((Country)service.findEntity(ObjectType.Country, form.getId()));
        }
    }

    private void setBreadcrumbsInfo(EntityBase entity) throws Exception {
        NumberFormat nf = CurrentUserSettingsHolder.getNumberFormat();

        if (entity instanceof CampaignCreativeGroup) {
            CampaignCreativeGroup group = (CampaignCreativeGroup) entity;
            CampaignCreativeGroupForm groupForm = new CampaignCreativeGroupForm();
            groupForm.setId(group.getId() == null ? "" : group.getId().toString());
            groupForm.setName(group.getName());
            IdNameForm<String> campaignForm = new IdNameForm<String>();
            BeanUtils.copyProperties(campaignForm, group.getCampaign(), nf);
            groupForm.setCampaign(campaignForm);
            groupForm.setCcgType(group.getCcgType().toString());
            form.setGroupForm(groupForm);
        }

        if (entity instanceof Channel) {
            final Channel channel = (Channel) entity;
            final IdNameForm channelForm = new IdNameForm() {
                public String getChannelType() {
                    return channel.getChannelType();
                }
            };

            BeanUtils.copyProperties(channelForm, channel, nf);
            form.setChannelForm(channelForm);
        }

        if (entity instanceof Country) {
            setCountryEntity((Country)entity);
        }

        if (entity instanceof CTRAlgorithmData) {
            IdNameBean countryBean = new IdNameBean();
            countryBean.setId(((CTRAlgorithmData) entity).getCountryCode());
            form.setCountryEntity(countryBean);
        }

        if (entity instanceof VersionEntityBase) {
            form.setVersion(((VersionEntityBase) entity).getVersion());
        }

        request.setAttribute("entityBean", entity);
    }

    private void setAccountContext(EntityBase entity) {
        if (entity instanceof OwnedEntity<?>) {
            Account account = ((OwnedEntity<?>) entity).getAccount();
            if ((account != null) && (restrictionService.isPermitted("Context.switch", account))) {
                Contexts.getContexts(request).switchTo(account);
            }
        }
    }

    public int getMaxRows() {
        switch (form.getObjectType()) {
            case CurrencyExchange:
                return MAX_AUDITLOG_ROWS_CURRENCY_EXCHANGE;
            case NoAdvertisingChannel:
                return MAX_AUDITLOG_ROWS_NO_ADVERTISING_CHANNEL;
            case NoTrackingChannel:
                return MAX_AUDITLOG_ROWS_NO_TRACKING_CHANNEL;
            default:
                return MAX_AUDITLOG_ROWS;
        }
    }

    @Override
    public AuditLogForm getModel() {
        return form;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        ObjectType objectType = form.getObjectType();
        EntityBase entity = null;
        if (objectType.isEntity()) {
            entity = (form.getId() != null) ? service.findEntity(objectType, form.getId()) : null;
        }
        if (ObjectType.PlacementsBlacklist == objectType) {
            entity = (form.getId() != null) ? service.findEntity(ObjectType.Country, form.getId()) : null;
        }

        Breadcrumbs breadcrumbs;
        switch (objectType) {
            case CurrencyExchange: {
                breadcrumbs = new Breadcrumbs().add(new CurrencyExchangesBreadcrumbsElement());
                break;
            }
            case Colocation: {
                breadcrumbs = new Breadcrumbs().add(new ColocationBreadcrumbsElement((Colocation) entity));
                break;
            }
            case WalledGarden: {
                breadcrumbs = new Breadcrumbs().add(new WalledGardenBreadcrumbsElement());
                break;
            }
            case Campaign: {
                Campaign campaign = (Campaign) entity;
                breadcrumbs = new Breadcrumbs().add(new CampaignBreadcrumbsElement(campaign));
                break;
            }
            case WDTag: {
                WDTag tag = (WDTag) entity;
                breadcrumbs = new Breadcrumbs().add(new SiteBreadcrumbsElement(tag.getSite())).add(new WDTagBreadcrumbsElement(tag));
                break;
            }
            case Site: {
                Site site = (Site) entity;
                breadcrumbs = new Breadcrumbs().add(new SiteBreadcrumbsElement(site));
                break;
            }
            case InternalAccount: {
                InternalAccount ia = (InternalAccount) entity;
                breadcrumbs = new Breadcrumbs().add(new InternalAccountsBreadcrumbsElement()).add(new InternalAccountBreadcrumbsElement(ia));
                break;
            }
            case CampaignCredit: {
                CampaignCredit cc = (CampaignCredit) entity;
                breadcrumbs = new Breadcrumbs().add(new ManageCampaignCreditBreadcrumbsElement(cc));
                break;
            }
            case Action: {
                Action action = (Action) entity;
                breadcrumbs = new Breadcrumbs().add(new ActionBreadcrumbsElement(action));
                break;
            }
            case CampaignCreativeGroup: {
                CampaignCreativeGroup group = (CampaignCreativeGroup) entity;
                breadcrumbs = new Breadcrumbs().add(new CampaignBreadcrumbsElement(group.getCampaign())).add(new CampaignGroupBreadcrumbsElement(group));
                break;
            }
            case Opportunity: {
                Opportunity o = (Opportunity) entity;
                breadcrumbs = new Breadcrumbs().add(new OpportunityBreadcrumbsElement(o));
                break;
            }
            case Tag: {
                Tag tag = (Tag) entity;
                breadcrumbs = new Breadcrumbs().add(new SiteBreadcrumbsElement(tag.getSite())).add(new TagBreadcrumbsElement(tag));
                break;
            }
            case User: {
                User user = (User) entity;
                breadcrumbs = createUserBreadcrumbs(user);
                break;
            }
            case AccountType: {
                breadcrumbs = new Breadcrumbs().add(new AccountTypesBreadcrumbsElement()).add(new AccountTypeBreadcrumbsElement((AccountType) entity));
                break;
            }
            case Country: {
                breadcrumbs = new Breadcrumbs().add(new CountriesBreadcrumbsElement()).add(new CountryBreadcrumbsElement((Country) entity));
                break;
            }
            case CTRAlgorithmData: {
                CTRAlgorithmData data = (CTRAlgorithmData) entity;
                breadcrumbs = new Breadcrumbs().add(new CountriesBreadcrumbsElement()).add(new CountryBreadcrumbsElement(data.getCountry())).add(new CTRAlgorithmBreadcrumbsElement(data.getCountry()));
                break;
            }
            case Creative: {
                Creative creative = (Creative) entity;
                breadcrumbs = createCreativeBreadcrumbs(creative);
                break;
            }
            case CreativeTemplate: {
                CreativeTemplate creativeTemplate = (CreativeTemplate) entity;
                breadcrumbs = new Breadcrumbs().add(new CreativeTemplatesBreadcrumbsElement()).add(new CreativeTemplateBreadcrumbsElement(creativeTemplate));
                break;
            }
            case SizeType: {
                breadcrumbs = new Breadcrumbs().add(new SizeTypesBreadcrumbsElement()).add(new SizeTypeBreadcrumbsElement((SizeType) entity));
                break;
            }
            case CreativeSize: {
                breadcrumbs = new Breadcrumbs().add(new CreativeSizesBreadcrumbsElement()).add(new CreativeSizeBreadcrumbsElement((CreativeSize) entity));
                break;
            }
            case DiscoverTemplate: {
                breadcrumbs = new Breadcrumbs().add(new DiscoverTemplatesBreadcrumbsElement()).add(new DiscoverTemplateBreadcrumbsElement((Template) entity));
                break;
            }
            case CreativeCategory: {
                breadcrumbs = new Breadcrumbs().add(new CreativeCategoriesBreadcrumbsElement());
                break;
            }
            case NoTrackingChannel: {
                breadcrumbs = new Breadcrumbs().add(new NoTrackChannelBreadcrumbsElement());
                break;
            }
            case NoAdvertisingChannel: {
                breadcrumbs = new Breadcrumbs().add(new NoAdvChannelBreadcrumbsElement());
                break;
            }
            case PlacementsBlacklist: {
                Country country = (Country)entity;
                breadcrumbs = new Breadcrumbs()
                        .add(new CountriesBreadcrumbsElement())
                        .add(new CountryBreadcrumbsElement(country))
                        .add(new EntityPlacementsBlacklistBreadcrumbsElement(country));
                break;
            }
            case SearchEngine: {
                breadcrumbs = new Breadcrumbs().add(new SearchEnginesBreadcrumbsElement()).add(new SearchEngineBreadcrumbsElement((SearchEngine) entity));
                break;
            }
            case FraudCondition: {
                breadcrumbs = new Breadcrumbs().add(new FraudConditionBreadcrumbsElement());
                break;
            }
            case WDFrequencyCap: {
                breadcrumbs = new Breadcrumbs().add(new WDFrequencyCapsBreadcrumbsElement());
                break;
            }
            case UserRole: {
                breadcrumbs = new Breadcrumbs().add(new UserRolesBreadcrumbsElement()).add(new UserRoleBreadcrumbsElement((UserRole) entity));
                break;
            }
            case CategoryChannel:
            case DeviceChannel:
            case KeywordChannel:
            case DiscoverChannel:
            case DiscoverChannelList:
            case BehavioralChannel:
            case ExpressionChannel:
            case AudienceChannel:
            case GeoChannel: {
                breadcrumbs = ChannelBreadcrumbsElement.getChannelBreadcrumbs((Channel) entity);
                break;
            }
            case FileManager:
                breadcrumbs = new Breadcrumbs();
                break;
            default: {
                breadcrumbs = null;
                break;
            }
        }

        if (breadcrumbs != null && !breadcrumbs.isEmpty()) {
            breadcrumbs.add(ActionBreadcrumbs.VIEW_LOG);
        }
        return breadcrumbs;
    }

    private Breadcrumbs createCreativeBreadcrumbs(Creative creative) {
        Breadcrumbs breadcrumbs;
        breadcrumbs = new Breadcrumbs().add(new CreativeBreadcrumbsElement(creative));
        return breadcrumbs;
    }

    private Breadcrumbs createUserBreadcrumbs(User user) {
        Breadcrumbs breadcrumbs;
        if (user.getAccount().getRole() == AccountRole.INTERNAL) {
            if (request.getParameter("internalUserPage").equals("true")) {
                breadcrumbs = new Breadcrumbs()
                        .add(new InternalUsersBreadcrumbsElement())
                        .add(new InternalUserStandaloneBreadcrumbsElement(user));
            } else {
                breadcrumbs = new Breadcrumbs()
                        .add(new InternalAccountsBreadcrumbsElement())
                        .add(new InternalAccountBreadcrumbsElement(user.getAccount()))
                        .add(new InternalUserBreadcrumbsElement(user));
            }
        } else {
            breadcrumbs = new Breadcrumbs().add(new ExternalUserBreadcrumbsElement(user));
        }
        return breadcrumbs;
    }

    private void setCountryEntity(Country country) {
        IdNameBean countryBean = new IdNameBean();
        countryBean.setId(country.getCountryCode());
        form.setCountryEntity(countryBean);
    }
}
