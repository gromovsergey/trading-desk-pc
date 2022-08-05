package com.foros.model.template;

import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.PublisherAccount;
import com.foros.model.creative.Creative;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.util.url.URLValidator;

public class OptionValueUtils {
    public static final String IMAGE_RESIZED_FOLDER = "~resized";
    public static final String HTML_FOLDER = "~html";

    public static String getFileStripped(OptionValue optionValue) {
        if (!isFile(optionValue)) {
            return null;
        }

        String fileRoot;
        Account account = optionValue.getAccount();
        if (account instanceof AdvertiserAccount) {
            fileRoot = OptionValueUtils.getAdvertiserRoot((AdvertiserAccount) account);
        } else if (account instanceof PublisherAccount) {
            fileRoot = OptionValueUtils.getPublisherRoot((PublisherAccount) account);
        } else {
            return optionValue.getValue();
        }

        String stripped = optionValue.getValue();
        if (stripped != null && stripped.startsWith(fileRoot)) {
            stripped = stripped.substring(fileRoot.length());
        }
        return stripped;
    }

    public static boolean isFile(OptionValue optionValue) {
        if (optionValue.getOption().getType() == OptionType.FILE || optionValue.getOption().getType() == OptionType.DYNAMIC_FILE) {
            return true;
        }

        if (optionValue.getOption().getType() == OptionType.FILE_URL &&
                !URLValidator.isValid(URLValidator.urlForValidate(optionValue.getValue()))) {
            return true;
        }

        return false;
    }

    public static String getAdvertiserRoot(AdvertiserAccount account) {
        if (account.isInAgencyAdvertiser()) {
            return "/" + account.getAgency().getId() + "/" + account.getId() + "/";
        } else {
            return "/" + account.getId() + "/";
        }
    }

    public static String getAdvertisingRoot(AdvertisingAccountBase account) {
        if (account instanceof AdvertiserAccount && ((AdvertiserAccount) account).isInAgencyAdvertiser()) {
            return "/" + ((AdvertiserAccount) account).getAgency().getId() + "/" + account.getId() + "/";
        } else {
            return "/" + account.getId() + "/";
        }
    }

    public static String getTextAdImagesRoot(Config config, AdvertiserAccount account) {
        return getAdvertiserRoot(account) + config.get(ConfigParameters.TEXT_AD_IMAGES_FOLDER) + "/";
    }


    public static String getTextAdImagesResizedRoot(Config config, AdvertiserAccount account) {
        return getAdvertiserRoot(account)
                + config.get(ConfigParameters.TEXT_AD_IMAGES_FOLDER) + "/" + IMAGE_RESIZED_FOLDER + "/";
    }

    public static String getHtmlRoot(Creative creative) {
        return getAdvertiserRoot(creative.getAccount()) + HTML_FOLDER + "/" + creative.getId() + "/";
    }

    public static String getPublisherRoot(PublisherAccount account) {
        return "/" + account.getId() + "/";
    }

    public static void prepareOptionValue(OptionValue option, Account account) {
        String optionValue = option.getValue();
        OptionType optionType = option.getOption().getType();

        String root = null;
        if (account instanceof PublisherAccount) {
            root = OptionValueUtils.getPublisherRoot((PublisherAccount) account);
        } else if (account instanceof AdvertiserAccount) {
            root = OptionValueUtils.getAdvertiserRoot((AdvertiserAccount) account);
        }

        if ((optionType == OptionType.FILE || optionType == OptionType.DYNAMIC_FILE) && StringUtil.isPropertyNotEmpty(optionValue)) {
            String fileName = root + optionValue;
            option.setValue(fileName);
        } else if (optionType == OptionType.FILE_URL && StringUtil.isPropertyNotEmpty(optionValue) &&
                !StringUtil.startsWith(optionValue, "http://", "https://", "//")) {
            String fileName = root + optionValue;
            option.setValue(fileName);
        } else if (StringUtil.isPropertyEmpty(optionValue)) {
            option.setValue(null);
        } else if (optionType == OptionType.INTEGER) {
            Long value = NumberUtil.parseLong(optionValue);
            option.setValue(value.toString());
        }
    }

    public static boolean isDefaultValue(OptionValue optionValue) {
        return isDefaultValue(optionValue.getOption(), optionValue.getValue());
    }

    public static boolean isDefaultValue(Option option, String value) {
        if (option.getDefaultValue() == null && value == null) {
            return true;
        }

        if (option.getDefaultValue() != null && value != null) {
            if (option.getType() == OptionType.COLOR && option.getDefaultValue().equalsIgnoreCase(value)) {
                return true;
            } else if (option.getDefaultValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
