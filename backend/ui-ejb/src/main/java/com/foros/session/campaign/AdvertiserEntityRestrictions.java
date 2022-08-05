package com.foros.session.campaign;

import static com.foros.security.AccountRole.ADVERTISER;
import static com.foros.security.AccountRole.AGENCY;
import static com.foros.security.AccountRole.INTERNAL;

import com.foros.model.DisplayStatus;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.model.OwnedApprovable;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.model.campaign.RateType;
import com.foros.model.creative.Creative;
import com.foros.model.security.AccountType;
import com.foros.model.security.NotManagedEntity;
import com.foros.model.security.OwnedStatusable;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.AccountRole;
import com.foros.session.BeanValidations;
import com.foros.session.CurrentUserService;
import com.foros.session.UtilityService;
import com.foros.session.account.AccountService;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.bulk.OperationType;
import com.foros.session.restriction.EntityRestrictions;
import com.foros.validation.ValidationContext;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.util.ValidationUtil;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "advertiser_entity", action = "view", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "advertiser_entity", action = "edit", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "advertiser_entity", action = "create", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "advertiser_entity", action = "undelete", accountRoles = { INTERNAL }),
        @Permission(objectType = "advertiser_entity", action = "approve", accountRoles = { INTERNAL }),
        @Permission(objectType = "advertiser_entity", action = "activate", accountRoles = { INTERNAL }),
        @Permission(objectType = "advertiser_entity", action = "advanced", accountRoles = { INTERNAL }),
        @Permission(objectType = "advertiser_entity", action = "reset_ctr_date", accountRoles = { INTERNAL }),
        @Permission(objectType = "advertiser_entity", action = "log_check", accountRoles = { INTERNAL })
})
public class AdvertiserEntityRestrictions {

    private static final boolean IS_MANAGED = NotManagedEntity.Util.isManaged(
        Campaign.class,
        CampaignCreativeGroup.class,
        Action.class,
        CampaignCreative.class,
        Creative.class,
        CCGKeyword.class
        );

    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private WalledGardenService walledGardenService;

    @EJB
    private UtilityService utilityService;

    @EJB
    private BeanValidations beanValidations;

    @EJB
    private AccountService accountService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Restriction
    public void canMerge(ValidationContext context, Campaign campaign, OperationType operationType) {
        validateCampaignType(context, campaign, operationType);
        validateMerge(context, Campaign.class, campaign, Account.class, campaign.getAccount(), operationType);
    }

    public void canMerge(ValidationContext context, Action action, OperationType operationType) {
        validateMerge(context, Action.class, action, Account.class, action.getAccount(), operationType);
    }

    public void canMerge(ValidationContext context, CampaignCreativeGroup group, OperationType operationType) {
        validateMerge(context, CampaignCreativeGroup.class, group, Campaign.class, group.getCampaign(), operationType);
    }

    public void canMerge(ValidationContext context, CCGKeyword keyword, OperationType operationType) {
        validateMerge(context, CCGKeyword.class, keyword,
            CampaignCreativeGroup.class, keyword.getCreativeGroup(), operationType);
    }

    public void canMerge(ValidationContext context, Creative creative, OperationType operationType) {
        validateMerge(context, Creative.class, creative, Account.class, creative.getAccount(), operationType);
    }

    public void canMerge(ValidationContext context, CampaignCreative cc, OperationType operationType) {
        validateMerge(context, CampaignCreative.class, cc, CampaignCreativeGroup.class, cc.getCreativeGroup(), operationType);
    }

