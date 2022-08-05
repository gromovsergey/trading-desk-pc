package com.foros.session.campaign;

import com.foros.model.FrequencyCap;
import com.foros.model.campaign.CampaignCreative;

import java.util.Comparator;

import org.apache.commons.lang.ObjectUtils;

public class CampaignCreativeComparator implements Comparator<CampaignCreative> {

    @Override
    public int compare(CampaignCreative oldCC, CampaignCreative newCC) {
        if (oldCC == newCC) {
            return 0;
        }
        if ((oldCC == null) || (newCC == null)) {
            return -1;
        }
        boolean isWeightEquals = ObjectUtils.equals(oldCC.getWeight(), newCC.getWeight());
        boolean isFrequencyCapEquals = compareFrequencyCap(oldCC.getFrequencyCap(), newCC.getFrequencyCap());
        boolean equals = isWeightEquals && isFrequencyCapEquals;
        return equals ? 0 : -1;
    }

    static boolean compareFrequencyCap(FrequencyCap oldFrequencyCap, FrequencyCap newFrequencyCap) {
        if (oldFrequencyCap == newFrequencyCap) {
            return true;
        }
        if ((oldFrequencyCap == null) || (newFrequencyCap == null)) {
            return false;
        }
        boolean isFrequencyCapEquals = ObjectUtils.equals(oldFrequencyCap.getPeriod(), newFrequencyCap.getPeriod())
                && ObjectUtils.equals(oldFrequencyCap.getLifeCount(), newFrequencyCap.getLifeCount())
                && ObjectUtils.equals(oldFrequencyCap.getWindowLength(), newFrequencyCap.getWindowLength())
                && ObjectUtils.equals(oldFrequencyCap.getWindowCount(), newFrequencyCap.getWindowCount());
        return isFrequencyCapEquals;
    }
}
