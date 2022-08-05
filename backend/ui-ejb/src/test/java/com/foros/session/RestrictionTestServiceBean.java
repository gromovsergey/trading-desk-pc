package com.foros.session;

import com.foros.restriction.annotation.Restrict;

import javax.ejb.Stateless;

@Stateless(name = "RestrictionTestService")
public class RestrictionTestServiceBean implements RestrictionTestService {

    @Override
    public boolean check1(Long id) {
        return id.equals(0L);
    }

    @Override
    public boolean check2(Long id) {
        return id.equals(0L);
    }

    @Override
    @Restrict(restriction = "TestEntity.test", parameters = "#id")
    public void test(Long id) {
        // do nothing
    }

    @Override
    @Restrict(restriction = "TestEntity.testParameterized", parameters = "#id")
    public void parameterizedTest(Long id) {
        // do nothing
    }

    @Override
    @Restrict(restriction = "TestEntity.nonexistentRestriction")
    public void restrictionNotFound(Long id) {
        // do nothing
    }

}
