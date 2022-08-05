package com.foros.util.preview;

import com.foros.config.ConfigService;
import com.foros.model.account.PublisherAccount;
import com.foros.model.site.Tag;
import com.foros.model.template.OptionValue;
import com.foros.session.ServiceLocator;
import com.foros.util.TemplateUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TagOptionValueSource implements OptionValueSource {

    private final Map<Long, OptionValue> optionValues = new HashMap<>();
    private final PublisherAccount account;

    public TagOptionValueSource(Tag tag) {
        this(tag.getAccount(), tag.getOptions());
    }

    public TagOptionValueSource(PublisherAccount account, Collection<? extends OptionValue> optionValues) {
        this.account = account;
        for (OptionValue optionValue : optionValues) {
            this.optionValues.put(optionValue.getOptionId(), optionValue);
        }
    }

    @Override
    public OptionValue get(Long optionId) {
        return this.optionValues.get(optionId);
    }

    @Override
    public String getImagesPath() {
        ConfigService configService = ServiceLocator.getInstance().lookup(ConfigService.class);
        return TemplateUtil.getImagePath(configService, account);
    }
}
