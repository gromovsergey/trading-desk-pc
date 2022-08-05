package app.programmatic.ui.flight.dao.model;

import java.util.Collections;
import java.util.List;

public class FlightLineItems {
    private Flight flight;
    private List<LineItem> lineItems;

    public FlightLineItems(Flight flight, LineItem lineItem) {
        this(flight, Collections.singletonList(lineItem));
    }

    public FlightLineItems(Flight flight, List<LineItem> lineItems) {
        this.flight = flight;
        this.lineItems = lineItems;
    }

    public Flight getFlight() {
        return flight;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }
}
