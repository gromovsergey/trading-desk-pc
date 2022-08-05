package com.foros.util.preview;

import com.foros.config.ConfigService;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.model.template.OptionValue;
import com.foros.session.ServiceLocator;
import com.foros.util.TemplateUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CreativeOptionValueSource implements OptionValueSource {

    private final Map<Long, OptionValue> optionValues = new HashMap<>();
    private final AdvertiserAccount account;

    public CreativeOptionValueSource(Creative creative) {
        this(creative.getAccount(), creative.getOptions());
    }

    public CreativeOptionValueSource(AdvertiserAccount account, Collection<? extends OptionValue> optionValues) {
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
        if (account != null) {
            ConfigService configService = ServiceLocator.getInstance().lookup(ConfigService.class);
            return TemplateUtil.getImagePath(configService, account);
        } else {
            return null;
        }
    }
}
