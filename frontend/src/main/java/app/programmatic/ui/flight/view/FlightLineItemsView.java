package app.programmatic.ui.flight.view;

import java.util.List;

public class FlightLineItemsView {
    private FlightView flightView;
    private List<LineItemView> lineItemsView;

    public FlightLineItemsView(FlightView flightView, List<LineItemView> lineItemsView) {
        this.flightView = flightView;
        this.lineItemsView = lineItemsView;
    }

    public FlightView getFlightView() {
        return flightView;
    }

    public List<LineItemView> getLineItemsView() {
        return lineItemsView;
    }
}
