package com.foros.model.channel.placementsBlacklist;

import com.foros.annotations.Audit;
import com.foros.annotations.Auditable;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.changes.inspection.changeNode.custom.CollectionStringValueChange;
import com.foros.model.Identifiable;

import java.util.Collections;
import java.util.List;

@Auditable
@Audit(nodeFactory = PlacementsBlacklistChange.Factory.class)
public class PlacementsBlacklistWrapper implements Identifiable {

    private Long id;
    private List<PlacementBlacklist> oldPlacements = Collections.emptyList();

    @ChangesInspection(type = InspectionType.DEFAULT)
    @Audit(nodeFactory = CollectionStringValueChange.Factory.class)
    private List<PlacementBlacklist> placements;

    public PlacementsBlacklistWrapper(Long countryId) {
        id = countryId;
    }

    public List<PlacementBlacklist> getPlacements() {
        return placements;
    }

    public void setPlacements(List<PlacementBlacklist> placements) {
        this.placements = placements;
    }

    public List<PlacementBlacklist> getOldPlacements() {
        return oldPlacements;
    }

    public void setOldPlacements(List<PlacementBlacklist> oldPlacements) {
        this.oldPlacements = oldPlacements;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
