package app.programmatic.ui.flight.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.dao.model.SpecialChannelIdProjection;

import java.util.List;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

public interface LineItemRepository extends CrudRepository<LineItem, Long> {
    List<LineItem> findByFlightId(Long flightId);

    @EntityGraph(value = "LineItem.specialChannelId", type = LOAD)
    List<SpecialChannelIdProjection> findSpecialChannelIdBySpecialChannelIdIn(Iterable<Long> specialChannelIds);

    long countBySpecialChannelIdOrChannelIds(Long specialChannelId, Long channelId);
}
