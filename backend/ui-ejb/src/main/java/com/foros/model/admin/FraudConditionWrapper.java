package com.foros.model.admin;

import com.foros.annotations.Auditable;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;

import java.util.ArrayList;
import java.util.List;

@Auditable
public class FraudConditionWrapper {

    @ChangesInspection(type = InspectionType.CASCADE)
    private GlobalParam userInactivityTimeOut;

    @ChangesInspection(type = InspectionType.CASCADE)
    private List<FraudCondition> fraudConditions = new ArrayList<FraudCondition>();

    public void setUserInactivityTimeOut(GlobalParam userInactivityTimeOut) {        
        this.userInactivityTimeOut = userInactivityTimeOut;
    }
    
    public GlobalParam getUserInactivityTimeOut() {
        return userInactivityTimeOut;
    }
    
    public void setFraudConditions(List<FraudCondition> fraudConditions) {
        this.fraudConditions = fraudConditions;
    }
    
    public List<FraudCondition> getFraudConditions() {
        return fraudConditions;
    }
 
}
