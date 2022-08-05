package com.foros.action.admin.accountType;

import com.foros.action.Invalidable;
import com.foros.action.admin.AbstractNameLocalizableNameComparator;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.LocalizableName;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.creative.CreativeSize;
import com.foros.session.creative.CreativeSizeTO;
import com.foros.model.security.AccountType;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateTO;
import com.foros.security.AccountRole;
import com.foros.session.security.AccountCreativeSizeEntityTO;
import com.foros.session.security.AccountTemplateEntityTO;
import com.foros.util.CollectionUtils;
import com.foros.util.mapper.Converter;
import com.foros.util.mapper.IdentifiableTOMapper;
import com.foros.validation.annotation.Validate;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName = "channelChecks.First.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "channelChecks.Second.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "channelChecks.Third.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "campaignChecks.First.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "campaignChecks.Second.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "campaignChecks.Third.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "maxKeywordLength", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "maxUrlLength", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "maxKeywordsPerGroup", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "maxKeywordsPerChannel", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "maxUrlsPerChannel", key = "errors.field.integer")
        }
)
public class SaveAccountTypeAction extends AccountTypeSupportAction implements Invalidable, BreadcrumbsSupport {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("channelFirstCheck", "'channelChecks.First.value'", "violation.message")
            .add("channelSecondCheck", "'channelChecks.Second.value'", "violation.message")
            .add("channelThirdCheck", "'channelChecks.Third.value'", "violation.message")
            .add("campaignFirstCheck", "'campaignChecks.First.value'", "violation.message")
            .add("campaignSecondCheck", "'campaignChecks.Second.value'", "violation.message")
            .add("campaignThirdCheck", "'campaignChecks.Third.value'", "violation.message")
            .rules();

    @Validate(validation = "AccountType.create", parameters = "#target.prepareAccountType()")
    public String create() {
        prepareAccountType();
        service.create(entity);
        return SUCCESS;
    }

    public AccountType prepareAccountType() {
        setSizesTemplates();
        setCheckIntervals();
        AccountRole accountRole = entity.getAccountRole();
        if (!(AccountRole.ADVERTISER.equals(accountRole) || AccountRole.AGENCY.equals(accountRole))) {
            entity.getCcgTypes().clear();
        }
        setDeviceChannels();
        return getEntity();
    }

    @Validate(validation = "AccountType.update", parameters = "#target.prepareAccountType()")
    public String update() {
        prepareAccountType();
        service.update(entity);
        return SUCCESS;
    }

    private void setSizesTemplates() {
        AccountRole role = entity.getAccountRole();
        if (role == AccountRole.ADVERTISER || role == AccountRole.AGENCY) {
            entity.setTemplates(new LinkedHashSet<>(CollectionUtils.convert(new Converter<TemplateTO, Template>() {
                @Override
                public Template item(TemplateTO to) {
                    return new CreativeTemplate(to.getId());
                }
            }, creativeTemplateList)));
        } else if (role == AccountRole.PUBLISHER) {
            entity.setTemplates(new LinkedHashSet<>(CollectionUtils.convert(new Converter<TemplateTO, Template>() {
                @Override
                public Template item(TemplateTO to) {
                    return new DiscoverTemplate(to.getId());
                }
            }, discoverTemplateList)));
        }

        if (role == AccountRole.ADVERTISER || role == AccountRole.AGENCY || role == AccountRole.PUBLISHER) {
            entity.setCreativeSizes(new LinkedHashSet<>(CollectionUtils.convert(new Converter<CreativeSizeTO, CreativeSize>() {
                @Override
                public CreativeSize item(CreativeSizeTO s) {
                    return new CreativeSize(s.getId());
                }
            }, creativeSizeList)));
        }

    }

    private void setCheckIntervals() {
        AccountType model = getModel();

        if (model.isChannelCheck()) {
            for (CheckNumber cn : CheckNumber.values()) {
                model.setChannelCheckByNum(cn.ordinal() + 1, getChannelChecks().get(cn).getTimeSpan());
            }
        }

        if (model.isCampaignCheck()) {
            for (CheckNumber cn : CheckNumber.values()) {
                model.setCampaignCheckByNum(cn.ordinal() + 1, getCampaignChecks().get(cn).getTimeSpan());
            }
        }
    }

    @Override
    public void invalid() {
        populateUIControls();
        changesCheck = service.getAccountTypeChangesCheck(entity);

        AccountRole role = entity.getAccountRole();

        if ((role == AccountRole.ADVERTISER || role == AccountRole.AGENCY || role == AccountRole.PUBLISHER)
                && !getEntity().getCreativeSizes().isEmpty()) {
            creativeSizeList = new LinkedList<>();
            Map<Long, CreativeSizeTO> sizeMap = CollectionUtils.map(new IdentifiableTOMapper<CreativeSizeTO>(), getAvailableSizes());
            for (CreativeSize cs : getEntity().getCreativeSizes()) {
                creativeSizeList.add(sizeMap.get(cs.getId()));
            }
        }

        if ((role == AccountRole.ADVERTISER || role == AccountRole.AGENCY)
                && !getEntity().getTemplates().isEmpty()) {
            creativeTemplateList = new LinkedList<>();
            Map<Long, TemplateTO> map = CollectionUtils.map(new IdentifiableTOMapper<TemplateTO>(), getAvailableCreativeTemplates());
            for (Template t : getEntity().getTemplates()) {
                creativeTemplateList.add(map.get(t.getId()));
            }
        } else if (role == AccountRole.PUBLISHER) {
            discoverTemplateList = new LinkedList<>();
            Map<Long, TemplateTO> map = CollectionUtils.map(new IdentifiableTOMapper<TemplateTO>(), getAvailableDiscoverTemplates());
            for (Template t : getEntity().getTemplates()) {
                discoverTemplateList.add(map.get(t.getId()));
            }
        }

        if (hasActionErrors()) {
            if (getActionErrors().contains(getText("AccountType.invalid.field", new String[]{getText("AccountType.advExclusionApprovalFlag")}))) {
                setAdvExclusionApprovalFlag(false);
            }
        }
    }


    private static class AccountSizeComparator extends AbstractNameLocalizableNameComparator<AccountCreativeSizeEntityTO> {
        @Override
        protected int compareLocalizableNames(Comparator<LocalizableName> comparator, AccountCreativeSizeEntityTO o1, AccountCreativeSizeEntityTO o2) {
            return comparator.compare(o1.getSizeName(), o2.getSizeName());
        }
    }

    private static class AccountTemplateComparator extends AbstractNameLocalizableNameComparator<AccountTemplateEntityTO> {
        @Override
        protected int compareLocalizableNames(
                Comparator<LocalizableName> comparator,
                AccountTemplateEntityTO o1,
                AccountTemplateEntityTO o2) {

            return comparator.compare(o1.getTemplateName(), o2.getTemplateName());
        }
    }

    protected void setDeviceChannels() {
        getModel().getDeviceChannels().clear();
        if (AccountRole.ADVERTISER.equals(entity.getAccountRole()) || AccountRole.AGENCY.equals(entity.getAccountRole())) {
            for (Long id : getDeviceHelper().getSelectedChannels()) {
                DeviceChannel ch = deviceChannelService.findById(id);
                getModel().getDeviceChannels().add(ch);
            }
        }
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        if (entity.getId() != null) {
            AccountType persistent = service.findById(entity.getId());
            breadcrumbs = new Breadcrumbs().add(new AccountTypesBreadcrumbsElement()).add(new AccountTypeBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = new Breadcrumbs().add(new AccountTypesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
