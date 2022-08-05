package com.foros.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VATValidator {
    private static final String COUNTRY_CODE_GB = "GB";

    private static final Pattern GB_VAT_PATTERN_13 = Pattern.compile("^GB[0-9]{3}[ ][0-9]{4}[ ][0-9]{2}$");
    private static final Pattern GB_VAT_PATTERN_16 = Pattern.compile("^GB[0-9]{3}[ ][0-9]{4}[ ][0-9]{2}[ ][0-9]{3}$");

    private VATValidator() {
        
    }

    public static boolean isValid(String countryCode, String vat) {
        if (!COUNTRY_CODE_GB.equals(countryCode) || StringUtil.isPropertyEmpty(vat)) {
            return true;
        }

        Matcher matcher13 = GB_VAT_PATTERN_13.matcher(vat);
        Matcher matcher16 = GB_VAT_PATTERN_16.matcher(vat);

        if (!matcher13.matches() && !matcher16.matches()) {
            return false;
        }

        int [] multipliers = {8, 7, 6, 5, 4, 3, 2};
        int totalSum = 0;

        vat = vat.replaceAll(" ", "").substring(2);

        for (int i = 0; i < 7; i++) {
            totalSum += Integer.parseInt(vat.substring(i, i + 1)) * multipliers[i];
        }

        int modValue = (totalSum % 97);
        int eighthNinthDigits = Integer.parseInt(vat.substring(7, 9));

        return (eighthNinthDigits == (97 - modValue));
    }
}
