package com.foros.session.creative;

import com.foros.config.ConfigService;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.CreativeToken;
import com.foros.model.template.Option;
import com.foros.session.account.AccountService;
import com.foros.session.template.OptionService;
import com.foros.session.template.TemplateService;
import com.foros.session.textad.TextAdImageUtil;
import com.foros.util.StringUtil;


public class LivePreviewHelper {
    public static final long TEXT_HEIGHT = 90l;
    public static final long TEXT_WIDTH = 234l;

    public static LivePreviewResult prepareCreative(Creative creative, ConfigService configService, AccountService accountService,
            OptionService optionService, CreativeSizeService creativeSizeService, TemplateService templateService,
            CreativePreviewService previewService) {
        creative.setAccount((AdvertiserAccount)accountService.find(creative.getAccount().getId()));
        creative.setTemplate((CreativeTemplate) templateService.findById(creative.getTemplate().getId()));
        creative.setSize(creativeSizeService.findById(creative.getSize().getId()));

        String width = null;
        String height = null;
        for (CreativeOptionValue value : creative.getOptions()) {
            Option option = optionService.findById(value.getOption().getId());
            value.setOption(option);
            if (creative.isTextCreative() && CreativeToken.IMAGE_FILE.getName().equals(value.getOption().getToken())) {
                if (!StringUtil.isPropertyEmpty(value.getValue())) {
                    value.setValue(TextAdImageUtil.getResizedFilePath(configService, creative.getAccount(), value.getValue()));
                }
                continue;
            }


            if (CreativeToken.WIDTH.getName().equals(option.getToken())) {
                width = value.getValue();
            } else if (CreativeToken.HEIGHT.getName().equals(option.getToken())) {
                height = value.getValue();
            }
        }

        Long previewHeight;
        Long previewWidth;
        if (creative.isTextCreative()) {
            previewHeight = TEXT_HEIGHT;
            previewWidth = TEXT_WIDTH;
        } else if (width != null || height != null) {
            previewHeight = fetchOptionDimension(height);
            previewWidth = fetchOptionDimension(width);
        } else {
            previewHeight = fetchSizeDimension(creative.getSize().getHeight());
            previewWidth = fetchSizeDimension(creative.getSize().getWidth());
        }

        try {
            return new LivePreviewResult(
                    previewHeight,
                    previewWidth,
                    previewService.generateTemporaryPreview(creative)
            );
        } catch (Exception e) {
            LivePreviewResult result = new LivePreviewResult(previewHeight, previewWidth, null);
            throw new LivePreviewException(result, e);
        }
    }

    public static boolean isPreviewPossible(Creative creative) {
        return creative != null && creative.getSize() != null && creative.getSize().getId() != null &&
                creative.getTemplate() != null && creative.getTemplate().getId() != null;
    }

    private static Long fetchOptionDimension(String optionDimension) {
        try {
            return Long.valueOf(optionDimension);
        } catch (Exception e) {
            return 0l;
        }
    }

    private static Long fetchSizeDimension(Long sizeDimension) {
        return sizeDimension == null ? 0l : sizeDimension;
    }
}
