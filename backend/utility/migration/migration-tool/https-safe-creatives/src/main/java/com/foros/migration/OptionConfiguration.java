package com.foros.migration;

import com.foros.migration.CreativeHttpSafe.TemplateGroup;
import com.foros.migration.CreativeHttpSafe.TemplateOption;
import com.foros.session.template.HtmlOptionHelper;
import com.foros.util.function.BiFunction;
import com.foros.util.function.Function;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.foros.migration.CreativeHttpSafe.CHUNK_SIZE;
import static com.foros.migration.CreativeHttpSafe.TEMPLATE_ID_AND_GROUP_ID_SOURCE;
import static com.foros.migration.CreativeHttpSafe.TEMPLATE_ID_AND_OPTON_ID_SOURCE;

@Configuration
public class OptionConfiguration {

    @Autowired
    public Logger logger;

    @Bean
    public Step readExistingOption(StepBuilderFactory stepBuilderFactory,
            ItemReader<TemplateOption> existingOptionReader,
            ItemWriter<TemplateOption> existingOptionWriter,
            StepExecutionListener customChunkListener) {
        return stepBuilderFactory.get("readExistingOption")
                .listener(customChunkListener)
                .<TemplateOption, TemplateOption> chunk(CHUNK_SIZE)
                .reader(existingOptionReader)
                .writer(existingOptionWriter)
                .build();
    }

    @Bean
    public ItemReader<TemplateOption> existingOptionReader(DataSource postgresDataSource) {
        String sql = "select  t.template_id, opt.option_id, opt.default_value from options as opt join template as t on t.template_id = opt.template_id "
                + " where opt.token = '" + HtmlOptionHelper.HTTPS_SAFE_TOKEN + "' and opt.name ='Secure AdServing' "
                + " order by t.template_id ";
        JdbcCursorItemReader<TemplateOption> itemReader = new JdbcCursorItemReader<>();
        itemReader.setDataSource(postgresDataSource);
        itemReader.setSql(sql);
        itemReader.setRowMapper(new RowMapper<TemplateOption>() {
            public TemplateOption mapRow(ResultSet rs, int rowNum) throws SQLException {
                return TemplateOption.of(rs.getLong("template_id"), rs.getLong("option_id"),
                        HtmlOptionHelper.HTTPS_SAFE_TOKEN, rs.getString("default_value"),  true);
            }
        });
        return itemReader;
    }

    @Bean
    public ItemWriter<TemplateOption> existingOptionWriter() {
        return new ItemWriter<TemplateOption>() {
            Map<Long, TemplateOption> source = null;

            @BeforeStep
            public void beforeStep(StepExecution stepExecution) {
                source = (Map<Long, TemplateOption>) stepExecution.getJobExecution().getExecutionContext().get(CreativeHttpSafe.TEMPLATE_ID_AND_OPTON_ID_SOURCE);
            }

            @Override
            public void write(List<? extends TemplateOption> items) throws Exception {
                for (TemplateOption triple : items) {
                    if (source.get(triple.getTemplateId()) == null) {
                        source.put(triple.getTemplateId(), triple);
                    }
                }
            }
        };
    }

    @Bean
    public Step createOption(StepBuilderFactory stepBuilderFactory,
            ItemReader<TemplateGroup> optionReader,
            ItemWriter<TemplateGroup> optionWriter,
            StepExecutionListener customChunkListener) {
        return stepBuilderFactory.get("createOption")
                .listener(customChunkListener)
                .<TemplateGroup, TemplateGroup> chunk(CHUNK_SIZE)
                .reader(optionReader)
                .writer(optionWriter)
                .build();
    }

    @Bean
    public ItemReader<TemplateGroup> optionReader() throws Exception {
        ItemReader<TemplateGroup> reader = new ItemReader<TemplateGroup>() {
            private IteratorItemReader<TemplateGroup> reader;

            @BeforeStep
            public void befoStep(StepExecution stepExecution) {
                Collection<TemplateGroup> source = ((Map<Long, TemplateGroup>) stepExecution.getJobExecution().getExecutionContext().get(TEMPLATE_ID_AND_GROUP_ID_SOURCE)).values();
                reader = new IteratorItemReader<>(source);
            }

            @Override
            public TemplateGroup read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                return reader.read();
            }
        };

