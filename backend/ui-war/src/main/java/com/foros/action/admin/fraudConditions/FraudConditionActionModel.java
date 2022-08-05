package com.foros.action.admin.fraudConditions;

import com.foros.action.IdNameBean;
import com.foros.model.admin.FraudCondition;
import com.foros.model.admin.FraudConditionType;
import com.foros.model.admin.GlobalParam;
import com.foros.util.StringUtil;

import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Conversion()
public class FraudConditionActionModel {
    private List<FraudConditionTO> fraudConditions = new ArrayList<FraudConditionTO>();

    private Long userInactivityTimeout;
    private Timestamp userInactivityTimeoutVersion;

    public FraudConditionActionModel() {}
    
    public FraudConditionActionModel(List<FraudCondition> fraudConditions, GlobalParam userInactivityTimeout) {
        this.userInactivityTimeout = Long.valueOf(userInactivityTimeout.getValue()) / 60;
        userInactivityTimeoutVersion = userInactivityTimeout.getVersion();

        for (FraudCondition fraudCondition : fraudConditions) {
            this.fraudConditions.add(new FraudConditionTO(fraudCondition));
        }
    }

    public List<FraudCondition> getUpdatedFraudConditions() {
        List<FraudCondition> result = new ArrayList<FraudCondition>(getFraudConditions().size());
        for (FraudConditionTO to : fraudConditions) {
            if (to != null) {
                result.add(to.toFraudCondition());
            }
        }
        return result;
    }

    public GlobalParam getUpdatedUserInactivityTimeout() {
        GlobalParam result = new GlobalParam();
        String value = getUserInactivityTimeout() != null ?
                       Long.toString(getUserInactivityTimeout() * 60) : null;
        result.setValue(value);
        result.setVersion(getUserInactivityTimeoutVersion());
        return result;
    }

    public List<FraudConditionTO> getFraudConditions() {
        return fraudConditions;
    }

    public void setFraudConditions(List<FraudConditionTO> fraudConditions) {
        this.fraudConditions = fraudConditions;
    }

    @TypeConversion(type = ConversionType.CLASS, converter = "com.foros.framework.conversion.LongNumberFormattingConverter")
    public Long getUserInactivityTimeout() {
        return userInactivityTimeout;
    }

    @TypeConversion(type = ConversionType.CLASS, converter = "com.foros.framework.conversion.LongNumberFormattingConverter")
    public void setUserInactivityTimeout(Long userInactivityTimeout) {
        this.userInactivityTimeout = userInactivityTimeout;
    }

    public Timestamp getUserInactivityTimeoutVersion() {
        return userInactivityTimeoutVersion;
    }

    public void setUserInactivityTimeoutVersion(Timestamp userInactivityTimeoutVersion) {
        this.userInactivityTimeoutVersion = userInactivityTimeoutVersion;
    }

    public List<IdNameBean> getFraudConditionsUnits() {
        List<IdNameBean> res = new ArrayList<IdNameBean>(3);
        res.add(new IdNameBean(FraudConditionTO.UNITS_SECONDS, StringUtil.getLocalizedString("form.select.second")));
        res.add(new IdNameBean(FraudConditionTO.UNITS_MINUTES, StringUtil.getLocalizedString("form.select.minute")));
        res.add(new IdNameBean(FraudConditionTO.UNITS_HOURS, StringUtil.getLocalizedString("form.select.hour")));
        return res;
    }

    public List<IdNameBean> getFraudConditionsTypes() {
        List<IdNameBean> res = new ArrayList<IdNameBean>(2);
        res.add(new IdNameBean(FraudConditionType.CLK.name(), StringUtil.getLocalizedString("fraudCondition.clicks")));
        res.add(new IdNameBean(FraudConditionType.IMP.name(), StringUtil.getLocalizedString("fraudCondition.impressions")));
        return res;
    }
}
