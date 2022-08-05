package com.foros.action.campaign.bulk;

import com.foros.action.bulk.CsvNodeWriter;
import com.foros.action.bulk.CsvRow;
import com.foros.model.EntityBase;

public interface CombiningCsvNodeWriter<T extends EntityBase> extends CsvNodeWriter<T> {

    void write(CsvRow row, T entity);

    void write(CsvRow row, T entity, T entity2);
}
