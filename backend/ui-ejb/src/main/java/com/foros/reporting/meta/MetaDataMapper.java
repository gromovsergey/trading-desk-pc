package com.foros.reporting.meta;

import com.foros.reporting.Row;
import java.util.List;

public interface MetaDataMapper {

    MetaData metaData(Row row);

    List<? extends MetaData> metaData();

}
