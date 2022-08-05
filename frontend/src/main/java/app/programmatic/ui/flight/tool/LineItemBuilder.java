package app.programmatic.ui.flight.tool;

import app.programmatic.ui.ccg.dao.model.CcgDisplayStatus;
import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.LineItem;

import java.util.Collections;
import java.util.EnumSet;
import java.util.stream.Collectors;


public class LineItemBuilder {
    public static final String DEFAULT_NAME = MessageInterpolator.getDefaultMessageInterpolator().interpolate(
            "flight.defaultLineItem.name") + " %d";

    public static LineItem defaultLineItem(Flight flight) {
        LineItem result = new LineItem();
        result.setFlightId(flight.getId());
        result.setName(buildDefaultName(result));
        result.setDisplayStatus(CcgDisplayStatus.INACTIVE);
        result.setDeviceChannelIds(Collections.emptyList());
        result.setSpecialChannelLinked(false);
        result.setAccountId(flight.getAccountId());

        result.setPropertiesSource(EffectiveLineItemTool.buildPropsSource(
            EnumSet.allOf(ConfigurableLineItemProperty.class).stream()
                .map( e -> e.getName() )
                .collect(Collectors.toList())
        ));

        return result;
    }

    public static String buildDefaultName(LineItem lineItem) {
        return String.format(DEFAULT_NAME, lineItem.getFlightId());
    }
}
