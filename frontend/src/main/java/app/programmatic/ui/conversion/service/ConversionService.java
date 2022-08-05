package app.programmatic.ui.conversion.service;

import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.StatusOperation;
import app.programmatic.ui.conversion.dao.model.Conversion;

import java.util.List;

public interface ConversionService {
    List<Conversion> getConversions(Long accountId);

    Conversion find(Long conversionId);

    Long create(Conversion conversion);

    Long update(Conversion conversion);

    MajorDisplayStatus changeStatus(Long conversionId, StatusOperation operation);
}
