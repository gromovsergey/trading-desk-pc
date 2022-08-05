package com.foros.migration;

import static com.foros.migration.CreativeHttpSafe.AD_OPS;
import static com.foros.migration.CreativeHttpSafe.TEMPLATE_ID_AND_GROUP_ID_SOURCE;

import com.foros.migration.CreativeHttpSafe.TemplateGroup;
import com.foros.model.template.CreativeTemplate;
import com.foros.session.template.HtmlOptionHelper;
import com.foros.util.function.BiFunction;
import com.foros.util.function.Function;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

@Configuration
public class OptionGroupConfiguration {

    @Bean
    public Step createOptionGroup(StepBuilderFactory stepBuilderFactory,
            ItemReader<CreativeTemplate> templatesReader,
            ItemWriter<CreativeTemplate> groupWriter,
            StepExecutionListener customChunkListener) {
        StepBuilder stepBuilder = stepBuilderFactory.get("createGroup");
        return stepBuilder
                .listener(customChunkListener)
                .<CreativeTemplate, CreativeTemplate> chunk(CreativeHttpSafe.CHUNK_SIZE)
                .reader(templatesReader)
                .writer(groupWriter)
                .build();
    }

    @Bean
    public ItemReader<CreativeTemplate> templatesReader(DataSource postgresDataSource) throws Exception {
        JdbcPagingItemReader<CreativeTemplate> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(postgresDataSource);
        itemReader.setPageSize(CreativeHttpSafe.PAGE_SIZE);
        itemReader.setQueryProvider(templatePagingQueryProvider(postgresDataSource));
        itemReader.setRowMapper(new RowMapper<CreativeTemplate>() {
            @Override
            public CreativeTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
                CreativeTemplate template = new CreativeTemplate();
                template.setId(rs.getLong(1));
                return template;
            }
        });
        return itemReader;
    }

    @Bean
    public PagingQueryProvider templatePagingQueryProvider(DataSource postgresDataSource) throws Exception {
        return templatePagingQueryProviderFactoryBean(postgresDataSource).getObject();
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean templatePagingQueryProviderFactoryBean(DataSource postgresDataSource) {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(postgresDataSource);
        factoryBean.setDatabaseType(DatabaseType.POSTGRES.name());
        factoryBean.setSelectClause("select template_id ");
        factoryBean.setFromClause(" from Template as t ");
        factoryBean.setWhereClause(" where not  exists ( " +
                " select template_id from Options where token = '" + HtmlOptionHelper.HTTPS_SAFE_TOKEN + "' and t.template_id = template_id )  ");
        factoryBean.setSortKey("template_id");
        return factoryBean;
    }

    @Bean
    public ItemWriter<CreativeTemplate> groupWriter(DataSource postgresDataSource) {
        String sql = "insert into optiongroup (template_id, name, availability, collapsibility, type, sort_order) values (?, '" + AD_OPS + "', 'A', 'N', 'Hidden', 1)";
        PostgresBatchItemWriter<CreativeTemplate, TemplateGroup> jdbcBatchItemWriter = new PostgresBatchItemWriter<>();
        jdbcBatchItemWriter.setDataSource(postgresDataSource);
        jdbcBatchItemWriter.setSql(sql);
        jdbcBatchItemWriter.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<CreativeTemplate>() {
            @Override
            public void setValues(CreativeTemplate item, PreparedStatement ps) throws SQLException {
                ps.setLong(1, item.getId());
            }
        });
        jdbcBatchItemWriter.setKeySourceForFutureSteps(TEMPLATE_ID_AND_GROUP_ID_SOURCE);
        jdbcBatchItemWriter.setKeyTransformer(new Function<CreativeTemplate, Long>() {
            @Override
            public Long apply(CreativeTemplate template) {
                return template.getId();
            }
        });
        jdbcBatchItemWriter.setValueTransformer(new BiFunction<CreativeTemplate, Long, TemplateGroup>() {
            @Override
            public TemplateGroup apply(CreativeTemplate t, Long generatedKey) {
                return TemplateGroup.of(t.getId(), generatedKey);
            }
        });
        return jdbcBatchItemWriter;
    }
}
