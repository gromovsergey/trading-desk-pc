package com.foros.jaxb.adapters;

import com.foros.model.ApproveStatus;
import com.foros.util.StringUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;


public class QaStatusAdapter extends XmlAdapter<String, ApproveStatus> {
    private static final String APPROVED = "APPROVED";
    private static final String DECLINED = "DECLINED";
    private static final String HOLD = "HOLD";

    private static final Map<String, ApproveStatus> STATUSES = initQaStatuses();
    private static final String EXPECTED_STATUSES = initExpectedQaStatuses();

    @Override
    public String marshal(ApproveStatus v) throws Exception {
        switch (v) {
            case APPROVED: return APPROVED;
            case DECLINED: return DECLINED;
            case HOLD: return HOLD;
        }
        throw new IllegalArgumentException(v + " is unexpected ApproveStatus");
    }

    @Override
    public ApproveStatus unmarshal(String v) throws Exception {
        ApproveStatus result = STATUSES.get(v);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException(StringUtil.getLocalizedString("errors.api.triggerQA.invalidStatus", v, EXPECTED_STATUSES));
    }

    private static Map<String, ApproveStatus> initQaStatuses() {
        Map<String, ApproveStatus> result = new HashMap<>(3);
        result.put(APPROVED, ApproveStatus.APPROVED);
        result.put(DECLINED, ApproveStatus.DECLINED);
        result.put(HOLD, ApproveStatus.HOLD);
        return result;
    }

    private static String initExpectedQaStatuses() {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (String status : STATUSES.keySet()) {
            if (!first) {
                result.append(", ");
            }
            first = false;
            result.append(status);
        }
        return result.toString();
    }
}
