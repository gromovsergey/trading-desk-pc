package com.foros.session.site;

import static com.foros.config.ConfigParameters.DEFAULT_MAX_UPLOAD_SIZE;
import com.foros.config.ConfigService;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.PublisherAccount;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.security.AccountType;
import com.foros.model.security.AdvExclusionsType;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCategory;
import com.foros.model.site.SiteCreativeCategoryExclusion;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountService;
import com.foros.session.creative.CreativeValidations;
import com.foros.session.fileman.FileUtils;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.strategy.ValidationMode;

import java.io.File;
import java.security.AccessControlException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.ObjectUtils;

@LocalBean
@Stateless
@Validations
public class SiteValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private PublisherEntityRestrictions publisherEntityRestrictions;

    @EJB
    private SiteService siteService;

    @EJB
    private AccountService accountService;

    @EJB
    private ValidationService validationService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CreativeValidations creativeValidations;

    @EJB
    private ConfigService configService;

    private static final Set<String> VALID_MIME_TYPES = new HashSet<String>() {
        {
            add("text/plain");
            add("text/csv");
            add("text/html");
        }
    };

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) Site site) {
        validateSiteCategory(context, site);
        validateFrequencyCap(context, site, "FrequencyCap.update");
        validateCreativeExclusions(context, site);
        validateNoAdsTimeout(context, site);
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) Site site) {
        validateSecurity(context, site);
        validateSiteCategory(context, site);
        validateFrequencyCap(context, site, "FrequencyCap.update");
        validateCreativeExclusions(context, site);
        validateNoAdsTimeout(context, site);
    }

    @Validation
    public void validateCreateOrUpdate(ValidationContext context, @ValidateBean(ValidationMode.BULK) Site site) {
        // check if site really exists
        Long id = site.getId();
        if (id != null) {
            Site existingSite = em.find(Site.class, id);

            if (existingSite == null) {
                // no site available with input id
                context
                        .addConstraintViolation("site.error.not.found")
                        .withPath("id")
                        .withParameters("{Site.entityName}", id)
                        .withValue(id);
                return;
            }

            validateAccount(context, site, existingSite);

            Status existingStatus = existingSite.getStatus();
            if (!currentUserService.isSiteAccessGranted(site) || (currentUserService.isExternal() &&
                    existingStatus != site.getStatus() && existingStatus == Status.DELETED)) {
                // user is trying to change deleted site, do not allow
                context
                        .addConstraintViolation("errors.forbidden")
                        .withParameters(site.getName())
                        .withPath("error")
                        .withValue(site.getName());
            }
        } else {
            validateAccount(context, site, null);
        }

        if (siteService.isDuplicateSite(site)) {
            context
                    .addConstraintViolation("errors.duplicate.sites")
                    .withParameters("'" + site.getName() + "'")
                    .withPath("name")
                    .withValue(site.getName());
        }
    }

    private void validateAccount(ValidationContext context, Site site, Site existingSite) {
        PublisherAccount publisherAccount = site.getAccount();
        Long publisherId = publisherAccount.getId();
        String publisherName = publisherAccount.getName();

        boolean accountIsChanged = false;
        ValidationContext accountSubContext = context.createSubContext(site.getAccount(), "account");
        if (publisherId == null) {
            accountSubContext
                .addConstraintViolation("errors.field.required")
                .withPath("id");
        } else if (existingSite != null && !existingSite.getAccount().getId().equals(publisherId)) {
            accountIsChanged = true;
        }

        if (publisherAccount.isChanged("name")) {
            if (publisherName == null) {
            accountSubContext
                .addConstraintViolation("errors.field.required")
                .withPath("name");
            } else if (existingSite != null && !existingSite.getAccount().getName().equals(publisherName)) {
                accountIsChanged = true;
            }
        }

        if (accountIsChanged) {
            accountSubContext
                .addConstraintViolation("site.error.change.account")
                .withParameters(site.getName())
                .withPath("name");
        } else if (!publisherEntityRestrictions.canUpload(publisherId)) {
            context
                .addConstraintViolation("errors.forbidden")
                .withParameters(site.getName())
                .withPath("id")
                .withValue(site.getName());
        }
    }

    @Validation
    public void validateFileUpload(ValidationContext context, File fileToUpload) {
        if (fileToUpload != null) {
            long fileSize = fileToUpload.length();
            if (fileSize > configService.get(DEFAULT_MAX_UPLOAD_SIZE)) {
                 context
                        .addConstraintViolation("errors.file.sizeExceeded")
                        .withPath("fileToUpload");
            } else if (fileSize <= 0) {
                context
                        .addConstraintViolation("errors.invalidfile")
                        .withParameters(fileToUpload.getName())
                        .withPath("fileToUpload");
            }
        }

        if (!VALID_MIME_TYPES.contains(FileUtils.getMimeTypeByMagic(fileToUpload))) {
            context.addConstraintViolation("errors.field.type").withPath("fileToUpload");
        }
    }

    private void validateSecurity(ValidationContext context, Site site) {
        Site existing = siteService.find(site.getId());
        if (context.isReachable("siteCategory")
                && existing.getSiteCategory() != null
                && !existing.getSiteCategory().getId().equals(site.getSiteCategory().getId()) // Remove this after struts 2 migrations
                && !publisherEntityRestrictions.canAdvanced()) {
            throw new AccessControlException("Update for field Site.siteCategory is restricted by [PublisherEntity.advanced] restriction");
        }
    }

    private void validateSiteCategory(ValidationContext context, Site site) {
        if (context.isReachable("siteCategory")
                && site.getSiteCategory() != null
                && !hasViolation("siteCategory", context.getConstraintViolations())) {
            SiteCategory existing = em.find(SiteCategory.class, site.getSiteCategory().getId());
            if (existing == null) {
                context
                        .addConstraintViolation("error.deleted.category")
                        .withParameters("{site.category}")
                        .withPath("siteCategory")
                        .withValue(site.getSiteCategory());
            }
            else {
                Account pubAccount = accountService.findPublisherAccount(site.getAccount().getId());
                if (!ObjectUtils.equals(existing.getCountry(), pubAccount.getCountry())) {
                    context
                            .addConstraintViolation("error.category.invalid.country")
                            .withParameters("{site.category}")
                            .withPath("siteCategory")
                            .withValue(site.getSiteCategory());
                }
            }
        }
    }

    private void validateFrequencyCap(ValidationContext context, Site site, String validationName) {
        if (context.isReachable("frequencyCap") && site.getFrequencyCap() != null) {
            AccountType accountType = accountService.findPublisherAccount(site.getAccount().getId()).getAccountType();
            if (!accountType.isFreqCapsFlag()) {
                context
                        .addConstraintViolation("errors.freqCapAllowedForSite")
                        .withPath("frequencyCapDisabled");
            } else {
                validationService.validateWithContext(context.createSubContext(site.getFrequencyCap(), "frequencyCap"), validationName, site.getFrequencyCap());
            }
        }
    }

    private void validateNoAdsTimeout(ValidationContext context, Site site) {
        if (context.isReachable("noAdsTimeout")) {
            AccountType accountType = accountService.findPublisherAccount(site.getAccount().getId()).getAccountType();
            if (!accountType.isFreqCapsFlag()) {
                if (site.getNoAdsTimeout() != null && !site.getNoAdsTimeout().equals(Long.valueOf(0L))) {
                    context
                            .addConstraintViolation("errors.noAdsTimeoutAllowedForSite")
                            .withPath("noAdsTimeoutDisabled");
                }
            } else {
                if (site.getNoAdsTimeout() == null) {
                    context
                            .addConstraintViolation("errors.field.required")
                            .withPath("noAdsTimeout");
                }
            }
        }
    }

    private boolean hasViolation(String path, Set<ConstraintViolation> violations) {
        for (ConstraintViolation violation : violations) {
            if (ObjectUtils.equals(path, violation.getPropertyPath().toString())) {
                return true;
            }
        }
        return false;
    }

    private void validateCreativeExclusions(ValidationContext context, Site site) {
        Set<SiteCreativeCategoryExclusion> categoryExclusions = site.getCategoryExclusions();
        if (context.isReachable("categoryExclusions") && !categoryExclusions.isEmpty()) {
            AccountType existingAccountType = accountService.find(site.getAccount().getId()).getAccountType();
            if (AdvExclusionsType.DISABLED == existingAccountType.getAdvExclusions()) {
                // site level creative exclusion is disabled but user still has provided few creative exclusions
                context
                        .addConstraintViolation("site.error.creativeExclusionDisabled")
                        .withPath("siteLevelCreativeExclusionError");
            }
            if (!existingAccountType.isAdvExclusionApprovalAllowed()) {
                for (SiteCreativeCategoryExclusion exclusion : categoryExclusions) {
                    if (exclusion.getCreativeCategory().getType() == CreativeCategoryType.CONTENT && exclusion.getApproval() == CategoryExclusionApproval.APPROVAL) {
                        context
                                .addConstraintViolation("site.error.creativeExclusionApproval")
                                .withPath("siteLevelCreativeExclusionError");
                        break;
                    }
                }
            }
            validateTags(context, categoryExclusions);
        }
    }

    private void validateTags(ValidationContext context, Set<SiteCreativeCategoryExclusion> categoryExclusions) {
        for (SiteCreativeCategoryExclusion categoryExclusion : categoryExclusions) {
            if (categoryExclusion.getCreativeCategory().getId() == null) {
                // this is a newly added tag
                creativeValidations.validateTagName(context, categoryExclusion.getCreativeCategory().getDefaultName());
                if (context.hasViolation("selectedTags")) {
                    break;
                }
            }
        }
    }


    @Validation
    public void validateNameConstraintViolations(ValidationContext context, Long accountId, List<Site> sitesList) {
        Collection<Site> siteList = siteService.findDuplicated(sitesList, accountId);

        StringBuilder siteNamesBuilder = new StringBuilder();
        for (Site site : siteList) {
            siteNamesBuilder.append(" '").append(site.getName()).append("',");
        }
        String siteNames = siteNamesBuilder.toString();
        siteNames = siteNames.substring(0, siteNames.length() - 1);
        context.addConstraintViolation("errors.duplicate.sites")
                .withParameters(siteNames)
                .withPath("error");
    }
}
