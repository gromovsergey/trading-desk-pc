package com.foros.action.creative.display;

import com.foros.action.IdNameBean;
import com.foros.framework.ReadOnly;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.CreativeSizeExpansion;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.Template;
import com.foros.session.UtilityService;
import com.foros.session.account.AccountService;
import com.foros.session.creative.CreativeSizeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

public class EditCreativeAction extends EditCreativeActionBase {

    @EJB
    private AccountService accountService;

    @EJB
    private CreativeSizeService creativeSizeService;

    @EJB
    private UtilityService utilityService;

    private Long advertiserId;

    private boolean showTemplateOptions = false;

    @ReadOnly
    public String create() {
        CampaignCreativeGroup creativeGroup = creativeGroupService.find(getCcgId().get(0));
        if (CampaignType.TEXT == creativeGroup.getCampaign().getCampaignType()) {
            createText();
        } else {
            createDisplay();
        }
        return SUCCESS;
    }

    @ReadOnly
    public String copy() {
        edit();
        String name = utilityService.calculateNameForCopy(creative, 100);
        creative.setName(name);
        clearIds();
        return SUCCESS;
    }

    private void clearIds() {
        creative.setId(null);
        creative.setVersion(null);
        for (CreativeOptionValue optionValue : creative.getOptions()) {
            optionValue.setId(null);
            optionValue.setVersion(null);
        }
        for (CreativeOptGroupState groupState : creative.getGroupStates()) {
            groupState.setId(null);
            groupState.setVersion(null);
        }
    }

    @ReadOnly
    public String createDisplay() {
        prepareAccount();
        return SUCCESS;
    }

    @ReadOnly
    public String createText() {
        prepareAccount();
        creative.setTemplate(templateService.findTextTemplate());
        creative.setSize(creativeSizeService.findTextSize());
        creative.setEnableAllAvailableSizes(true);
        return SUCCESS;
    }

    private void prepareAccount() {
        AdvertiserAccount advertiser = getAdvertiser();
        creative.setAccount(advertiser);
        creative.setCategories(accountService.loadCategories(advertiser));
    }

    private AdvertiserAccount getAdvertiser() {
        AdvertiserAccount advertiser;
        if (advertiserId == null) {
            advertiser = creativeGroupService.find(getCcgId().get(0)).getAccount();
        } else {
            advertiser = accountService.findAdvertiserAccount(advertiserId);
        }
        return advertiser;
    }



    @ReadOnly
    public String edit() {
        creative = displayCreativeService.view(creative.getId());
        loadOptionValues();
        if (creative.getTemplate() != null) {
            showTemplateOptions = creative.getTemplate().isExpandable();
        }
        return SUCCESS;
    }

    @Override
    public void loadOptionValues() {
        super.loadOptionValues();
        CreativeSize size = creative.getSize();
        CreativeTemplate template = creative.getTemplate();
        if (creative.getId() != null && template != null && size != null) {
            IdNameBean sizeTo = new IdNameBean(size.getId().toString());
            if (!getSizes().contains(sizeTo) && !creative.isTextCreative()) {
                creative.setSize(null);
                setOptionValues(new HashMap<Long, CreativeOptionValue>());
                return;
            }

            List<Template> availableTemplates = templateService.findJsHTMLTemplatesBySize(creative.getAccount().getAccountType().getId(), size.getId());
            if (!availableTemplates.contains(template) && !creative.isTextCreative()) {
                creative.setTemplate(null);
                Map<Long, CreativeOptionValue> values = getOptionValues();
                for (Option option : template.getAdvertiserOptions()) {
                    values.remove(option.getId());
                }
                setOptionValues(values);
            }
        }
    }

    @ReadOnly
    public String changeTemplate() {
        Long templateId = creative.getTemplate() == null ? null : creative.getTemplate().getId();
        Long sizeId = creative.getSize() == null ? null : creative.getSize().getId();

        boolean expandable = creative.isExpandable();
        CreativeSizeExpansion expansion = creative.getExpansion();

        reloadCreative();

        CreativeTemplate template;
        if (templateId != null) {
            template = (CreativeTemplate) templateService.findById(templateId);
            creative.setTemplate(template);
            if (template.isExpandable()) {
                creative.setExpandable(expandable);
            }
        }

        if (sizeId != null) {
            CreativeSize creativeSize = creativeSizeService.findById(sizeId);
            creative.setSize(creativeSize);
            if (expandable && creativeSize.getExpansions().contains(expansion)) {
                creative.setExpansion(expansion);
            } else {
                creative.setExpansion(null);
            }
        }

        return SUCCESS;
    }

    @ReadOnly
    public String changeSize() {
        Long sizeId = creative.getSize() == null ? null : creative.getSize().getId();
        CreativeTemplate selectedTemplate = getSelectedTemplate();

        if (sizeId != null) {
            boolean expandable = creative.isExpandable();
            CreativeSizeExpansion expansion = creative.getExpansion();
            reloadCreative();
            CreativeSize creativeSize = sizeService.findById(sizeId);
            creative.setSize(creativeSize);
            if (expandable && creativeSize.getExpansions().contains(expansion)) {
                creative.setExpansion(expansion);
            } else {
                creative.setExpansion(null);
            }

            if (selectedTemplate != null) {
                Long accountTypeId = creative.getAccount().getAccountType().getId();
                List<Template> availableTemplates = templateService.findJsHTMLTemplatesBySize(accountTypeId, sizeId);

                if (!availableTemplates.contains(selectedTemplate)) {
                    clearTemplate();
                } else if (selectedTemplate.isExpandable()) {
                    creative.setExpandable(expandable);
                }
            }
        } else {
            clearTemplate();
        }

        return SUCCESS;
    }

    @ReadOnly
    public String updateExpansion() {
        return SUCCESS;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public boolean isShowTemplateOptions() {
        return showTemplateOptions;
    }

    private void reloadCreative() {
        if (creative.getId() != null) {
            creative = displayCreativeService.view(creative.getId());
            setGroupStateValues(null);
            setOptionValues(null);
        }
    }

    private void clearTemplate() {
        selectedTemplate = null;
        creative.setTemplate(null);
    }



}
