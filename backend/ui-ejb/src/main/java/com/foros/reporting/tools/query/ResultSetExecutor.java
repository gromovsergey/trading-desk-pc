package com.foros.reporting.tools.query;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlProvider;

public interface ResultSetExecutor extends SqlProvider {

    void execute(JdbcTemplate template, List<? extends SqlParameterValue> parameters, Adjuster adjuster, ResultSetExtractor extractor);

    List<? extends SqlParameter> getDeclaredParameters();

    boolean hasDeclaredParameter(String name);
}
