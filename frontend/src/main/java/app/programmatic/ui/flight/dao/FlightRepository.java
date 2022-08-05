package app.programmatic.ui.flight.dao;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import app.programmatic.ui.flight.dao.model.CreativeIdsProjection;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.SiteIdsProjection;

public interface FlightRepository extends CrudRepository<Flight, Long> {

    @EntityGraph(value = "Flight.creativeIds", type = LOAD)
    CreativeIdsProjection findCreativeIdsById(Long flightId);

    @EntityGraph(value = "Flight.siteIds", type = LOAD)
    SiteIdsProjection findSiteIdsById(Long flightId);
}
