package com.foros.action.admin.behavioralParameters;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.session.admin.behavioralParameters.BehavioralParamsListService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ejb.EJB;

public class BehavioralParamsListActionSupport extends BaseActionSupport implements ModelDriven<BehavioralParametersList> {
    @EJB
    protected BehavioralParamsListService behavioralParamsListService;

    protected BehavioralParametersList list = new BehavioralParametersList();

    @Override
    public BehavioralParametersList getModel() {
        return list;
    }

    protected void sortBehavioralParameters(List<BehavioralParameters> bparams) {
        Collections.sort(bparams, new Comparator<BehavioralParameters>() {
            @Override
            public int compare(BehavioralParameters o1, BehavioralParameters o2) {
                int resType = o1.getTriggerType().compareTo(o2.getTriggerType());
                if (resType != 0) {
                    return resType;
                }

                int resFrom = o1.getTimeFrom().compareTo(o2.getTimeFrom());
                if (resFrom != 0) {
                    return resFrom;
                }

                return o1.getTimeTo().compareTo(o2.getTimeTo());
            }
        });
    }

}

