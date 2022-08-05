package app.programmatic.ui.restriction.tool;

import static app.programmatic.ui.restriction.model.Restriction.ACTIVATE_CAMPAIGN;
import static app.programmatic.ui.restriction.model.Restriction.INACTIVATE_CAMPAIGN;

import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.model.RestrictionCommandBuilder;
import app.programmatic.ui.restriction.service.RestrictionService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import app.programmatic.ui.restriction.view.RestrictionResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RunWith(SpringRunner.class)
public class RestrictionDefinitionTest extends Assert {

    private final TestRestrictionService trueRestrictionService = new TestRestrictionService(Boolean.TRUE);
    private final TestRestrictionService falseRestrictionService = new TestRestrictionService(Boolean.FALSE);
    private final TestRestrictionService switchRestrictionService = new TestRestrictionService(null);

    @Test
    public void test() {
        RestrictionDefinition definition = new RestrictionDefinition(ACTIVATE_CAMPAIGN, INACTIVATE_CAMPAIGN);

        List<RestrictionResponse> responses = definition.calcPredicates(Collections.singletonList(1l), trueRestrictionService);
        assertOneResponse(responses, true, 1l);

        responses = definition.calcPredicates(Collections.singletonList(1l), falseRestrictionService);
        assertOneResponse(responses, false, 1l);

        responses = definition.calcPredicates(Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l,7l, 8l, 9l, 10l), switchRestrictionService);
        assertEquals(10, responses.size());

        Long expectedId = 1l;
        for (RestrictionResponse response : responses) {
            assertEquals("Iteration " + expectedId, expectedId, response.getId());
            assertTrue("Definition size = 2, so switch should be 'true && false = false'. Iteration " + expectedId, !response.isAllowed());

            expectedId++;
        }
    }

    private void assertOneResponse(List<RestrictionResponse> responses, boolean result, Long id) {
        assertEquals(1, responses.size());
        assertEquals(id, responses.get(0).getId());
        assertTrue(responses.get(0).isAllowed() == result);
    }

    private class TestRestrictionService implements RestrictionService {
        private final boolean isSwitch;
        private boolean currentResult;

        public TestRestrictionService(Boolean result) {
            this.currentResult = result == null ? false : result;
            isSwitch = result == null;
        }

        private boolean getResult() {
            if (isSwitch) {
                currentResult = !currentResult;
            }
            return currentResult;
        }

        @Override
        public boolean isPermittedAll(RestrictionCommandBuilder builder) {
            return getResult();
        }

        @Override
        public List<Boolean> isPermitted(RestrictionCommandBuilder builder) {
            return builder.build().getRestrictionCommands().stream()
                    .map( r -> getResult() )
                    .collect(Collectors.toList());
        }

        @Override
        public void throwIfNotPermitted(RestrictionCommandBuilder builder) {}
        @Override
        public void throwIfNotPermitted(Restriction restriction) {}
        @Override
        public void throwIfNotPermitted(Restriction restriction, Long paramId) {}
        @Override
        public void throwIfNotCurrentUser(Long expectedUserId) {}
    }
}
