package com.foros.migration;

import com.foros.migration.CreativeHttpSafe.CustomHtmlOptionHelper;
import com.foros.migration.CreativeHttpSafe.TemplateOption;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.template.Option;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;

import static com.foros.session.template.HtmlOptionHelper.HTTPS_SAFE;
import static com.foros.session.template.HtmlOptionHelper.HTTP_ONLY;

public class CreativeProcessor implements ItemProcessor<Creative, CreativeOptionValue> {

    private Map<Long, TemplateOption> templatesAndOptionIds;

    private CustomHtmlOptionHelper htmlOptionHelper;

    public CreativeProcessor(CustomHtmlOptionHelper htmlOptionHelper) {
        this.htmlOptionHelper = htmlOptionHelper;
    }

    public void beforStep(StepExecution stepExecution) {
        templatesAndOptionIds = (Map<Long, TemplateOption>) stepExecution.getJobExecution().getExecutionContext().get(CreativeHttpSafe.TEMPLATE_ID_AND_OPTON_ID_SOURCE);
    }

    @Override
    public CreativeOptionValue process(Creative creative) throws Exception {
        Long templateId = creative.getTemplate().getId();
        TemplateOption templateOption = templatesAndOptionIds.get(templateId);
        if (templateOption == null) {
            throw new IllegalArgumentException("templateOption : " + templateId + " must be not null ");
        }

        String httpSafeValue = htmlOptionHelper.isHttpOnlyCreative(creative) ? HTTP_ONLY : HTTPS_SAFE;

        if (httpSafeValue.equals(templateOption.getOption().getDefaultValue())) {
            return null;
        }

        CreativeOptionValue httpOptionValue = new CreativeOptionValue();
        httpOptionValue.setCreative(creative);

        Option httpSafeOption = new Option();
        httpSafeOption.setId(templateOption.getOption().getId());

        httpOptionValue.setOption(httpSafeOption);
        httpOptionValue.setValue(httpSafeValue);
        return httpOptionValue;
    }
}