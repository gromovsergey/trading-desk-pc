package com.foros.session;

import javax.ejb.Local;

@Local
public interface RestrictionTestService {

    boolean check1(Long id);

    boolean check2(Long id);

    void test(Long id);

    void parameterizedTest(Long id);

    void restrictionNotFound(Long id);

}
