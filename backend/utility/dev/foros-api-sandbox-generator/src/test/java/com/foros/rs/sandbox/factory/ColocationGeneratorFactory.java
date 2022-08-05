package com.foros.rs.sandbox.factory;

import com.foros.model.account.IspAccount;
import com.foros.model.isp.Colocation;
import com.foros.test.factory.ColocationTestFactory;

public class ColocationGeneratorFactory  extends BaseGeneratorFactory<Colocation, ColocationTestFactory> {

    private final IspAccount account;

    public ColocationGeneratorFactory(ColocationTestFactory colocationTestFactory, IspAccount account) {
        super(colocationTestFactory);
        this.account = account;
    }

    @Override
    protected Colocation createDefault() {
        return factory.create(account);
    }
}

