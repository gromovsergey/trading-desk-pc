package com.foros.util.unixcommons;

public class TriggerNormalization {

    public static String normalizeKeyword(String countryCode, String keyword) {
        keyword = keyword.trim();

        try {
            return doNormalizeKeyword(countryCode, keyword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String doNormalizeKeyword(String countryCode, String original) throws Exception {
        if (original == null) {
            throw new NullPointerException("Original keyword should not be null!");
        }

        String normalizedKeyword = CommonToolsInstance.get().normalizeKeyword(original);
        if (!original.isEmpty() && (normalizedKeyword == null || normalizedKeyword.isEmpty())) {
            throw new RuntimeException("Normalized keyword should not be empty!");
        }
        return normalizedKeyword;
    }
}
