package com.foros.action.creative.display;

import com.foros.action.IdNameBean;
import com.foros.action.creative.CreativeActionSupport;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.LocalizableName;
import com.foros.model.Status;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.security.AccountType;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionType;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.template.OptionService;
import com.foros.util.EntityUtils;
import com.foros.util.LocalizableNameEntityHelper;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.StringUtil;
import com.foros.util.context.RequestContexts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

public abstract class EditCreativeActionBase extends CreativeActionSupport implements RequestContextsAware, BreadcrumbsSupport {
    @EJB
    protected CreativeSizeService sizeService;

    @EJB
    protected OptionService optionService;

    @EJB
    private CurrentUserService userService;

    @EJB
    private AccountTypeService accountTypeService;

    private List<IdNameBean> sizes;

    private List<EntityTO> templates;

    protected CreativeTemplate selectedTemplate;

    private Long campaignId;

    private List<Long> ccgId = new ArrayList<Long>();

    private List<IdNameBean> availableVisualCategories;

    private List<IdNameBean> availableContentCategories;

    @EJB
    protected CampaignCreativeGroupService creativeGroupService;

    @EJB
    protected CampaignCreativeService campaignCreativeService;

    @EJB
    private ConfigService configService;

    private Collection<SizeType> sizeTypes;

    private CampaignCreativeGroup existingGroup;

    public void setGroupStateValues(Map<Long, CreativeOptGroupState> groupStateValues) {
        this.groupStateValues = groupStateValues;
    }

    public List<IdNameBean> getSizes() {
        if (sizes == null) {
            sizes = new LinkedList<IdNameBean>();
            AccountType accountType = accountTypeService.findById(creative.getAccount().getAccountType().getId());
            List<CreativeSize> creativeSizes = sizeService.findByAccountType(accountType);
            CreativeSize existingDeletedSize = null;
            if (creative.getId() != null) {
                existingDeletedSize = displayCreativeService.find(creative.getId()).getSize();
            }
            for (CreativeSize creativeSize : creativeSizes) {
                if (!creativeSize.isText()) {
                    if (creativeSize.getStatus() == Status.DELETED && !creativeSize.equals(existingDeletedSize)) {
                        continue;
                    }
                    IdNameBean size = new IdNameBean();
                    size.setId(creativeSize.getId().toString());
                    size.setName(localizeSizeName(creativeSize));
                    sizes.add(size);
                }
            }
            Collections.sort(sizes, new Comparator<IdNameBean>() {
                @Override
                public int compare(IdNameBean o1, IdNameBean o2) {
                    return StringUtil.compareToIgnoreCase(o1.getName(), o2.getName());
                }
            });
        }
        return sizes;
    }

    private String localizeSizeName(CreativeSize creativeSize) {
        String name = LocalizableNameUtil.getLocalizedValue(creativeSize.getName());
        return EntityUtils.appendStatusSuffix(name, creativeSize.getStatus());
    }

    public List<EntityTO> getTemplates() {
        if (templates == null) {
            Long accountTypeId = creative.getAccount().getAccountType().getId();
            CreativeSize size = creative.getSize();
            Long sizeId = size != null ? size.getId() : null;
            templates = CreativeTemplateHelper.getTemplatesForSize(templateService, accountTypeId, sizeId);
            if (creative.getId() != null) {
                CreativeTemplate existingTemplate = displayCreativeService.find(creative.getId()).getTemplate();
                if (existingTemplate.getStatus() == Status.DELETED) {
                    String templateName = LocalizableNameUtil.getLocalizedValue(existingTemplate.getName());
                    templateName = EntityUtils.appendStatusSuffix(templateName, existingTemplate.getStatus());
                    templates.add(new EntityTO(existingTemplate.getId(), templateName, existingTemplate.getStatus().getLetter()));
                }
            }
        }
        return templates;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchToAdvertiser(creative.getAccount().getId());
    }

    public List<IdNameBean> getAvailableVisualCategories() {
        if (availableVisualCategories == null) {
            List<CreativeCategory> creativeCategories = displayCreativeService.findCategoriesByType(CreativeCategoryType.VISUAL);
            Collections.sort(creativeCategories, new LocalizableNameEntityComparator());
            availableVisualCategories = LocalizableNameEntityHelper.convertToIdNameBeans(creativeCategories);
        }
        return availableVisualCategories;
    }

    public List<IdNameBean> getAvailableContentCategories() {
        if (availableContentCategories == null) {
            List<CreativeCategory> creativeCategories = displayCreativeService.findCategoriesByType(CreativeCategoryType.CONTENT);
            Collections.sort(creativeCategories, new LocalizableNameEntityComparator());
            availableContentCategories = LocalizableNameEntityHelper.convertToIdNameBeans(creativeCategories);
        }
        return availableContentCategories;
    }

    public CreativeTemplate getSelectedTemplate() {
        if (selectedTemplate == null && getTemplateId() != null) {
            selectedTemplate = (CreativeTemplate) templateService.findById(getTemplateId());
        }
        return selectedTemplate;
    }

    public Collection<Option> getTemplateOptions() {
        return getSelectedTemplate().getAdvertiserOptions();
    }

    public Long getTemplateId() {
        if (creative == null || creative.getTemplate() == null) {
            return null;
        } else {
            return creative.getTemplate().getId();
        }
    }

    public String localizeLabel(LocalizableName label) {
        return LocalizableNameUtil.getLocalizedValue(label, true);
    }

    public String getDefaultValue(Option option) {
        String value = null;
        if (option.getType() == OptionType.ENUM) {
            for (OptionEnumValue enumValue : option.getValues()) {
                if (enumValue.isDefault()) {
                    value = enumValue.getValue();
                }
            }
        } else {
            value = option.getDefaultValue();
        }
        return value;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = null;
        if (creative.getId() != null) {
            Creative persistent = displayCreativeService.find(creative.getId());
            breadcrumbs = new Breadcrumbs().add(new CreativeBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        }
        return breadcrumbs;
    }

    public Collection<SizeType> getSizeTypes() {
        if (sizeTypes == null) {
            sizeTypes = campaignCreativeService.findSizeTypesForEditByAccountId(creative.getAccount().getId());
        }
        return sizeTypes;
    }

    public List<Long> getCcgId() {
        return ccgId;
    }

    public String getCcgIds() {
        String result = "";
        for (Long id : ccgId) {
            result += "&ccgId=" + id;
        }
        return result;
    }

    public void setCcgId(List<Long> ccgId) {
        this.ccgId = ccgId;
    }

    public List<Long> getIds() {
        return ccgId;
    }

    public void setIds(List<Long> ids) {
        this.ccgId = ids;
    }

    private CampaignCreativeGroup getExistingGroup() {
        if (existingGroup == null) {
            existingGroup = creativeGroupService.find(getCcgId().get(0));
        }
        return existingGroup;
    }

    public boolean canUpdateCapsAndWeight() {
        return CCGType.TEXT != getExistingGroup().getCcgType() || userService.isInternal();
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public List<Long> getTemplateIds() {
        return templateService.findIdsByDefaultNames(configService.detach().get(ConfigParameters.YANDEX_TEMPLATE_NAMES));

    }
}
