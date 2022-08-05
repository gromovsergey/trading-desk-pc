package com.foros.action.xml.generator;

import com.foros.model.account.Account;
import com.foros.util.xml.generator.XmlGenerator;

/**
 *
 * @author olga_glukhova
 */
public class AccountCountryGenerator implements Generator<Account> {

    public String generate(Account account) {
        XmlGenerator generator = new XmlGenerator();

        String countryCode = account.getCountry().getCountryCode();

        generator.root("countryCode").text(countryCode);

        return generator.asString();
    }
}