    private <T extends EntityBase & OwnedStatusable & Identifiable, P extends EntityBase & OwnedStatusable & Identifiable>
            void validateMerge(ValidationContext context, Class<T> type, T entity, Class<P> parentType, P parent, OperationType operationType) {
        switch (operationType) {
        case CREATE:
            ValidationContext parentContext = context
                .subContext(parent).withPath(getParentPath(parentType)).build();

            beanValidations.linkValidator(parentContext, parentType).withRequired(true).validate(parent);

            if (parentContext.hasViolations()) {
                return;
            }

            P existingParent = utilityService.safeFind(parentType, parent.getId());

            canCreate(parentContext, existingParent);

            break;
        case UPDATE:
            beanValidations.linkValidator(context, type).validate(entity);

            if (context.hasViolations()) {
                return;
            }

            T existing = utilityService.safeFind(type, entity.getId());

            canPerformUpdate(context, entity, existing);

            break;
        default:
            throw new RuntimeException(operationType + " does not supported!");
        }
    }

    private String getParentPath(Class parentType) {
        char[] charArray = parentType.getSimpleName().toCharArray();
        charArray[0] = Character.toLowerCase(charArray[0]);
        return new String(charArray);
    }

    @Restriction
    public void canCreate(ValidationContext context) {
        validatePermission(context, "create");
    }

    @Restriction
    public void canCreate(ValidationContext context, OwnedStatusable parent) {
        canCreate(context);
        entityRestrictions.canCreate(context, parent, IS_MANAGED);
    }

    @Restriction
    public void canCreateBulk(ValidationContext context, List<Long> ccgIds) {
        if (ccgIds.isEmpty()) {
            return;
        }

        canCreate(context);
        for (Long ccgId : ccgIds) {
            CampaignCreativeGroup creativeGroup = utilityService.find(CampaignCreativeGroup.class, ccgId);
            entityRestrictions.canCreate(context, creativeGroup, IS_MANAGED);
        }
    }

    @Restriction
    public void canPerformUpdate(ValidationContext context, OwnedStatusable entity, OwnedStatusable existing) {
        validatePermission(context, "edit");
        if (context.hasViolations()) {
            return;
        }
        validateUpdateOrUndelete(context, entity, existing);
    }

    private void validateUpdateOrUndelete(ValidationContext context, OwnedStatusable entity, OwnedStatusable existing) {
        if (isUndeleted(entity, existing)) {
            canUndelete(context, existing);
            return;
        }

        entityRestrictions.canUpdate(context, existing);
    }

    private void validatePermission(ValidationContext context, String action) {
        entityRestrictions.validatePermission(context, "advertiser_entity", action);
    }

    private boolean isUndeleted(OwnedStatusable entity, OwnedStatusable existing) {
        Status existingStatus = existing.getStatus();
        Status entityStatus = ((EntityBase) entity).isChanged("status") ? entity.getStatus() : existingStatus;
        return existingStatus == Status.DELETED && entityStatus != Status.DELETED;
    }

    @Restriction
    public void canUndelete(ValidationContext context, OwnedStatusable entity) {
        validatePermission(context, "undelete");
        if (context.hasViolations()) {
            return;
        }
        entityRestrictions.canUndelete(context, entity);
    }

    @Restriction
    public boolean canUndelete() {
        return permissionService.isGranted("advertiser_entity", "undelete");
    }

    @Restriction
    public void canView(ValidationContext context) {
        validatePermission(context, "view");
    }

    @Restriction
    public void canView(ValidationContext context, OwnedStatusable parent) {
        canView(context);
        entityRestrictions.canView(context, parent);
    }

    @Restriction
    public boolean canView() {
        ValidationContext context = ValidationUtil.createContext();
        canView(context);
        return context.ok();
    }

    @Restriction
    public boolean canView(OwnedStatusable ownedStatusable) {
        ValidationContext context = ValidationUtil.createContext();
        canView(context, ownedStatusable);
        return context.ok();
    }

    @Restriction
    public boolean canViewCreative(Long creativeId) {
        ValidationContext context = ValidationUtil.createContext();
        canView(context);
        if (context.ok()) {
            Creative creative = utilityService.find(Creative.class, creativeId);
            canView(context, creative);
        }
        return context.ok();
    }

    @Restriction
    public void canUpdate(ValidationContext context) {
        validatePermission(context, "edit");
    }

    @Restriction
    public void canUpdate(ValidationContext context, OwnedStatusable ownedStatusable) {
        canUpdate(context);
        entityRestrictions.canUpdate(context, ownedStatusable);
    }

