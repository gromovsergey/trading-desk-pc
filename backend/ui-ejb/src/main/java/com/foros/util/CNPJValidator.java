package com.foros.util;

import java.util.regex.Pattern;

public class CNPJValidator {
    private static final String COUNTRY_CODE_BR = "BR";
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/?\\d{4}-\\d{2}$");

    private CNPJValidator() {
    }

    // Valid value example: 03.847.655/0001-98
    public static boolean isValid(String countryCode, String cnpj) {
        if (!COUNTRY_CODE_BR.equals(countryCode) || StringUtil.isPropertyEmpty(cnpj)) {
            return true;
        }

        if (!CNPJ_PATTERN.matcher(cnpj).matches()) {
            return false;
        }

        return validateChecksum(convertToIntArray(cnpj));
    }

    private static boolean validateChecksum(int[] cnpj) {
        int v1, v2;

        v1 = 5 * cnpj[0] + 4 * cnpj[1] + 3 * cnpj[2] + 2 * cnpj[3];
        v1 += 9 * cnpj[4] + 8 * cnpj[5] + 7 * cnpj[6] + 6 * cnpj[7];
        v1 += 5 * cnpj[8] + 4 * cnpj[9] + 3 * cnpj[10] + 2 * cnpj[11];
        v1 = 11 - v1 % 11;

        if (v1 >= 10) {
            v1 = 0;
        }

        if (v1 != cnpj[12]) {
            return false;
        }

        v2 = 6 * cnpj[0] + 5 * cnpj[1] + 4 * cnpj[2] + 3 * cnpj[3];
        v2 += 2 * cnpj[4] + 9 * cnpj[5] + 8 * cnpj[6] + 7 * cnpj[7];
        v2 += 6 * cnpj[8] + 5 * cnpj[9] + 4 * cnpj[10] + 3 * cnpj[11];
        v2 += 2 * v1;
        v2 = 11 - v2 % 11;

        if (v2 >= 10) {
            v2 = 0;
        }

        return v2 == cnpj[13];
    }

    private static int[] convertToIntArray(String cnpj) {
        int[] result = new int[14];

        result[0] = cnpj.charAt(0) - '0';
        result[1] = cnpj.charAt(1) - '0';

        result[2] = cnpj.charAt(3) - '0';
        result[3] = cnpj.charAt(4) - '0';
        result[4] = cnpj.charAt(5) - '0';

        result[5] = cnpj.charAt(7) - '0';
        result[6] = cnpj.charAt(8) - '0';
        result[7] = cnpj.charAt(9) - '0';

        result[8] = cnpj.charAt(11) - '0';
        result[9] = cnpj.charAt(12) - '0';
        result[10] = cnpj.charAt(13) - '0';
        result[11] = cnpj.charAt(14) - '0';

        result[12] = cnpj.charAt(16) - '0';
        result[13] = cnpj.charAt(17) - '0';
        
        return result;
    }
}
