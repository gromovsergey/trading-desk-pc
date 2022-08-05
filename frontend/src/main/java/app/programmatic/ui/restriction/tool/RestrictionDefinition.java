package app.programmatic.ui.restriction.tool;

import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.model.RestrictionCommandBuilder;
import app.programmatic.ui.restriction.service.RestrictionService;
import app.programmatic.ui.restriction.view.RestrictionResponse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class RestrictionDefinition {
    private final List<Restriction> restrictions;

    public RestrictionDefinition(Restriction... restrictions) {
        this.restrictions = Arrays.asList(restrictions);
    }

    public List<RestrictionResponse> calcPredicates(List<Long> entityIds, RestrictionService restrictionService) {
        RestrictionCommandBuilder commandBuilder = new RestrictionCommandBuilder();
        entityIds.stream().forEach( id ->
                restrictions.stream().forEach( r -> commandBuilder.add(r, id))
        );

        RestrictionResponseBuilder responseBuilder = new RestrictionResponseBuilder(entityIds, restrictions.size());

        return restrictionService.isPermitted(commandBuilder).stream()
                .map( p -> responseBuilder.build(p) )
                .filter( r -> r != null )
                .collect(Collectors.toList());
    }

    public boolean contains(Restriction restriction) {
        return restrictions.contains(restriction);
    }

    private class RestrictionResponseBuilder {
        private final Iterator<Long> idsIterator;
        private final int groupSize;
        private int iteration;
        private Long curId;
        private Boolean curPredicate;

        public RestrictionResponseBuilder(List<Long> entityIds, int groupSize) {
            this.idsIterator = entityIds.iterator();
            this.groupSize = groupSize;
            initIteration();
        }

        public RestrictionResponse build(Boolean predicate) {
            curPredicate = curPredicate && predicate;

            if (iteration == groupSize) {
                RestrictionResponse result = new RestrictionResponse(curId, curPredicate);
                initIteration();
                return result;
            }

            iteration++;
            return null;
        }

        private void initIteration() {
            iteration = 1;
            if (idsIterator.hasNext()) {
                curId = idsIterator.next();
            }
            curPredicate = Boolean.TRUE;
        }
    }
}
