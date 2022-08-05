package com.foros.migration;

import com.foros.migration.CreativeHttpSafe.CustomHtmlOptionHelper;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionType;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.foros.session.template.HtmlOptionHelper.HTTPS_SAFE_TOKEN;

@Configuration
public class OptionValueConfiguration {

    private static final CreativeOptionValue STUB = new CreativeOptionValue();

    private static final CreativeSize CREATIVE_SIZE = new CreativeSize();

    @Autowired
    public Logger logger;

    @Bean
    public Step updateCreativeVersion(StepBuilderFactory stepBuilderFactory, DataSource postgresDataSource) {
        StepBuilder stepBuilder = stepBuilderFactory.get("updateCreativeVersion");
        return stepBuilder.tasklet(updateCreativeVersionQuery(postgresDataSource)).build();
    }

    @Bean Tasklet updateCreativeVersionQuery(DataSource postgresDataSource) {
        return new UpdateCreativeVersion(postgresDataSource);
    }

    public class UpdateCreativeVersion implements Tasklet {
        DataSource dataSource;
        public UpdateCreativeVersion(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            String sql = "update creative set version = now()";
            new JdbcTemplate(dataSource).execute(sql);
            return RepeatStatus.FINISHED;
        }
    }

    @Bean
    public Step removeCreativeOptionValue(StepBuilderFactory stepBuilderFactory, DataSource postgresDataSource) {
        StepBuilder stepBuilder = stepBuilderFactory.get("removeCreativeOptionValue");

        return stepBuilder.tasklet(runDeleteCreativeOptionsQuery(postgresDataSource)).build();
    }

    @Bean
    public Tasklet runDeleteCreativeOptionsQuery(DataSource postgresDataSource) {
        return new DeleteCreativeOptions(postgresDataSource);
    }

