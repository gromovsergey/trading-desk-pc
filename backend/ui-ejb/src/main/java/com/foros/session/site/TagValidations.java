package com.foros.session.site;

import com.foros.model.Country;
import com.foros.model.account.MarketplaceType;
import com.foros.model.account.PublisherAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.creative.SizeType.MultipleSizes;
import com.foros.model.security.AccountType;
import com.foros.model.site.ContentCategory;
import com.foros.model.site.SiteRate;
import com.foros.model.site.SiteRateType;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroupState;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionValue;
import com.foros.model.template.Template;
import com.foros.restriction.RestrictionService;
import com.foros.security.principal.SecurityContext;
import com.foros.session.account.AccountService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.SizeTypeService;
import com.foros.session.template.OptionGroupStateHelper;
import com.foros.session.template.OptionValueValidations;
import com.foros.session.template.TemplateService;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.strategy.ValidationMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

@LocalBean
@Stateless
@Validations
public class TagValidations {

    private static final class OptionTransformer implements Transformer {
        @Override
        public Object transform(Object input) {
            return ((Option) input).getId();
        }
    }

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;
    @EJB
    private TagsService tagsService;
    @EJB
    private WalledGardenService walledGardenService;
    @EJB
    private CountryService countryService;
    @EJB
    private SiteService siteService;
    @EJB
    private RestrictionService restrictionService;
    @EJB
    private AccountService accountService;
    @EJB
    private OptionValueValidations optionValueValidations;
    @EJB
    private TemplateService templateService;
    @EJB
    private CreativeSizeService creativeSizeService;
    @EJB
    private SizeTypeService sizeTypeService;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) Tag tag) {
        validateInventoryEstimation(context, tag);
        validateCreativeExclusions(context, tag);
        validateSizes(context, tag);
        validateMarketplaceType(context, tag);
        validateContentCategories(context, tag);
        validateTagPricings(context, tag);
        validateExpandable(context, tag);
    }

    @Validation
    public void validateUpdateOptions(ValidationContext context, Tag tag) {
        validateVersion(context, tag, em.find(Tag.class, tag.getId()));

        // further checks aren't suitable as the entity was concurrently updated and we need to refresh it anyway
        if (context.hasViolations()) {
            return;
        }

        validateOptions(context, tag);
    }

    private void validateVersion(ValidationContext context, Tag tag, Tag existing) {
        if (context.isReachable("version") && !ObjectUtils.equals(tag.getVersion(), existing.getVersion())) {
            context.addConstraintViolation("errors.version").withValue(tag.getVersion()).withPath("version");
        }
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) Tag tag) {
        validateInventoryEstimation(context, tag);
        validateCreativeExclusions(context, tag);
        validateSizes(context, tag);
        validateMarketplaceType(context, tag);
        validateContentCategories(context, tag);
        validateParentSite(context, tag);
        validateTagPricings(context, tag);
        validateSiteRates(context, tag);
        validateExpandable(context, tag);
    }

    private void validateTagPricings(ValidationContext context, Tag tag) {
        if (context.isReachable("pricing")) {
            List<TagPricing> pricings = tag.getTagPricings();
            if (!tag.isInventoryEstimationFlag() && pricings.isEmpty()) {
                context
                        .addConstraintViolation("errors.field.required")
                        .withPath("pricing");
            }

            int maxFractionDigits = accountService.find(tag.getAccount().getId()).getCurrency().getFractionDigits();
            for (int i = 0; i < pricings.size(); i++) {
                TagPricing pricing = pricings.get(i);
                ValidationContext subContext = context.createSubContext(pricing, "pricings", i);
                validateTagPricingCPM(subContext, maxFractionDigits, pricing);
                validateTagPricingRateType(subContext, pricing);
                if (i > 0) {
                    validateTagPricingUnique(subContext, pricing, pricings.subList(0, i));
                }
            }
        }
    }

    private void validateSiteRates(ValidationContext context, Tag tag) {
        if (!SecurityContext.isInternal() && context.isReachable("pricing")) {
            Long tagId = tag.getId();
            if (tagId != null) {
                Tag existing = em.find(Tag.class, tagId);
                for (int i = 0; i < tag.getTagPricings().size(); i++) {
                    TagPricing newPricing = tag.getTagPricings().get(i);
                    ValidationContext subContext = context.createSubContext(newPricing, "pricings", i);
                    boolean exist = false;
                    for (TagPricing oldPricing : existing.getTagPricings()) {
                        if (TagPricingUtil.isSameTagPricing(oldPricing, newPricing)) {
                            SiteRateType newRateType = newPricing.getSiteRate().getRateType();
                            SiteRateType oldRateType = oldPricing.getSiteRate().getRateType();
                            if (newRateType != oldRateType && oldRateType == SiteRateType.CPM) {
                                subContext
                                    .addConstraintViolation("site.edittag.tagPricings.error.cantChangeRateType")
                                    .withPath("rateType");
                            } else if (newRateType == SiteRateType.RS &&
                                    newPricing.getSiteRate().getRate().compareTo(oldPricing.getSiteRate().getRate()) != 0) {
                                subContext
                                    .addConstraintViolation("site.edittag.tagPricings.error.cantChangeRateValue")
                                    .withPath("rate");
                            }
                            exist = true;
                        }
                    }
                    if (!exist && newPricing.getSiteRate().getRateType() == SiteRateType.RS) {
                        subContext.addConstraintViolation("site.edittag.tagPricings.error.cantAddRevenueShare").withPath("rateType");
                    }
                }
            } else {
                for (int i = 0; i < tag.getTagPricings().size(); i++) {
                    TagPricing newPricing = tag.getTagPricings().get(i);
                    ValidationContext subContext = context.createSubContext(newPricing, "pricings", i);
                    if (newPricing.getSiteRate().getRateType() == SiteRateType.RS) {
                        subContext.addConstraintViolation("site.edittag.tagPricings.error.cantAddRevenueShare").withPath("rateType");
                    }
                }
            }
        }
    }

    private void validateTagPricingUnique(ValidationContext context, TagPricing tp, List<TagPricing> others) {
        if (tp.isDefault()) {
            context.addConstraintViolation("site.edittag.tagPricings.error.uniqueDefault")
                    .withPath("unique");
            return;
        }
        for (TagPricing other : others) {
            if (TagPricingUtil.isSameTagPricing(tp, other)) {
                context.addConstraintViolation("site.edittag.tagPricings.error.unique")
                        .withPath("unique");
                break;
            }
        }
    }

    private void validateTagPricingCPM(ValidationContext context, int maxFractionDigits, TagPricing tp) {
        SiteRate siteRate = tp.getSiteRate();
        if (siteRate.getRateType() == null) {
            context.addConstraintViolation("errors.field.required").withPath("rateType");
            return;
        }
        boolean costViolated = false;
        if (siteRate != null && siteRate.getRate() != null) {
            switch (siteRate.getRateType()) {
                case CPM:
                    BigDecimal rate = siteRate.getRate();
                    context.validator(FractionDigitsValidator.class)
                            .withFraction(maxFractionDigits)
                            .withPath("rate")
                            .validate(rate);
                    if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.precision() - rate.scale() > 7) {
                        costViolated = true;
                    }
                    break;
                case RS:
                    BigDecimal ratePercent = siteRate.getRatePercent();
                    context.validator(FractionDigitsValidator.class)
                            .withFraction(2)
                            .withPath("rate")
                            .validate(ratePercent);
                    context.validator(RangeValidator.class)
                            .withMin(BigDecimal.ZERO)
                            .withMax(BigDecimal.valueOf(100))
                            .withPath("rate")
                            .validate(ratePercent);
                    break;
                default:
                    costViolated = true;
                    break;
            }
        } else {
            costViolated = true;
        }
        if(costViolated){
            context.addConstraintViolation("errors.cost").withPath("rate");
        }
    }

    private void validateTagPricingRateType(ValidationContext context, TagPricing tp) {
        if (!CCGType.DISPLAY.equals(tp.getCcgType()) && tp.getCcgRateType() != null) {
            context.addConstraintViolation("site.edittag.tagPricings.error.rateTypeForDisplayOnly")
                    .withPath("rateType");
        }
    }

    @Validation
    public void validateLivePreview(ValidationContext context, Tag tag) {
        PublisherAccount account = accountService.findPublisherAccount(tag.getAccount().getId());

        Collection<Long> allowedOptionsIds = getAllowOptionsIdsForLivePreview(tag);
        for (OptionValue optionValue : tag.getOptions()) {
            long optionId = optionValue.getOptionId();
            Option option = em.find(Option.class, optionId);
            optionValue.setOption(option);

            if (option == null || !allowedOptionsIds.contains(option.getId())) {
                context.addConstraintViolation("site.tagsPreview.invalid.options").withPath("options");
                return;
            }

            optionValueValidations.validateOptionValue(context, optionValue, account);
        }
    }

    @Validation
    public void validateCreateOrUpdate(ValidationContext context, @ValidateBean(ValidationMode.BULK) Tag tag) {
        Long id = tag.getId();
        if (id != null) {
            Tag existingTag = em.find(Tag.class, id);

            if (existingTag == null) {
                // no tag available with input id
                context
                        .addConstraintViolation("site.error.not.found")
                        .withPath("id")
                        .withParameters("{Tag.entityName}", id)
                        .withValue(id);
                return;
            }
        }

        SizeType sizeType = validateSizeType(context, tag);
        validateSizesByProtocol(context, tag, sizeType);
        validateTagPricingCountry(context, tag);
        validateTagPricings(context, tag);
        validateSiteRates(context, tag);

        if (id != null) {
            validateParentSite(context, tag);
        }
        validateExpandable(context, tag);
    }

    private SizeType validateSizeType(ValidationContext context, final Tag tag) {
        if (StringUtil.isPropertyEmpty(tag.getSizeType().getDefaultName())) {
            context
                .addConstraintViolation("errors.field.required")
                .withPath("sizeType");
            return null;
        }

        SizeType sizeType = getAccountType(tag).findSizeTypeByName(tag.getSizeType().getDefaultName());

        if (sizeType == null) {
            context
                .addConstraintViolation("unauthorized.creative.sizeType.usage")
                .withPath("sizeType");
            return null;
        }
        return sizeType;
    }

    private void validateSizes(ValidationContext context, Tag tag) {
        // validate size being used
        if (!hasViolation("sizes", context.getConstraintViolations())) {
            SizeType sizeType = tag.getSizeType();
            if (MultipleSizes.ONE_SIZE == sizeType.getMultipleSizes()) {
                if (tag.getSizes().size() > 1) {
                    context
                        .addConstraintViolation("length.creative.size.usage")
                        .withPath("sizes");
                }
            }

            Set<CreativeSize> sizes = new TreeSet<>(CreativeSize.BY_PROTOCOL_COMPARATOR);
            sizes.addAll(getAllSizes(tag));
            for (CreativeSize creativeSize : tag.getSizes()) {
                if (!sizes.contains(creativeSize)) {
                    context
                        .addConstraintViolation("unauthorized.creative.size.usage")
                        .withPath("sizes");
                    break;
                }
            }

            if (tag.getSizes().isEmpty() && !tag.isAllSizesFlag()) {
                context
                    .addConstraintViolation("errors.field.required")
                    .withPath("sizes");
            }

        }
    }

    private void validateOptions(ValidationContext context, Tag tag) {
        PublisherAccount account = accountService.findPublisherAccount(tag.getAccount().getId());
        List<CreativeTemplate> templates = templateService.findTemplatesWithPublisherOptions(tag);

        Map<Long, OptionGroupState> statesByOptionId = OptionGroupStateHelper.getGroupsStatesByOptionId(
            tag.getGroupStates(), templates, tag.getEffectiveSizes(), OptionGroupType.Publisher);

        Collection<Long> allowedOptionsIds = getAllowOptionsIds(getAllSizes(tag), templates);
        for (OptionValue optionValue : tag.getOptions()) {
            long optionId = optionValue.getOptionId();
            Option option = em.find(Option.class, optionId);
            optionValue.setOption(option);

            if (option == null || !allowedOptionsIds.contains(option.getId())) {
                context.addConstraintViolation("site.tagsPreview.invalid.options").withPath("options");
                return;
            }
            OptionGroupState state = statesByOptionId.get(optionValue.getOptionId());
            if (state.getEnabled()) {
                optionValueValidations.validateOptionValue(context, optionValue, account);
            }
        }
    }

    private Collection<Long> getAllowOptionsIdsForLivePreview(Tag tag) {
        CreativeTemplate textTemplate = templateService.findTextTemplate();
        return getAllowOptionsIds(getAllSizes(tag), Collections.singleton(textTemplate));
    }

    private Collection<Long> getAllowOptionsIds(Collection<CreativeSize> sizes, Collection<CreativeTemplate> templates) {
        Collection<Option> publisherOptions = new ArrayList<Option>();
        for (Template template : templates) {
            publisherOptions.addAll(template.getPublisherOptions());
        }

        for (CreativeSize size : sizes) {
            publisherOptions.addAll(size.getPublisherOptions());
        }

        Collection<Long> allowedOptionsIds = CollectionUtils.collect(publisherOptions, new OptionTransformer());

        return allowedOptionsIds;
    }

    private void validateSizesByProtocol(ValidationContext context, Tag tag, SizeType sizeType) {
        if (hasViolation("sizeType", context.getConstraintViolations())) {
            return;
        }

        // validate size being used
        List<CreativeSize> allSizes = getAllSizes(tag);
        Map<String, String> sizeTypeNamesByProtocol = new HashMap<String, String>(allSizes.size());
        for (CreativeSize size : allSizes) {
            sizeTypeNamesByProtocol.put(size.getProtocolName(), size.getSizeType().getDefaultName());
        }

        Set<CreativeSize> creativeSizes = tag.getSizes();
        for (CreativeSize size : creativeSizes) {
            String protocol = size.getProtocolName();
            if (StringUtil.isPropertyEmpty(protocol)) {
                context
                    .addConstraintViolation("errors.field.required")
                    .withPath("sizes");
                return;
            }

            boolean matchFound = sizeTypeNamesByProtocol.containsKey(protocol);
            if ((!matchFound && !hasViolation("sizes", context.getConstraintViolations()))
                    || (!tag.getSizeType().getDefaultName().equals(sizeTypeNamesByProtocol.get(size.getProtocolName())))) {
                context
                    .addConstraintViolation("unauthorized.creative.size.usage")
                    .withPath("sizes");
                break;
            }
        }

        if (!hasViolation("sizes", context.getConstraintViolations()) &&
                (sizeType.isSingleSize() && (tag.isAllSizesFlag() || creativeSizes.size() > 1))) {
            context
                .addConstraintViolation("unauthorized.creative.size.single")
                .withPath("sizes");
        }

    }

    private void validateInventoryEstimation(ValidationContext context, Tag tag) {
        // check if accountType inventory Estimation Flag is disable
        if (!siteService.find(tag.getSite().getId()).getAccount().getAccountType().isPublisherInventoryEstimationFlag() && tag.isInventoryEstimationFlag()) {
            context
                    .addConstraintViolation("inventory.estimation.disabled")
                    .withPath("adservingMode");
        }
    }

    private void validateParentSite(ValidationContext context, Tag tag) {
        if (!ObjectUtils.equals(tagsService.find(tag.getId()).getSite().getId(), tag.getSite().getId())) {
            context
                    .addConstraintViolation("site.tag.error.changeSite")
                    .withPath("error");
        }
    }

    private void validateMarketplaceType(ValidationContext context, Tag tag) {
        if (tag.isChanged("marketplaceType")) {
            boolean isWalledGardenEnabled = walledGardenService.findByPublisher(siteService.find(tag.getSite().getId()).getAccount().getId()) != null;

            if (isWalledGardenEnabled ^ (tag.getMarketplaceType() != MarketplaceType.NOT_SET)) {
                context
                        .addConstraintViolation("WalledGarden.validation.marketplace")
                        .withPath("wgSettings");
            }
        }
    }

    private void validateContentCategories(ValidationContext context, Tag tag) {
        Tag existingTag = null;

        if (tag.getId() != null) {
            existingTag = tagsService.find(tag.getId());
        }

        if (existingTag == null ||
 (existingTag.getContentCategories().isEmpty() || restrictionService
                        .isPermitted("PublisherEntity.advanced"))) {
            Set<ContentCategory> categories = tag.getContentCategories();

            if (categories.isEmpty()) {
                // content categories are mandatory
                context
.addConstraintViolation("errors.field.required").withPath("contentCategories");

                return;
            }

            for (ContentCategory category : categories) {
                if (countryService.findContentCategory(category.getId()) == null) {
                    context
.addConstraintViolation("error.deleted.category").withParameters("{site.tag.category}")
                            .withPath("contentCategories");
                    break;
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

    private void validateTagPricingCountry(ValidationContext context, Tag tag) {
        if (!tag.getTagPricings().isEmpty()) {
            List<String> missingCountries = new ArrayList<String>();

            for (TagPricing tp : tag.getTagPricings()) {
                if (tp.getCountry() != null) {
                    String countryCode = tp.getCountry().getCountryCode();

                    if (em.find(Country.class, countryCode) == null) {
                        missingCountries.add(countryCode);
                    }
                }
            }

            if (!missingCountries.isEmpty()) {
                // invalid countries exist, add related error
                context.addConstraintViolation("site.error.not.found")
                        .withParameters("{site.edittag.tagPricings.country}", StringUtils.join(missingCountries, ", "));
            }
        }
    }

    private void validateCreativeExclusions(ValidationContext context, Tag tag) {
        if (!siteService.find(tag.getSite().getId()).getAccount().getAccountType().isAdvExclusionSiteTagFlag()
                && tag.isTagLevelExclusionFlag()) {
            // tag level creative exclusion not allowed but user chose to enable it
            context
                    .addConstraintViolation("site.tag.error.tagCreativeExclusionDisabled")
                    .withPath("tagLevelCreativeExclusionError");
        }
    }

    private void validateExpandable(ValidationContext context, Tag tag) {
        if (context.props("position", "sizes", "sizeType").reachableAndNoViolations()) {
            if (tag.isAllowExpandable()) {
                boolean isExpandable = false;
                List<CreativeSize> allSizes = getAllSizes(tag);
                Set<CreativeSize> sizes = new HashSet<>(allSizes.size());
                if (tag.isAllSizesFlag()) {
                    sizes.addAll(allSizes);
                } else {
                    Map<String, CreativeSize> sizesByProtocolName = new HashMap<>(allSizes.size());
                    for (CreativeSize size : allSizes) {
                        sizesByProtocolName.put(size.getProtocolName(), size);
                    }

                    for (CreativeSize creativeSize : tag.getSizes()) {
                        String protocolName = creativeSize.getProtocolName();
                        if (!sizesByProtocolName.containsKey(protocolName)) {
                            sizesByProtocolName.remove(protocolName);
                        }
                    }
                    sizes.addAll(sizesByProtocolName.values());
                }

                for (CreativeSize size : sizes) {
                    if (size.isExpandable()) {
                        isExpandable = true;
                        break;
                    }
                }

                if (!isExpandable) {
                    context.addConstraintViolation("tags.error.invalid.allowExpandable").withPath("allowExpandable");
                }
            }
        }
    }

    private List<CreativeSize> getAllSizes(Tag tag) {
        List<CreativeSize> list = getAccountType(tag).findSizesBySizeTypeName(tag.getSizeType().getDefaultName());
        return list;
    }

    private AccountType getAccountType(Tag tag) {
        AccountType accountType;
        if (tag.getAccount() == null) {
            accountType = siteService.find(tag.getSite().getId()).getAccount().getAccountType();
        } else {
            accountType = accountService.find(tag.getAccount().getId()).getAccountType();
        }
        return accountType;
    }

}
