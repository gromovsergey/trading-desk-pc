package com.foros.migration;

import com.foros.util.function.BiFunction;
import com.foros.util.function.Function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class PostgresBatchItemWriter<T, S> extends JdbcBatchItemWriter<T> {
    private Map<Long, S> sourceForFutureSteps = new HashMap<>();
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private String sql;
    private ItemPreparedStatementSetter<T> preparedStatementSetter;
    private BiFunction<T, Long, S> valueTransformer;
    private Function<T, Long> keyTransformer;
    private String keySourceForFutureSteps;

    @Override
    public void setSql(String sql) {
        super.setSql(sql);
        this.sql = sql;
    }

    public void setTransformer(BiFunction<T, Long, S> transformer) {
        this.valueTransformer = transformer;
    }

    @Override
    public void setItemPreparedStatementSetter(ItemPreparedStatementSetter<T> preparedStatementSetter) {
        super.setItemPreparedStatementSetter(preparedStatementSetter);
        this.preparedStatementSetter = preparedStatementSetter;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        if (namedParameterJdbcTemplate == null) {
            this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        }
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        if (isSaveGeneratedKeys()) {
            Map<Long, S> source = (Map<Long, S>) stepExecution.getJobExecution().getExecutionContext().get(keySourceForFutureSteps);
            source.putAll(sourceForFutureSteps);
        }

        printInfo();

    }

    protected void printInfo() {}

    @Override
    public void write(final List<? extends T> items) throws Exception {
        if (!items.isEmpty()) {

            if (logger.isDebugEnabled()) {
                logger.debug("Executing batch with " + items.size() + " items.");
            }

            namedParameterJdbcTemplate.getJdbcOperations().execute(
                    new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection c) throws SQLException {
                            return isSaveGeneratedKeys() ? c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : c.prepareStatement(sql);
                        }
                    },
                    new PreparedStatementCallback<int[]>() {
                        public int[] doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                            for (T item : items) {
                                preparedStatementSetter.setValues(item, ps);
                                ps.addBatch();
                            }
                            int[] executeBatch = ps.executeBatch();
                            if (isSaveGeneratedKeys()) {
                                fillSource(items, ps);
                            }
                            return executeBatch;
                        }
                    });
        }

    }

    private boolean isSaveGeneratedKeys() {
        return keySourceForFutureSteps != null;
    }

    private void fillSource(List<? extends T> items, PreparedStatement ps) throws SQLException {
        sourceForFutureSteps = new HashMap<>(items.size());
        try (ResultSet resultSet = ps.getGeneratedKeys()) {
            int i = 0;
            while (resultSet.next()) {
                T item = items.get(i++);
                sourceForFutureSteps.put(keyTransformer.apply(item), valueTransformer.apply(item, resultSet.getLong(1)));

            }
        }
    }

    public void setKeySourceForFutureSteps(String keySourceForFutureSteps) {
        this.keySourceForFutureSteps = keySourceForFutureSteps;
    }


    public void setKeyTransformer(Function<T, Long> keyTransformer) {
        this.keyTransformer = keyTransformer;
    }


    public void setValueTransformer(BiFunction<T, Long, S> valueTransformer) {
        this.valueTransformer = valueTransformer;
    }

}