    public class DeleteCreativeOptions implements Tasklet {
        DataSource dataSource;
        public DeleteCreativeOptions(DataSource dataSource) {
            this.dataSource = dataSource;
        }
        @Override
        public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            String sql = "delete from creativeoptionvalue where option_id in(select option_id from options where token = '" + HTTPS_SAFE_TOKEN + "')";
            new JdbcTemplate(dataSource).execute(sql);
            return RepeatStatus.FINISHED;
        }
    }

    @Bean
    public Step createCreativeOptionValue(StepBuilderFactory stepBuilderFactory,
            ItemReader<CreativeOptionValue> creativeOptionValueReader,
            ItemProcessor<CreativeOptionValue, CreativeOptionValue> creativeOptionValueProccessor,
            ItemWriter<CreativeOptionValue> httpSafeCreativeOptionValueWriter,
            StepExecutionListener customChunkListener) {
        StepBuilder stepBuilder = stepBuilderFactory.get("createCreativeOptionValue");
        return stepBuilder
                .listener(customChunkListener)
                .<CreativeOptionValue, CreativeOptionValue> chunk(CreativeHttpSafe.CHUNK_SIZE)
                .reader(creativeOptionValueReader)
                .processor(creativeOptionValueProccessor)
                .writer(httpSafeCreativeOptionValueWriter)
                .build();
    }

    @Bean
    public ItemReader<CreativeOptionValue> creativeOptionValueReader(DataSource postgresDataSource,
            RowMapper<CreativeOptionValue> creativeRowMapper) throws Exception {

        JdbcPagingItemReader<CreativeOptionValue> itemReader = new JdbcPagingItemReader<CreativeOptionValue>() {
            @Override
            protected void doReadPage() {
                super.doReadPage();
                checkResults();
            }

            private void checkResults() {
                if (results.size() < 2) {
                    return;
                }
                CreativeOptionValue last = results.get(results.size() - 1);

                int i = 0;
                for (int j = results.size() - 1; j > 0; j--)
                    if (!last.getCreative().equals(results.get(j).getCreative())) {
                        i = j;
                        break;
                    }

                if (i == 0) { // last step
                    results.add(STUB);
                    return;
                }

                for (int j = i + 1; j < results.size(); j++) {
                    results.set(j, STUB);
                }
            }
        };
        itemReader.setDataSource(postgresDataSource);
        itemReader.setPageSize(CreativeHttpSafe.PAGE_SIZE);
        itemReader.setQueryProvider(customCreativePagingQueryProvider(postgresDataSource));
        itemReader.setRowMapper(creativeRowMapper);
        return itemReader;
    }

    @Bean
    public ItemWriter<CreativeOptionValue> httpSafeCreativeOptionValueWriter(DataSource postgresDataSource) {
        String sql = "insert into creativeoptionvalue (option_id, creative_id, value) values (?, ?, ?)";
        PostgresBatchItemWriter<CreativeOptionValue, Object> jdbcBatchItemWriter = new PostgresBatchItemWriter<CreativeOptionValue, Object>() {
            private List<CreativeOptionValue> items = new ArrayList<>();

            @Override
            public void write(List<? extends CreativeOptionValue> items) throws Exception {
                this.items.addAll(items);
                super.write(items);
            }

            @Override
            protected void printInfo() {
                logger.info("Created optionvalues : " + items.size());
            }
        };
        jdbcBatchItemWriter.setDataSource(postgresDataSource);
        jdbcBatchItemWriter.setSql(sql);
        jdbcBatchItemWriter.setItemPreparedStatementSetter(
                new ItemPreparedStatementSetter<CreativeOptionValue>() {
                    @Override
                    public void setValues(CreativeOptionValue optionValue, PreparedStatement ps) throws SQLException {
                        ps.setLong(1, optionValue.getOptionId());
                        ps.setLong(2, optionValue.getCreative().getId());
                        ps.setString(3, optionValue.getValue());
                    }
                });
        return jdbcBatchItemWriter;
    }

    @Bean
    public PagingQueryProvider customCreativePagingQueryProvider(DataSource postgresDataSource) throws Exception {
        PostgresPagingQueryProvider provider = new PostgresPagingQueryProvider() {

            @Override
            public String generateRemainingPagesQuery(int pageSize) {
                String query = super.generateRemainingPagesQuery(pageSize);
                query = query.replace("cr.creative_id >", "cr.creative_id >=");

                return query;
            }
        };

        provider.setSelectClause("select cr.template_id as \"cr.template_id\" , cr.creative_id as \"cr.creative_id\" , v.value, opt.option_id, opt.type, opt.token ");
        provider.setFromClause(" from creative as  cr " +
                " join creativeoptionvalue as v on v.creative_id = cr.creative_id " +
                " join options as opt on opt.option_id = v.option_id ");
        provider.setWhereClause(" where opt.type in ('HTML', 'File/URL', 'URL')");
        Map<String, Order> sortKeys = new LinkedHashMap<>();
        sortKeys.put("cr.creative_id", Order.ASCENDING);
        provider.setSortKeys(sortKeys);
        provider.setSortKeys(sortKeys);

        provider.init(postgresDataSource);
        return provider;
    }

    @Bean
    public ItemProcessor<CreativeOptionValue, CreativeOptionValue> creativeOptionValueProccessor(CustomHtmlOptionHelper htmlOptionHelper) {
        return new CustomItemProcessor(creativeProccessor(htmlOptionHelper));
    }

    @Bean
    public CreativeProcessor creativeProccessor(CustomHtmlOptionHelper htmlOptionHelper) {
        return new CreativeProcessor(htmlOptionHelper);
    }

    public static class CustomItemProcessor extends StepListenerSupport<CreativeOptionValue, CreativeOptionValue>
            implements ItemProcessor<CreativeOptionValue, CreativeOptionValue> {
        private Creative tmpCreative = null;

        private CreativeProcessor creativeProccessor;

        public CustomItemProcessor(CreativeProcessor creativeProccessor) {
            this.creativeProccessor = creativeProccessor;
        }

        @Override
        public void beforeStep(StepExecution stepExecution) {
            creativeProccessor.beforStep(stepExecution);
        }

        @Override
        public CreativeOptionValue process(CreativeOptionValue optionValue) throws Exception {
            if (tmpCreative == null) {
                tmpCreative = optionValue.getCreative();
                return null;
            }

            if (tmpCreative.equals(optionValue.getCreative())) {
                tmpCreative.getOptions().add(optionValue);
                return null;
            }

            CreativeOptionValue httpSafeOptionValue = creativeProccessor.process(tmpCreative);
            tmpCreative = optionValue.getCreative();
            return httpSafeOptionValue;
        }
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean creativePagingQueryProviderFactoryBean(DataSource postgresDataSource) {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(postgresDataSource);
        factoryBean.setDatabaseType(DatabaseType.POSTGRES.name());
        factoryBean.setSelectClause("select cr.template_id as \"cr.template_id\" , cr.creative_id as \"cr.creative_id\" , v.value, opt.option_id, opt.type, opt.token ");
        factoryBean.setFromClause(" from creative as  cr " +
                " join creativeoptionvalue as v on v.creative_id = cr.creative_id " +
                " join options as opt on opt.option_id = v.option_id ");
        factoryBean.setWhereClause(" where opt.type in ('HTML', 'File/URL', 'URL')");
        Map<String, Order> sortKeys = new LinkedHashMap<>();
        sortKeys.put("cr.creative_id", Order.ASCENDING);
        //        sortKeys.put("cr.template_id", Order.ASCENDING);
        factoryBean.setSortKeys(sortKeys);
        return factoryBean;
    }

    @Bean
    public RowMapper<CreativeOptionValue> creativeRowMapper() {
        return new RowMapper<CreativeOptionValue>() {
            @Override
            public CreativeOptionValue mapRow(ResultSet rs, int rowNum) throws SQLException {
                long creativeId = rs.getLong("cr.creative_id");
                Creative creative = new Creative(creativeId);
                creative.setTemplate(new CreativeTemplate(rs.getLong("cr.template_id")));
                creative.setSize(CREATIVE_SIZE);

                CreativeOptionValue optionValue = new CreativeOptionValue();
                optionValue.setValue(rs.getString("value"));
                Option option = new Option(rs.getLong("option_id"));
                option.setType(OptionType.byName(rs.getString("type")));
                option.setToken(rs.getString("token"));
                optionValue.setOption(option);

                creative.getOptions().add(optionValue);
                optionValue.setCreative(creative);
                return optionValue;
            }
        };
    }

}
