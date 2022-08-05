package com.foros.action.xml.generator;

import com.foros.model.Country;
import com.foros.session.ServiceLocator;
import com.foros.session.admin.country.CountryService;
import com.foros.util.xml.generator.XmlGenerator;

public class CountryInfoGenerator implements Generator<String> {

    public String generate(String code) {
        XmlGenerator generator = new XmlGenerator();

        CountryService countryService = ServiceLocator.getInstance().lookup(CountryService.class);
        Country country = countryService.find(code);
        generator.
                root("country").
                childs().
                tag("currency").text(country.getCurrency() != null ? country.getCurrency().getId() : "").
                tag("timezone").text(country.getTimezone() != null ? country.getTimezone().getId() : "").
                tag("language").text(country.getLanguage() !=null ? country.getLanguage() : "");
        return generator.asString();
    }
}