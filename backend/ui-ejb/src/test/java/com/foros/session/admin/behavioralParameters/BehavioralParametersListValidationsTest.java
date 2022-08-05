package com.foros.session.admin.behavioralParameters;

import com.foros.AbstractValidationsTest;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.test.factory.BehavioralParamsTestFactory;
import com.foros.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ejb.EJB;
import java.util.List;

import group.Db;
import group.Validation;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Validation.class })
public class BehavioralParametersListValidationsTest extends AbstractValidationsTest {
    @EJB
    private ValidationService validationService;

    @Autowired
    private BehavioralParamsTestFactory behavioralParamsTF;

    @Test
    public void testValidateCreate() throws Exception {
        BehavioralParametersList paramsList = behavioralParamsTF.create();
        validate("BehavioralParamsList.create", paramsList);
        assertEquals(0, violations.size());

        // Validating timeTo for triggerType != 0
        populateParameterToList(paramsList, 0L, 0L);
        validate("BehavioralParamsList.create", paramsList);
        assertHasNoViolation("behavioralParameters[0]");

        // Validating if timeFrom > timeTo
        populateParameterToList(paramsList, 120L, 60L);
        validate("BehavioralParamsList.create", paramsList);
        assertHasViolation("behavioralParameters[0]");

        // Validating if timeFrom and timeTo lieing in same boundries
        populateParameterToList(paramsList, 60L, 3660L);
        validate("BehavioralParamsList.create", paramsList);
        assertHasViolation("behavioralParameters[0].timeTo");

        // Validating if timeFrom < timeTo
        populateParameterToList(paramsList, 60L, 120L);
        validate("BehavioralParamsList.create", paramsList);
        assertHasNoViolation("behavioralParameters[0]");

        // Valiating for atleast one Behavioural Parameter is added
        removeParameterFromList(paramsList, getBehavioralParameters(paramsList));
        validate("BehavioralParamsList.create", paramsList);
        assertHasViolation("params");
    }

    @Test
    public void testValidateUpdate() throws Exception {
        BehavioralParametersList paramsList = behavioralParamsTF.createPersistent();

        // Validating timeTo for triggerType != 0
        populateParameterToList(paramsList, 0L, 0L);
        validate("BehavioralParamsList.update", paramsList);
        assertHasNoViolation("behavioralParameters[0]");
        assertHasNoViolation("params");

        // Validating if timeFrom > timeTo
        populateParameterToList(paramsList, 120L, 60L);
        validate("BehavioralParamsList.update", paramsList);
        assertHasViolation("behavioralParameters[0]");

        // Validating if timeFrom and timeTo lieing in same boundries
        populateParameterToList(paramsList, 60L, 3660L);
        validate("BehavioralParamsList.update", paramsList);
        assertHasViolation("behavioralParameters[0].timeTo");

        // Validating if timeFrom < timeTo
        populateParameterToList(paramsList, 60L, 120L);
        validate("BehavioralParamsList.update", paramsList);
        assertHasNoViolation("behavioralParameters[0]");

        // Valiating for atleast one Behavioural Parameter is added
        removeParameterFromList(paramsList, getBehavioralParameters(paramsList));
        validate("BehavioralParamsList.update", paramsList);
        assertHasViolation("params");
    }

    private void populateParameterToList(BehavioralParametersList paramsList, Long timeFrom, Long timeTo) {
        BehavioralParameters parameter = getBehavioralParameters(paramsList);
        removeParameterFromList(paramsList, parameter);
        populateParameterTimeValues(parameter, timeFrom, timeTo);
        addParameterToList(paramsList, parameter);
    }

    private BehavioralParameters getBehavioralParameters(BehavioralParametersList paramsList) {
        List<BehavioralParameters> parametersList = paramsList.getBehavioralParameters();
        return parametersList.get(0);
    }

    private void populateParameterTimeValues(BehavioralParameters parameter, Long timeFrom, Long timeTo) {
        parameter.setTimeFrom(timeFrom);
        parameter.setTimeTo(timeTo);
    }

    private void removeParameterFromList(BehavioralParametersList paramsList, BehavioralParameters parameter) {
        paramsList.getBehavioralParameters().remove(parameter);
    }

    private void addParameterToList(BehavioralParametersList paramsList, BehavioralParameters parameter) {
        addListToParameter(paramsList, parameter);
        paramsList.getBehavioralParameters().add(parameter);
    }

    private void addListToParameter(BehavioralParametersList paramsList, BehavioralParameters parameter) {
        parameter.setParamsList(paramsList);
    }
}
