package com.foros.action.creative.display;

import com.foros.action.Invalidable;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.template.CreativeTemplate;
import com.foros.security.principal.SecurityContext;
import com.foros.session.account.AccountService;
import com.foros.session.account.yandex.brand.YandexTnsBrandService;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.util.StringUtil;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "weight", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.periodSpan.value", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowLengthSpan.value", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowCount", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.lifeCount", key = "errors.field.integer")
        })
public class SaveCreativeAction extends EditCreativeActionBase implements Invalidable {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .match("frequencyCap.(#path)").apply("'campaignCreative.frequencyCap.' + groups[0]", "violation.message")
            .match("options[(#index)](#path)", BusinessErrors.CREATIVE_INVALID_OPTION_ERROR).apply("violation.message")
            .match("options[(#index)](#path)").apply("'optionValues[' + groups[0] + ']' + groups[1]", "violation.message")
            .rules();

    private Collection<Long> selectedVisualCategories;

    private Collection<Long> selectedContentCategories;

    private Collection<String> selectedTags = new LinkedList<>();

    @EJB
    private CampaignCreativeService campaignCreativeService;

    private CampaignCreative campaignCreative = new CampaignCreative();

    private Long tnsBrandId;

    @EJB
    private AccountService accountService;

    @EJB
    private YandexTnsBrandService tnsBrandService;

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public String create() throws Exception {
        prepare();
        boolean linkToGroup = !getCcgId().isEmpty();
        if (linkToGroup) {
            campaignCreativeService.createCreativeWithLinks(creative, campaignCreative, getCcgId());
        } else {
            displayCreativeService.create(creative);
        }
        return linkToGroup ? "success.byCCG" : SUCCESS;
    }

    public String update() throws Exception {
        prepare();
        displayCreativeService.update(creative);
        return SUCCESS;
    }

    private void prepareTnsObjects() {
        creative.setAccount(accountService.findAdvertiserAccount(creative.getAccount().getId()));
        if (getTnsBrandId() != null) {
            creative.setTnsBrand(tnsBrandService.find(getTnsBrandId()));
        } else {
            creative.setTnsBrand(null);
        }
    }

    private void prepare() {
        processTagSizes();
        processOptions();
        processCreativeCategories();
        prepareTnsObjects();
    }

    private void processOptions() {
        creative.setOptions(new LinkedHashSet<CreativeOptionValue>(getOptionValues().values()));
        creative.setGroupStates(new LinkedHashSet<CreativeOptGroupState>(getGroupStateValues().values()));

        for (CreativeOptionValue optionValue : creative.getOptions())
            if (optionValue.getOption() != null && optionValue.getOption().getId() == null)
                optionValue.getOption().setId(optionValue.getOptionId());
    }

    private void processTagSizes() {
        creative.registerChange("sizeTypes", "tagSizes");
    }

    public String getCreativeName() {
        String creativeName = creative.getName();
        if (StringUtil.isPropertyEmpty(creativeName)) {
            creativeName = displayCreativeService.find(creative.getId()).getName();
        }
        return creativeName;
    }

    private void processCreativeCategories() {
        List<CreativeCategory> categories = unpackCategories();
        categories.addAll(unpackTags());
        creative.setCategories(new HashSet<CreativeCategory>(categories));
    }

    private List<CreativeCategory> unpackCategories() {
        List<CreativeCategory> categories = new LinkedList<CreativeCategory>();

        if (selectedVisualCategories != null) {
            for (Long categoryId : selectedVisualCategories) {
                CreativeCategory creativeCategory = new CreativeCategory(categoryId);
                creativeCategory.setType(CreativeCategoryType.VISUAL);
                categories.add(creativeCategory);
            }
        }
        if (selectedContentCategories != null) {
            for (Long categoryId : selectedContentCategories) {
                CreativeCategory creativeCategory = new CreativeCategory(categoryId);
                creativeCategory.setType(CreativeCategoryType.CONTENT);
                categories.add(creativeCategory);
            }
        }
        return categories;
    }

    private List<CreativeCategory> unpackTags() {
        List<CreativeCategory> categories = new LinkedList<CreativeCategory>();
        for (String tagName : selectedTags) {
            CreativeCategory tag = displayCreativeService.findCategory(CreativeCategoryType.TAG, tagName.toLowerCase(), true);
            if (tag == null) {
                tag = new CreativeCategory(null, tagName.toLowerCase());
                tag.setType(CreativeCategoryType.TAG);
                if (SecurityContext.isInternal()) {
                    tag.setQaStatus('A');
                } else {
                    tag.setQaStatus('H');
                }
            }
            categories.add(tag);
        }
        return categories;
    }

    public void setSelectedVisualCategories(Collection<Long> selectedVisualCategories) {
        this.selectedVisualCategories = selectedVisualCategories;
    }

    public void setSelectedContentCategories(Collection<Long> selectedContentCategories) {
        this.selectedContentCategories = selectedContentCategories;
    }

    public void setSelectedTags(Collection<String> selectedTags) {
        this.selectedTags = selectedTags;
    }

    @Override
    public void invalid() throws Exception {
        if (hasFieldErrors()) {
            if ((creative.getSize() != null) && (creative.getSize().getId() != null)) {
                creative.setSize(sizeService.findById(creative.getSize().getId()));
            }
            if ((creative.getTemplate() != null) && (creative.getTemplate().getId() != null)) {
                creative.setTemplate((CreativeTemplate) templateService.findById(creative.getTemplate().getId()));
            }
        }
    }

    @Override
    public Map<String, List<String>> getFieldErrors() {
        Map<String, List<String>> fieldErrors = super.getFieldErrors();

        if (fieldErrors.containsKey("options")) {
            LinkedHashSet<String> unique = new LinkedHashSet<String>(fieldErrors.get("options"));
            fieldErrors.put("options", new ArrayList<String>(unique));
        }

        return fieldErrors;
    }

    public CampaignCreative getCampaignCreative() {
        return campaignCreative;
    }

    public void setCampaignCreative(CampaignCreative campaignCreative) {
        this.campaignCreative = campaignCreative;
    }

    public Long getTnsBrandId() {
        return tnsBrandId;
    }

    public void setTnsBrandId(Long tnsBrandId) {
        this.tnsBrandId = tnsBrandId;
    }

}
