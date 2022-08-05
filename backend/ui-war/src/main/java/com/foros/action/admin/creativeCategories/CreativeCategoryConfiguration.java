package com.foros.action.admin.creativeCategories;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Lazy
@Configuration
public class CreativeCategoryConfiguration {
    @Bean
    public CreativeCategoryFieldCsvHelper creativeCategoryFieldCsvHelper() {
        return new CreativeCategoryFieldCsvHelper();
    }

    @Bean
    public CreativeCategoryCsvReader.Factory creativeCategoryCsvReaderFactory() {
        return new CreativeCategoryCsvReader.Factory();
    }

}
