package com.foros.tx;


import com.foros.model.Identifiable;

public class IdentityResetCallback implements TransactionCallback {
    private Identifiable entity;

    public IdentityResetCallback(Identifiable entity) {
        this.entity = entity;
    }

    @Override
    public void onBeforeCommit(){}

    @Override
    public void onCommit() {}

    @Override
    public void onRollback() {
        entity.setId(null);
    }
}
