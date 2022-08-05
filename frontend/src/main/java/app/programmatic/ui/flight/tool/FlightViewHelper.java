package app.programmatic.ui.flight.tool;

import app.programmatic.ui.common.model.RateType;

public class FlightViewHelper {

    public static String fromRateType(RateType rateType) {
        if (rateType == null) {
            return null;
        }

        return rateType.toString();
    }

    public static RateType toRateType(String rateType) {
        if (rateType == null) {
            return null;
        }

        return RateType.valueOf(rateType);
    }
}
