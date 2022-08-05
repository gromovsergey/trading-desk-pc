package com.foros.session.template;

import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.service.ByIdLocatorService;

import javax.ejb.Local;

@Local
public interface OptionService extends ByIdLocatorService<Option> {
    void create(Option option);

    Option update(Option option);

    Option findById(Long id);

    Option findByTokenFromTextTemplate(String token);

    OptionEnumValue findEnumValueById(Long id);

    OptionEnumValue findEnumValueByStringValue(Long optionId, String value);

    OptionEnumValue findDefaultEnumValue(Long optionId);

    Option createCopy(Long id);

    void remove(Long id);
}
