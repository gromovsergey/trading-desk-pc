package com.foros.web;

import com.foros.action.admin.creativeCategories.CreativeCategoryConfiguration;
import com.foros.session.spring.ImportEjb;
import com.foros.web.security.UiSecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ImportEjb
@Import({UiSecurityConfiguration.class, CreativeCategoryConfiguration.class})
public class UiConfiguration {
}
