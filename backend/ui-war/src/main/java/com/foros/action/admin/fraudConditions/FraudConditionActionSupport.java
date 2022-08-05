package com.foros.action.admin.fraudConditions;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.model.admin.FraudCondition;
import com.foros.model.admin.GlobalParam;
import com.foros.session.admin.fraudConditions.FraudConditionsService;

import javax.ejb.EJB;
import java.util.List;

public abstract class FraudConditionActionSupport extends BaseActionSupport implements ModelDriven<FraudConditionActionModel> {
    protected FraudConditionActionModel fraudConditionModel = new FraudConditionActionModel();

    @EJB
    protected FraudConditionsService fraudConditionsService;

    @Override
    public FraudConditionActionModel getModel() {
        return fraudConditionModel;
    }

    protected String doViewEdit() {
        GlobalParam userInactivityTimeout = fraudConditionsService.getUserInactivityTimeout();
        List<FraudCondition> fraudConditions = fraudConditionsService.findAll();

        fraudConditionModel = new FraudConditionActionModel(fraudConditions, userInactivityTimeout);

        return SUCCESS;
    }
}
