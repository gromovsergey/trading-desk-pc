package com.foros.test.factory;

import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.session.admin.behavioralParameters.BehavioralParamsListService;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class BehavioralParamsTestFactory extends TestFactory<BehavioralParametersList> {
    @EJB
    private BehavioralParamsListService behavioralParamsListService;

    public BehavioralParameters createBParam() {
        return createBParam(TriggerType.PAGE_KEYWORD);
    }

    public BehavioralParameters createBParam(TriggerType type) {
        BehavioralParameters bparam = new BehavioralParameters();
        bparam.setMinimumVisits(3L);
        bparam.setTriggerType(type.getLetter());
        bparam.setTimeFrom(0L);
        bparam.setTimeTo(360L);
        bparam.setWeight(1L);

        return bparam;
    }

    public BehavioralParameters createBParam(char triggerType, Long timeFrom, Long timeTo) {
        BehavioralParameters param = new BehavioralParameters();
        param.setMinimumVisits(10L);
        param.setTimeFrom(timeFrom);
        param.setTimeTo(timeTo);
        param.setTriggerType(triggerType);
        return param;
    }

    @Override
    public BehavioralParametersList create() {
        BehavioralParametersList bparamsList = new BehavioralParametersList();
        bparamsList.setName(getTestEntityRandomName());
        bparamsList.setThreshold(1L);
        BehavioralParameters param = createBParam();
        param.setParamsList(bparamsList);
        bparamsList.getBehavioralParameters().add(param);

        return bparamsList;
    }

    public BehavioralParametersList create(Collection<BehavioralParameters> bparams) {
        BehavioralParametersList bparamsList = new BehavioralParametersList();
        bparamsList.setName(getTestEntityRandomName());
        bparamsList.setThreshold(1L);

        for (BehavioralParameters bp : bparams) {
            bp.setParamsList(bparamsList);
        }
        bparamsList.getBehavioralParameters().addAll(bparams);

        return bparamsList;
    }

    @Override
    public BehavioralParametersList createPersistent() {
        BehavioralParametersList bparamsList = create();
        behavioralParamsListService.create(bparamsList);
        entityManager.flush();
        return bparamsList;
    }

    public BehavioralParametersList createPersistent(Collection<BehavioralParameters> bparams) {
        BehavioralParametersList bparamsList = create(bparams);
        behavioralParamsListService.create(bparamsList);
        entityManager.flush();
        return bparamsList;
    }

    @Override
    public void persist(BehavioralParametersList behavioralParametersList) {
        behavioralParamsListService.create(behavioralParametersList);
    }

    @Override
    public void update(BehavioralParametersList entity) {
        behavioralParamsListService.update(entity);
    }
}