    @Restriction
    public boolean canUpdate() {
        ValidationContext context = ValidationUtil.createContext();
        canUpdate(context);
        return context.ok();
    }

    @Restriction
    public boolean canUpdate(OwnedStatusable ownedStatusable) {
        ValidationContext context = ValidationUtil.createContext();
        canUpdate(context, ownedStatusable);
        return context.ok();
    }

    @Restriction
    public boolean canResetCtr(CampaignCreativeGroup group) {
        return canResetCtr0(group) && RateType.CPM != group.getCcgRate().getRateType();
    }

    @Restriction
    public boolean canResetCtr(Campaign campaign) {
        return canResetCtr0(campaign);
    }

    private boolean canResetCtr0(OwnedStatusable<AdvertiserAccount> entity) {
        ValidationContext context = ValidationUtil.createContext();
        validatePermission(context, "reset_ctr_date");
        entityRestrictions.canUpdate(context, entity);
        return context.ok();
    }

    @Restriction
    public boolean canCreateOrUpdate(OwnedStatusable ownedStatusable) {
        return canCreate(ownedStatusable) || canUpdate(ownedStatusable);
    }

    @Restriction
    public boolean canPerformUpdate(OwnedStatusable entity, OwnedStatusable existing) {
        ValidationContext context = ValidationUtil.createContext();
        canPerformUpdate(context, entity, existing);
        return context.ok();
    }

    @Restriction
    public boolean canCreate() {
        ValidationContext context = ValidationUtil.createContext();
        canCreate(context);
        return context.ok();
    }

    @Restriction
    public boolean canCreate(OwnedStatusable parent) {
        ValidationContext context = ValidationUtil.createContext();
        canCreate(context, parent);
        return context.ok();
    }

    @Restriction
    public void canCreateCopy(ValidationContext context, Campaign campaign) {
        canCreateCopy(context, campaign, campaign.getAccount());
    }

    @Restriction
    public void canCreateCopy(ValidationContext context, CampaignCreativeGroup ccg) {
        canCreateCopy(context, ccg, ccg.getCampaign());
    }

    @Restriction
    public void canCreateCopy(ValidationContext context, Creative creative) {
        canCreateCopy(context, creative, creative.getAccount());
    }

    private void canCreateCopy(ValidationContext context, OwnedStatusable entityToCopy, OwnedStatusable parent) {
        canCreate(context);
        canUpdate(context);
        entityRestrictions.canCreateCopy(context, entityToCopy, parent);
    }

    @Restriction
    public void canDelete(ValidationContext context, OwnedStatusable ownedStatusable) {
        canUpdate(context, ownedStatusable);
        entityRestrictions.canDelete(context, ownedStatusable);
    }

    @Restriction
    public boolean canActivate() {
        return permissionService.isGranted("advertiser_entity", "activate");
    }

    @Restriction
    public void canActivate(ValidationContext context, OwnedStatusable ownedStatusable) {
        canUpdate(context, ownedStatusable);
        entityRestrictions.canActivate(context, ownedStatusable);
    }

    @Restriction
    public void canActivatePending(ValidationContext context) {
        if (!currentUserService.isExternal()) {
            validatePermission(context, "activate");
        }
    }

    public boolean canActivatePending() {
        ValidationContext context = ValidationUtil.createContext();
        canActivatePending(context);
        return context.ok();
    }

    @Restriction
    public void canInactivate(ValidationContext context, OwnedStatusable ownedStatusable) {
        canUpdate(context, ownedStatusable);
        entityRestrictions.canInactivate(context, ownedStatusable);
    }

    @Restriction
    public void canApprove(ValidationContext context) {
        validatePermission(context, "approve");
    }

    @Restriction
    public boolean canApprove() {
        ValidationContext context = ValidationUtil.createContext();
        canApprove(context);
        return context.ok();
    }

    @Restriction
    public void canApprove(ValidationContext context, OwnedApprovable ownedApprovable) {
        canApprove(context);
        entityRestrictions.canApprove(context, ownedApprovable);
    }

