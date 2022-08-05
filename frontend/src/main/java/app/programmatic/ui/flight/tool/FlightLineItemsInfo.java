package app.programmatic.ui.flight.tool;

import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.service.LineItemService;

import java.util.List;

public class FlightLineItemsInfo {
    private final Long flightId;
    private final LineItemService service;

    private List<Long> lineItemIds;
    private LineItem defaultLineItem;


    public FlightLineItemsInfo(Long flightId, LineItemService service) {
        this.flightId = flightId;
        this.service = service;
        this.lineItemIds = service.fetchLineItemIds(flightId);
    }

    public boolean isDefaultLineItemExist() {
        return lineItemIds.size() == 1;
    }

    public LineItem getDefaultLineItem() {
        if (defaultLineItem == null) {
            if (!isDefaultLineItemExist()) {
                throw new IllegalStateException("Flight " + flightId + " have no default Line Item");
            }
            defaultLineItem = service.find(lineItemIds.iterator().next());
        }

        return defaultLineItem;
    }

    public Long getDefaultLineItemId() {
        if (!isDefaultLineItemExist()) {
            throw new IllegalStateException("Flight " + flightId + " have no default Line Item");
        }
        return lineItemIds.iterator().next();
    }
}
