package app.programmatic.ui.flight.dao.model.stat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDashboardStatInnerDto {
    List<FlightDashboardStat> flightDashboardStats;
    Long flightDashboardStatCount;
    List<Long> flightIds;
    List<Long> lineItemIds;
}
