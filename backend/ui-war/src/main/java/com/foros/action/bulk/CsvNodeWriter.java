package com.foros.action.bulk;

import com.foros.model.EntityBase;

public interface CsvNodeWriter <T extends EntityBase> {

    void write(CsvRow row, T entity);
}
