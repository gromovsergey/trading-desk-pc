package com.foros.action.campaign.campaignGroup;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.StringUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NamingConventionHelper {
    private static final String CHANNEL_MACRO_KEY = "campaign.group.target.macro.channel";

    private String accountName;
    private String campaignName;
    private String channelMacroName;
    private Date date;
    private List<String> predefinedNamingConventions;

    public NamingConventionHelper(String accountName, String campaignName) {
        this.accountName = accountName;
        this.campaignName = campaignName;
        this.channelMacroName = '<' + StringUtil.getLocalizedString(CHANNEL_MACRO_KEY) + '>';
        this.date = new Date();
    }

    public String getChannelMacroName() {
        return channelMacroName;
    }

    public List<String> getPredefinedNamingConventions() {
        if (predefinedNamingConventions == null) {
            predefinedNamingConventions = new ArrayList<>(4);

            predefinedNamingConventions.add(generateNamingConvention(false, false, true));
            predefinedNamingConventions.add(generateNamingConvention(true, false, true));
            predefinedNamingConventions.add(generateNamingConvention(true, true, false));
            predefinedNamingConventions.add(generateNamingConvention(false, true, false));
        }

        return predefinedNamingConventions;
    }

    public String getCustomizableNamingConvention() {
        return generateNamingConvention(true, false, false);
    }

    public boolean isNamingConventionValid(String customNamingConvention) {
        return StringUtil.isPropertyNotEmpty(customNamingConvention) && customNamingConvention.contains(channelMacroName);
    }

    public String getFinalName(String customNamingConvention, String channelName) {
        return customNamingConvention.replace(channelMacroName, channelName);
    }

    private String generateNamingConvention(boolean appendDate, boolean appendAccount, boolean appendCampaign) {
        StringBuilder result = new StringBuilder();
        if (appendCampaign) {
            result.append(campaignName);
            result.append('/');
        }
        if (appendAccount) {
            result.append(accountName);
            result.append('/');
        }
        if (appendDate) {
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, CurrentUserSettingsHolder.getLocale());
            result.append(df.format(date));
            result.append('/');
        }
        result.append(channelMacroName);
        return result.toString();
    }
}
