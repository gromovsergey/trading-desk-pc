package com.foros.test.factory;

import com.foros.model.admin.FraudCondition;
import com.foros.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

public class FraudConditionTestFactory extends TestFactory<FraudCondition> {
    @Override
    public FraudCondition create() {
        FraudCondition result = new FraudCondition();
        result.setLimit(Integer.valueOf(RandomUtil.getRandomInt(2, 1000)).longValue());
        result.setPeriod(Integer.valueOf(RandomUtil.getRandomInt(1, 24 * 60 * 60)).longValue());
        return result;
    }

    @Override
    public void persist(FraudCondition entity) {
        entityManager.persist(entity);
    }

    @Override
    public void update(FraudCondition entity) {
        entityManager.merge(entity);
    }

    @Override
    public FraudCondition createPersistent() {
        FraudCondition result = create();
        entityManager.persist(result);
        return result;
    }

    public List<FraudCondition> createConditions(int count) {
        List<FraudCondition> result = new ArrayList<FraudCondition>(count);
        for (int i = 0; i < count; i++) {
            result.add(createPersistent());
        }
        return result;
    }
}
