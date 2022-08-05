package app.programmatic.ui.flight.dao;

import org.springframework.data.repository.CrudRepository;
import app.programmatic.ui.flight.dao.model.FlightSchedule;

public interface FlightScheduleRepository extends CrudRepository<FlightSchedule, Long> {
}
