package com.foros.action.creative.display;

import com.foros.config.ConfigService;
import com.foros.framework.ReadOnly;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.session.account.AccountService;
import com.foros.session.creative.CreativePreviewService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.LivePreviewHelper;
import com.foros.session.creative.LivePreviewResult;
import com.foros.session.template.OptionService;
import com.foros.session.template.TemplateService;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.ejb.EJB;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class LivePreviewCreativeGeneratorAction implements ModelDriven<Creative> {

    @EJB
    private CreativePreviewService previewService;

    @EJB
    private AccountService accountService;

    @EJB
    private OptionService optionService;

    @EJB
    private CreativeSizeService creativeSizeService;

    @EJB
    private TemplateService templateService;

    @EJB
    private ConfigService configService;

    private Creative creative;
    private Map<Long, CreativeOptionValue> optionValues = new HashMap<>();
    private Long previewHeight;
    private Long previewWidth;
    private String previewUrl;

    @ReadOnly
    public String process() {
        if (LivePreviewHelper.isPreviewPossible(creative)) {
            creative.setOptions(new LinkedHashSet<>(getOptionValues().values()));
            LivePreviewResult result = LivePreviewHelper.prepareCreative(creative,
                    configService, accountService, optionService, creativeSizeService, templateService, previewService);
            previewHeight = result.getHeight();
            previewWidth = result.getWidth();
            previewUrl = result.getUrl();
        }
        return Action.SUCCESS;
    }

    public void setCreative(Creative creative) {
        this.creative = creative;
    }

    @Override
    public Creative getModel() {
        if (creative == null) {
            creative = new Creative();
        }
        return creative;
    }

    public Map<Long, CreativeOptionValue> getOptionValues() {
        return optionValues;
    }

    public void setOptionValues(Map<Long, CreativeOptionValue> optionValues) {
        this.optionValues = optionValues;
    }

    public Long getPreviewHeight() {
        return previewHeight;
    }

    public Long getPreviewWidth() {
        return previewWidth;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }
}