    @Restriction
    public void canDecline(ValidationContext context, OwnedApprovable ownedApprovable) {
        validatePermission(context, "approve");
        entityRestrictions.canDecline(context, ownedApprovable);
    }

    @Restriction
    public void canApproveChildren(ValidationContext context, OwnedStatusable ownedStatusable) {
        validatePermission(context, "approve");
        entityRestrictions.canUpdate(context, ownedStatusable);
    }

    @Restriction
    public void canUndeleteChildren(ValidationContext context, OwnedStatusable ownedStatusable) {
        validatePermission(context, "undelete");
        entityRestrictions.canUndeleteChildren(context, ownedStatusable);
    }

    @Restriction
    public void canAdvanced(ValidationContext context) {
        validatePermission(context, "advanced");
    }

    @Restriction
    public boolean canEditSiteTargeting(Account groupAccount) {
        Account existingAccount = accountService.find(groupAccount.getId());
        return existingAccount.getAccountType().isSiteTargetingFlag() &&
                (canAdvanced() || (currentUserService.isExternal() && existingAccount.isSiteTargetingFlag()));
    }

    @Restriction
    public boolean canAdvanced() {
        ValidationContext context = ValidationUtil.createContext();
        canAdvanced(context);
        return context.ok();
    }

    @Restriction
    public void canAccessTextAd(ValidationContext context, OwnedStatusable ownedStatusable) {
        doValidate(context, ownedStatusable, new Checker() {
            @Override
            public boolean check(AccountType accountType, boolean isWalledGardenEnabled) {
                return accountType.isAllowTextAdvertisingFlag() && !isWalledGardenEnabled;
            }
        });
    }

    @Restriction
    public boolean canAccessTextAd(OwnedStatusable ownedStatusable) {
        ValidationContext context = ValidationUtil.createContext();
        canAccessTextAd(context, ownedStatusable);
        return context.ok();
    }

    @Restriction
    public void canAccessKeywordTargetedTextAd(ValidationContext context, OwnedStatusable ownedStatusable) {
        doValidate(context, ownedStatusable, new Checker() {
            @Override
            public boolean check(AccountType accountType, boolean isWalledGardenEnabled) {
                return accountType.isAllowTextKeywordAdvertisingFlag();
            }
        });
    }

    @Restriction
    public void canAccessChannelTargetedTextAd(ValidationContext context, OwnedStatusable ownedStatusable) {
        doValidate(context, ownedStatusable, new Checker() {
            @Override
            public boolean check(AccountType accountType, boolean isWalledGardenEnabled) {
                return accountType.isAllowTextChannelAdvertisingFlag();
            }
        });
    }

    @Restriction
    public boolean canAccessChannelTargetedTextAd(OwnedStatusable ownedStatusable) {
        ValidationContext context = ValidationUtil.createContext();
        canAccessChannelTargetedTextAd(context, ownedStatusable);
        return context.ok();
    }

    @Restriction
    public void canAccessDisplayAd(ValidationContext context, OwnedStatusable ownedStatusable) {
        doValidate(context, ownedStatusable, new Checker() {
            @Override
            public boolean check(AccountType accountType, boolean isWalledGardenEnabled) {
                return accountType.isAllowDisplayAdvertisingFlag() &&
                        (!isWalledGardenEnabled
                                || accountType.isCPMFlag(CCGType.DISPLAY)
                                || accountType.isCPCFlag(CCGType.DISPLAY));
            }
        });
    }

    @Restriction
    public boolean canAccessDisplayAd(OwnedStatusable ownedStatusable) {
        ValidationContext context = ValidationUtil.createContext();
        canAccessDisplayAd(context, ownedStatusable);
        return context.ok();
    }

    @Restriction
    public void canCreateDisplayGroup(ValidationContext context, Campaign campaign) {
        canCreate(context, campaign);
        canAccessDisplayAd(context, campaign);
        checkCampaignType(context, campaign, CampaignType.DISPLAY);
    }

