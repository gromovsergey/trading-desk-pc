package com.foros.session.admin.fraudConditions;

import com.foros.model.admin.FraudCondition;
import com.foros.model.admin.GlobalParam;

import java.util.List;

import javax.ejb.Local;

@Local
public interface FraudConditionsService {

    List<FraudCondition> findAll();

    void update(GlobalParam userInactivityTimeOut, List<FraudCondition> fraudConditions);

    GlobalParam getUserInactivityTimeout();

}
