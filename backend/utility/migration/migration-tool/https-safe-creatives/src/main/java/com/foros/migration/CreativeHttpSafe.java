package com.foros.migration;

import com.foros.model.creative.Creative;
import com.foros.model.template.Option;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.template.HtmlOptionHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

@Configuration
@Import({ OptionGroupConfiguration.class, OptionConfiguration.class, OptionValueConfiguration.class })
@EnableBatchProcessing
public class CreativeHttpSafe implements Migration.Executor {

    public static final String TEMPLATE_ID_AND_GROUP_ID_SOURCE = "templateIdAndGroupId";
    public static final String TEMPLATE_ID_AND_OPTON_ID_SOURCE = "templateIdAndOptionId";

    public static final int CHUNK_SIZE = 10000;

    public static final int PAGE_SIZE = CHUNK_SIZE;

    public static final String AD_OPS = "AdOps";

    @Autowired
    private Job mainJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Resource(name = "exceptions")
    private List<Throwable> exceptions;

    @Autowired
    public Logger logger;

    @Bean
    public List<Throwable> exceptions() {
        return new ArrayList<>();
    }

    @Bean
    public Job mainJob(JobBuilderFactory jobs, Step createOptionGroup,
            Step createOption, Step createOptionEnumValue,
            Step readExistingOption,
            Step removeCreativeOptionValue,
            Step createCreativeOptionValue,
            Step updateCreativeVersion) {
        return jobs
                .get("mainJob")
                .incrementer(new RunIdIncrementer())
                .listener(customJobExecutionListener())
                .flow(createOptionGroup)
                .next(createOption)
                .next(createOptionEnumValue)
                .next(readExistingOption)
                .next(removeCreativeOptionValue)
                .next(createCreativeOptionValue)
                .next(updateCreativeVersion)
                .build()
                .build();
    }

    @Bean
    public CustomHtmlOptionHelper htmlOptionHelper() {
        return new CustomHtmlOptionHelper();
    }

    static class CustomHtmlOptionHelper extends HtmlOptionHelper {
        @Override
        protected EntityManager getEM() {
            return null;
        }

        @Override
        protected LoggingJdbcTemplate getJdbcTemplate() {
            return null;
        }

        @Override
        public boolean isHttpOnlyCreative(Creative creative) {
            return super.isHttpOnlyCreative(creative);
        }
    }

    @Bean
    public StepExecutionListener customChunkListener() {
        return new StepListenerSupport() {

            @Override
            public void beforeStep(StepExecution stepExecution) {
                super.beforeStep(stepExecution);
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {

                exceptions.addAll(stepExecution.getFailureExceptions());

                logger.log(INFO,
                        "StepName : {0} started {1} executed in {2} ",
                        new Object[] { stepExecution.getStepName(),
                                stepExecution.getStartTime(),
                                System.currentTimeMillis() - stepExecution.getStartTime().getTime() });
                return super.afterStep(stepExecution);
            }

            @Override
            public void afterChunk(ChunkContext context) {
                StepExecution stepExecution = context.getStepContext().getStepExecution();
                logger.log(INFO, "StepName : {0}  ReadCount : {1} FilterCount {2}",
                        new Object[] { stepExecution.getStepName(), stepExecution.getReadCount(), stepExecution.getFilterCount() });

            }
        };

    }

    @Bean
    public JobExecutionListener customJobExecutionListener() {
        return new JobExecutionListener() {

            @Override
            public void beforeJob(JobExecution jobExecution) {
                logger.info("beforeJob");
                jobExecution.getExecutionContext().put(TEMPLATE_ID_AND_GROUP_ID_SOURCE, new HashMap<Long, TemplateGroup>());
                jobExecution.getExecutionContext().put(TEMPLATE_ID_AND_OPTON_ID_SOURCE, new HashMap<Long, TemplateOption>());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                Map groups = (Map) jobExecution.getExecutionContext().get(TEMPLATE_ID_AND_GROUP_ID_SOURCE);
                logger.log(INFO, "{0} was created groups" , groups.size());

                Map options = (Map) jobExecution.getExecutionContext().get(TEMPLATE_ID_AND_OPTON_ID_SOURCE);
                logger.log(INFO, "{0} was created options" , options.size());
            }
        };
    }

    @Bean
    public JobRepository jobRepository() throws Exception {
        MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean = new MapJobRepositoryFactoryBean();
        return mapJobRepositoryFactoryBean.getObject();
    }

    @Bean
    public JobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        return jobLauncher;
    }

    @Bean
    public DataSource postgresDataSource(
            @Value("${pg_url}") String url,
            @Value("${pg_user}") String username,
            @Value("${pg_password}") String password) {
        return new DriverManagerDataSource(url, username, password);
    }

    @Override
    public void run() throws Exception {
        jobLauncher.run(mainJob, new JobParameters());

        if (!exceptions.isEmpty()) {
            logger.log(Level.SEVERE, "{0}", exceptions);
            throw new RuntimeException(exceptions.get(0));
        }
    }

    public static void main(String[] args) {
        Migration.perform(CreativeHttpSafe.class);
    }

    public static class TemplateGroup implements Serializable {
        private ImmutablePair<Long, Long> pair;

        public static TemplateGroup of(final Long templateId, final Long groupId) {
            return new TemplateGroup(templateId, groupId);
        }

        private TemplateGroup(Long templateId, Long groupId) {
            pair = ImmutablePair.of(templateId, groupId);
        }

        public Long getTemplateId() {
            return pair.getLeft();
        }

        public Long getGroupId() {
            return pair.getRight();
        }

        @Override
        public String toString() {
            return pair.toString();
        }

    }

    public static class TemplateOption implements Serializable {
        private ImmutableTriple<Long, Option, Boolean> triple;

        public static TemplateOption of(final Long templateId, final Long optionId, final String token, final String defaultValue, final boolean existing) {
            Option option = new Option();
            option.setId(optionId);
            option.setToken(token);
            option.setDefaultValue(defaultValue);
            return new TemplateOption(templateId, option, existing);
        }

        private TemplateOption(Long templateId, Option option, boolean existing) {
            triple = ImmutableTriple.of(templateId, option, existing);
        }

        public Long getTemplateId() {
            return triple.getLeft();
        }

        public Option getOption() {
            return triple.getMiddle();
        }

        public boolean isExisting() {
            return triple.getRight();
        }

        @Override
        public String toString() {
            return triple.toString();
        }

    }

}
