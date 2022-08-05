package app.programmatic.ui.flight.tool;

import com.foros.rs.client.model.advertising.campaign.DeliveryPacing;
import com.foros.rs.client.model.advertising.campaign.FrequencyCap;


public class BaseBuilder {

    static FrequencyCap convertFrequencyCap(app.programmatic.ui.flight.dao.model.FrequencyCap source, FrequencyCap existing) {
        FrequencyCap result = new FrequencyCap();
        result.setId(existing == null ? null : existing.getId());
        if (source == null) {
            return result;
        }

        result.setLifeCount(toLong(source.getLifeCount()));
        result.setPeriod(source.getPeriod());
        result.setWindowCount(toLong(source.getWindowCount()));
        result.setWindowLength(source.getWindowLength());

        return result;
    }

    static DeliveryPacing convertDeliveryPacing(app.programmatic.ui.flight.dao.model.DeliveryPacing src) {
        switch (src) {
            case U: return DeliveryPacing.UNRESTRICTED;
            case F: return DeliveryPacing.FIXED;
            case D: return DeliveryPacing.DYNAMIC;
        }

        throw new IllegalArgumentException("Unexpected flight Delivery Pacing: " + src);
    }

    private static Long toLong(Integer src) {
        if (src == null) {
            return null;
        }
        return Long.valueOf(src);
    }
}
