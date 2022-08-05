package app.programmatic.ui.flight.dao.dto;

import app.programmatic.ui.flight.dao.model.FlightPart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLineItemsPartDto {
    Long flightId;
    FlightPart flightPart;
    Long accountId;
}
