package com.foros.session.admin;

import com.foros.model.ClobParam;
import com.foros.model.ClobParamType;

import javax.ejb.Local;

@Local
public interface ClobParamService {
    void update(ClobParam param);

    ClobParam find(Long accountId, ClobParamType name);
}
