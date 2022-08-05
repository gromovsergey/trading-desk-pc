package app.programmatic.ui.flight.dao.model.stat;

import app.programmatic.ui.common.model.MajorDisplayStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DisplayStatusAndFlightIdDto {
    Long flightId;
    MajorDisplayStatus status;
    String name;
}
