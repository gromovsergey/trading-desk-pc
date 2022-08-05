package com.foros.action.creative;

import com.foros.action.BaseActionSupport;
import com.foros.action.IdNameBean;
import com.foros.config.ConfigService;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.TextCreativeOption;
import com.foros.model.template.Option;
import com.foros.model.template.Template;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalService;
import com.foros.session.template.TemplateService;
import com.foros.session.textad.TextAdImageUtil;
import com.foros.util.LocalizableNameEntityHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class CreativeActionSupport extends BaseActionSupport implements ModelDriven<Creative> {
    @EJB
    protected DisplayCreativeService displayCreativeService;

    @EJB
    protected SiteCreativeApprovalService siteCreativeApprovalService;

    @EJB
    protected TemplateService templateService;

    protected Creative creative = new Creative();

    protected Map<Long, CreativeOptGroupState> groupStateValues;

    @EJB
    private ConfigService configService;

    private String imageFileId;

    public String getEntityType() {
        return "Creative";
    }

    @Override
    public Creative getModel() {
        return creative;
    }

    private List<IdNameBean> visualCategories;

    private List<IdNameBean> contentCategories;

    private List<IdNameBean> tags;

    private Map<Long, CreativeOptionValue> optionValues;

    public String getImageFileId() {
        if (imageFileId == null) {
            for (Option option : templateService.findTextTemplate().getAdvertiserOptions()) {
                if (TextCreativeOption.IMAGE_FILE.getToken().equals(option.getToken())) {
                    imageFileId = option.getId().toString();
                }
            }
        }
        return imageFileId;
    }

    public Collection<IdNameBean> getVisualCategories() {
        arrangeCategories();
        return visualCategories;
    }

    public Collection<IdNameBean> getContentCategories() {
        arrangeCategories();
        return contentCategories;
    }

    public Collection<IdNameBean> getTags() {
        arrangeCategories();
        return tags;
    }

    public Map<Long, CreativeOptionValue> getOptionValues() {
        if (optionValues == null) {
            loadOptionValues();
        }
        return optionValues;
    }

    public void setOptionValues(Map<Long, CreativeOptionValue> wdOptionValues) {
        this.optionValues = wdOptionValues;
    }

    public void loadOptionValues() {
        optionValues = new HashMap<Long, CreativeOptionValue>();
        for (CreativeOptionValue v : creative.getOptions()) {
            if (getModel().isTextCreative() && getImageFileId().equals(v.getOption().getId().toString())) {
                v.setValue(prepareTextImage(v.getValue(), getModel().getAccount()));
            }

            optionValues.put(v.getOptionId(), v);
        }
    }

    private void arrangeCategories() {
        Set<CreativeCategory> creativeCategories = creative.getCategories();
        if (creativeCategories != null) {
            List<CreativeCategory> visualCC = new LinkedList<CreativeCategory>();
            List<CreativeCategory> contentCC = new LinkedList<CreativeCategory>();
            List<CreativeCategory> tagsCC = new LinkedList<CreativeCategory>();
            for (CreativeCategory category : creativeCategories) {
                switch (category.getType()) {
                    case VISUAL:
                        visualCC.add(category);
                        break;
                    case CONTENT:
                        contentCC.add(category);
                        break;
                    default:
                        tagsCC.add(category);
                }
            }

            Template template = creative.getTemplate();
            if (template != null) {
                Set<CreativeCategory> templateCategories = creative.getTemplate().getCategories();
                if (templateCategories.size() > 0) {
                    visualCC = new ArrayList<CreativeCategory>(templateCategories);
                }
            }

            Comparator<LocalizableNameEntity> comparator = new LocalizableNameEntityComparator();

            Collections.sort(visualCC, comparator);
            Collections.sort(contentCC, comparator);
            Collections.sort(tagsCC, comparator);

            visualCategories = LocalizableNameEntityHelper.convertToIdNameBeans(visualCC);
            contentCategories = LocalizableNameEntityHelper.convertToIdNameBeans(contentCC);
            tags = LocalizableNameEntityHelper.convertToIdNameBeans(tagsCC);
        }
    }

    public Map<Long, CreativeOptGroupState> getGroupStateValues() {
        if (groupStateValues == null) {
            groupStateValues = new HashMap<Long, CreativeOptGroupState>();
            for (CreativeOptGroupState g : creative.getGroupStates()) {
                groupStateValues.put(g.getId().getOptionGroupId(), g);
            }
        }
        return groupStateValues;
    }

    protected String prepareTextImage(String imageName, AdvertiserAccount account) {
        String imageFile = null;
        if (imageName != null) {
            imageFile = TextAdImageUtil.getSourceFilePath(configService, account, imageName);
        }
        return imageFile;
    }

}
