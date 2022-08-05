package com.foros.util;

import java.math.BigDecimal;

public class DecimalUtil {
    public static BigDecimal notNull(BigDecimal decimal) {
        return decimal == null ? BigDecimal.ZERO : decimal;
    }

    public static boolean isZeroOrNull(BigDecimal number) {
        return number == null || number.compareTo(BigDecimal.ZERO) == 0;
    }
}
