package com.foros.monitoring;

/**
 * Names visible for external clients through JMX/AMX
 */
public enum JmxBeanName {
    DATABASE_CHANGE_ADAPTER_NAME("DatabaseChangesChecking"),

    TEMPLATE_FILE_CHECKER_NAME("TemplateFileChecking"),

    DISCOVER_CUSTOMIZATION_FILE_CHECKER_NAME("DiscoverCustomizationFileChecking"),

    BULK_CAMPAIGN_RESULTS_CHECKER_NAME("BulkCampaignResultsChecking"),

    TEXT_AD_IMAGE_FILE_CHECKER_NAME("TextAdImageFileChecking"),

    TMP_CREATIVE_PREVIEW_CLEANER_NAME("TemporaryCreativePreviewCleaning"),

    YANDEX_TNS_ADVERTISER_CHECKER_NAME("YandexTnsAdvertiserChecking"),

    YANDEX_TNS_BRAND_CHECKER_NAME("YandexTnsBrandChecking");

    private final String value;

    JmxBeanName(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