        return reader;

    }

    @Bean
    public ItemWriter<TemplateGroup> optionWriter(DataSource postgresDataSource) {
        String sql = "insert into options(name, type, token, default_value, required, template_id, option_group_id, is_internal, sort_order, recursive_tokens ) " +
                "values ('Secure AdServing', 'Enum', '" + HtmlOptionHelper.HTTPS_SAFE_TOKEN + "', '" + HtmlOptionHelper.HTTP_ONLY + "',  true, ?,  ? , true, 0 , 0)";
        PostgresBatchItemWriter<TemplateGroup, TemplateOption> jdbcBatchItemWriter = new PostgresBatchItemWriter<>();
        jdbcBatchItemWriter.setDataSource(postgresDataSource);
        jdbcBatchItemWriter.setSql(sql);
        jdbcBatchItemWriter.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<TemplateGroup>() {
            @Override
            public void setValues(TemplateGroup item, PreparedStatement ps) throws SQLException {
                ps.setLong(1, item.getTemplateId());
                ps.setLong(2, item.getGroupId());
            }
        });
        jdbcBatchItemWriter.setKeySourceForFutureSteps(TEMPLATE_ID_AND_OPTON_ID_SOURCE);
        jdbcBatchItemWriter.setKeyTransformer(new Function<TemplateGroup, Long>() {
            @Override
            public Long apply(TemplateGroup templateGroup) {
                return templateGroup.getTemplateId();
            }
        });
        jdbcBatchItemWriter.setValueTransformer(new BiFunction<TemplateGroup, Long, TemplateOption>() {
            @Override
            public TemplateOption apply(TemplateGroup t, Long generatedKey) {
                return TemplateOption.of(t.getTemplateId(), generatedKey,
                    HtmlOptionHelper.HTTPS_SAFE_TOKEN, HtmlOptionHelper.HTTP_ONLY, false);
            }
        });

        return jdbcBatchItemWriter;
    }

    @Bean
    public Step createOptionEnumValue(StepBuilderFactory stepBuilderFactory,
            ItemReader<TemplateOption> optionEnumValueReader,
            ItemWriter<TemplateOption> optionEnumValueWriter,
            StepExecutionListener customChunkListener) {
        return stepBuilderFactory.get("createOptionEnumValue")
                .listener(customChunkListener)
                .<TemplateOption, TemplateOption> chunk(CHUNK_SIZE)
                .reader(optionEnumValueReader)
                .writer(optionEnumValueWriter)
                .build();
    }

    @Bean
    public ItemReader<TemplateOption> optionEnumValueReader() {
        ItemReader<TemplateOption> reader = new ItemReader<TemplateOption>() {
            private IteratorItemReader<TemplateOption> reader;

            @BeforeStep
            public void beforeStep(StepExecution stepExecution) {
                Collection<TemplateOption> source = ((Map<Long, TemplateOption>) stepExecution.getJobExecution().getExecutionContext().get(TEMPLATE_ID_AND_OPTON_ID_SOURCE)).values();
                reader = new IteratorItemReader<>(source);
            }

            @Override
            public TemplateOption read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                return reader.read();
            }
        };

        return reader;
    }

    @Bean
    public PostgresBatchItemWriter<TemplateOption, Object> httpOnlyWriter(DataSource postgresDataSource) {
        String sql = "insert into optionenumvalue(option_id, value, is_default, name) "
                + "values (?, '" + HtmlOptionHelper.HTTP_ONLY + "', true, '" + HtmlOptionHelper.HTTP_ONLY + "')";
        PostgresBatchItemWriter<TemplateOption, Object> jdbcBatchItemWriter = new PostgresBatchItemWriter<>();
        jdbcBatchItemWriter.setDataSource(postgresDataSource);
        jdbcBatchItemWriter.setSql(sql);
        jdbcBatchItemWriter.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<TemplateOption>() {
            @Override
            public void setValues(TemplateOption item, PreparedStatement ps) throws SQLException {
                ps.setLong(1, item.getOption().getId());
            }
        });
        return jdbcBatchItemWriter;
    }

    @Bean
    public PostgresBatchItemWriter<TemplateOption, Object> httpSafeWriter(DataSource postgresDataSource) {
        String sql = "insert into optionenumvalue(option_id, value, is_default, name) "
                + "values (?, '" + HtmlOptionHelper.HTTPS_SAFE + "', false, '" + HtmlOptionHelper.HTTPS_SAFE + "')";
        PostgresBatchItemWriter<TemplateOption, Object> jdbcBatchItemWriter = new PostgresBatchItemWriter<>();
        jdbcBatchItemWriter.setDataSource(postgresDataSource);
        jdbcBatchItemWriter.setSql(sql);
        jdbcBatchItemWriter.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<TemplateOption>() {
            @Override
            public void setValues(TemplateOption item, PreparedStatement ps) throws SQLException {
                ps.setLong(1, item.getOption().getId());
            }
        });
        return jdbcBatchItemWriter;
    }

    @Bean
    public ItemWriter<TemplateOption> optionEnumValueWriter(
            final PostgresBatchItemWriter<TemplateOption, Object> httpOnlyWriter,
            final PostgresBatchItemWriter<TemplateOption, Object> httpSafeWriter) {
        return new ItemWriter<TemplateOption>() {

            @Override
            public void write(List<? extends TemplateOption> items) throws Exception {
                httpOnlyWriter.write(items);
                httpSafeWriter.write(items);
            }

            @AfterStep
            public void afterStep(StepExecution stepExecution) {
                httpOnlyWriter.afterStep(stepExecution);
                httpSafeWriter.afterStep(stepExecution);
            }
        };
    }
}
