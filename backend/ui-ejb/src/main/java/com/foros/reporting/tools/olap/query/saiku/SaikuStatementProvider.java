package com.foros.reporting.tools.olap.query.saiku;

import com.phorm.oix.saiku.SaikuStatement;
import javax.ejb.Local;

@Local
public interface SaikuStatementProvider {

    SaikuStatement createStatement(String cubeName);

    void refresh();

}
