package com.foros.session.site;

import com.foros.config.ConfigService;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.site.Tag;
import com.foros.model.site.TagOptionValue;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.CreativeToken;
import com.foros.model.template.Option;
import com.foros.session.BusinessServiceBean;
import com.foros.session.creative.CreativePreviewOptions;
import com.foros.session.creative.CreativePreviewService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.template.OptionService;
import com.foros.session.template.TemplateService;
import com.foros.util.StringUtil;
import com.foros.util.preview.CreativeOptionValueSource;
import com.foros.util.preview.TagOptionValueSource;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.commons.io.IOUtils;

@Stateless(name = "TagsPreviewService")
public class TagsPreviewServiceBean extends BusinessServiceBean<Tag>  implements TagsPreviewService {
    public final static Integer MAX_ADS_PER_TAG_DEFAULT_VALUE = 1;

    private static enum PublisherOptions {
        HEADLINE_COLOR,
        DESCRIPTION_COLOR,
        URL_COLOR,
        BACKGROUND_COLOR,
        EXT_BORDER_COLOR,
        INT_BORDER_COLOR,
        MAX_ADS_PER_TAG,
        EXT_BORDER_SIZE,
        INT_BORDER_SIZE,
        TA_FONT,
        TA_FONT_SIZE,
        AD_FOOTER_ENABLED
    }

    @EJB
    private TemplateService templateService;

    @EJB
    private CreativeSizeService sizeService;

    @EJB
    private OptionService optionService;

    @EJB
    private ConfigService configService;

    @EJB
    private CreativePreviewService creativePreviewService;

    public TagsPreviewServiceBean() {
        super(Tag.class);
    }

    @Override
    public ByteArrayOutputStream getLivePreview(Tag tag) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            generatePreviewInternal(tag, output);
        } finally {
            IOUtils.closeQuietly(output);
        }
        return output;
    }

    private void generatePreviewInternal(Tag tag, OutputStream output) {
        CreativeTemplate template = templateService.findTextTemplate();
        int creativesCount = getCreativesCount(tag);

        CreativePreviewOptions previewOptions = new CreativePreviewOptions();
        previewOptions.setTagSource(new TagOptionValueSource(tag));
        previewOptions.setCreativeSources(getCreatives(template, creativesCount));
        previewOptions.setTemplateId(template.getId());
        previewOptions.setSizeId(tag.getOnlySize().getId());
        previewOptions.setAdFooterUrl(tag.getAccount().getCountry().getAdFooterURL());
        creativePreviewService.generatePreview(previewOptions, output);
    }

    private List<CreativeOptionValueSource> getCreatives(CreativeTemplate template, int creativesCount) {
        Collection<Option> options = template.getAdvertiserOptions();

        Map<String, String> byToken = new HashMap<>();
        byToken.put(CreativeToken.HEADLINE.getName(), StringUtil.getLocalizedString("site.tag.preview.headline"));
        byToken.put(CreativeToken.DESCRIPTION1.getName(), StringUtil.getLocalizedString("site.tag.preview.description1"));
        byToken.put(CreativeToken.DESCRIPTION2.getName(), StringUtil.getLocalizedString("site.tag.preview.description2"));
        byToken.put(CreativeToken.DESCRIPTION3.getName(), StringUtil.getLocalizedString("site.tag.preview.description3"));
        byToken.put(CreativeToken.DESCRIPTION4.getName(), StringUtil.getLocalizedString("site.tag.preview.description4"));
        byToken.put(CreativeToken.DISPLAY_URL.getName(), StringUtil.getLocalizedString("site.tag.preview.displayURL"));
        byToken.put(CreativeToken.CRCLICK.getName(), StringUtil.getLocalizedString("site.tag.preview.click"));

        ArrayList<CreativeOptionValue> optionValues = new ArrayList<CreativeOptionValue>(byToken.size());
        for (Option option : options) {
            String value = byToken.get(option.getToken());
            if (value != null) {
                CreativeOptionValue optionValue = new CreativeOptionValue();
                optionValue.setOption(option);
                optionValue.setValue(value);
                optionValues.add(optionValue);
            }
        }

        CreativeOptionValueSource optionValueSource = new CreativeOptionValueSource(null, optionValues);
        ArrayList<CreativeOptionValueSource> res = new ArrayList<>(creativesCount);
        for (int i = 0; i < creativesCount; i++) {
            res.add(optionValueSource);
        }
        return res;
    }

    private int getCreativesCount(Tag tag) {
        int creatives = MAX_ADS_PER_TAG_DEFAULT_VALUE;
        for (TagOptionValue optionValue : tag.getOptions()) {
            if (PublisherOptions.MAX_ADS_PER_TAG.name().equals(optionValue.getOption().getToken())) {
                try {
                    creatives = Integer.valueOf(optionValue.getValue());
                } catch (NumberFormatException e) {
                    // ignore
                }
                break;
            }
        }
        return creatives;
    }
}
