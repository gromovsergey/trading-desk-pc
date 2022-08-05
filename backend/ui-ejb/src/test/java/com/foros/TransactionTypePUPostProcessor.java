package com.foros;

import javax.persistence.spi.PersistenceUnitTransactionType;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

public class TransactionTypePUPostProcessor implements PersistenceUnitPostProcessor {
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        pui.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
    }
}