    @Restriction
    public void canCreateKeywordTargetedTextGroup(ValidationContext context, Campaign campaign) {
        canCreate(context, campaign);
        canAccessKeywordTargetedTextAd(context, campaign);
        checkCampaignType(context, campaign, CampaignType.TEXT);
    }

    @Restriction
    public void canCreateChannelTargetedTextGroup(ValidationContext context, Campaign campaign) {
        canCreate(context, campaign);
        canAccessChannelTargetedTextAd(context, campaign);
        checkCampaignType(context, campaign, CampaignType.TEXT);
    }

    private void checkCampaignType(ValidationContext context, Campaign campaign, CampaignType campaignType) {
        if (campaign.getCampaignType() != campaignType) {
            context.addConstraintViolation("errors.field.invalid")
                .withPath("campaignType")
                .withValue(campaign.getCampaignType());
        }
    }

    private void doValidate(ValidationContext context, OwnedStatusable ownedStatusable, Checker checker) {
        Account account = ownedStatusable.getAccount();
        AccountType accountType = account.getAccountType();
        AccountRole accountRole = accountType.getAccountRole();

        if (accountRole != AGENCY && accountRole != ADVERTISER) {
            return;
        }

        boolean isWalledGardenEnabled = walledGardenService.isAdvertiserWalledGarden(account.getId());
        if (!checker.check(accountType, isWalledGardenEnabled)) {
            context.addConstraintViolation("errors.operation.not.permitted");
        }
    }

    private interface Checker {
        boolean check(AccountType accountType, boolean isWalledGardenEnabled);
    }

    @Restriction
    public boolean canViewCCGCheck(CampaignCreativeGroup group) {
        Account account = accountService.find(group.getAccount().getId());
        return !account.getTestFlag() && account.getAccountType().isCampaignCheck() && currentUserService.isInternal();
    }

    @Restriction
    public boolean canUpdateCCGCheck(CampaignCreativeGroup group) {
        return canViewCCGCheck(group) && group.getNextCheckDate() != null && group.getNextCheckDate().before(new Date()) &&
                permissionService.isGranted("advertiser_entity", "log_check") &&
                (group.getDisplayStatus().getMajor().equals(DisplayStatus.Major.LIVE) || group.getDisplayStatus().getMajor().equals(DisplayStatus.Major.LIVE_NEED_ATT)) &&
                group.getCampaign().getStatus().equals(Status.ACTIVE) && group.getAccount().getInheritedStatus().equals(Status.ACTIVE);
    }

    @Restriction
    public boolean canCreateDisplayCreative(Account account) {
        return canCreate(account) && account.getAccountType().isAllowDisplayAdvertisingFlag();
    }

    @Restriction
    public boolean canCreateTextCreative(Account account) {
        return canCreate(account) && account.getAccountType().isAllowTextAdvertisingFlag();
    }

    @Restriction
    public boolean canCreateCampaignCCGs(List<CampaignCreativeGroup> groups) {
        Campaign campaign = groups.get(0).getCampaign();
        for (CampaignCreativeGroup group : groups) {
            if (campaign.getId() != group.getCampaign().getId()) {
                return false;
            }
        }
        return canCreate(campaign);
    }

    private void validateCampaignType(ValidationContext context, Campaign campaign, OperationType operationType) {
        if (operationType == OperationType.CREATE && campaign.getAccount().getId() != null) {
            Account account = em.find(Account.class, campaign.getAccount().getId());
            if (account == null) {
                return;
            }

            boolean isCampaignAllowed = campaign.getCampaignType() == CampaignType.DISPLAY
                    ? canAccessDisplayAd(account)
                    : canAccessTextAd(account);

            if (!isCampaignAllowed) {
                context.addConstraintViolation("campaign.unsupportedCampaignType")
                    .withPath("campaignType")
                    .withValue(campaign.getCampaignType())
                    .withError(BusinessErrors.CAMPAIGN_ERROR);
            }
        }
    }
}
