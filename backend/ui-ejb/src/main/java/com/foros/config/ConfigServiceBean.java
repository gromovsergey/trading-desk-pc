package com.foros.config;

import com.foros.util.customization.CustomizationHelper;

import static javax.ejb.TransactionAttributeType.SUPPORTS;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.interceptor.ExcludeDefaultInterceptors;

@Startup
@Singleton(name = "ConfigService")
@ExcludeDefaultInterceptors
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ConfigServiceBean implements ConfigService {
    private ConfigImpl config;
    private Map<String, CustomizationConfig> customizations = new HashMap<>();

    @PostConstruct
    public void createConfig() {
        config = new ConfigImpl(ConfigParameters.class);
        for (String name : CustomizationHelper.getCustomizationNames()) {
            customizations.put(name, new CustomizationConfig(name, config));
        }
    }

    @Override
    @TransactionAttribute(SUPPORTS)
    public <T> T get(ConfigParameter<T> parameter) {
        return getConfig().get(parameter);
    }

    @Override
    @TransactionAttribute(SUPPORTS)
    public Config detach() {
        return this;
    }

    private Config getConfig() {
        String name = CustomizationHelper.getCustomizationName();
        Config res = null;
        if (name != null) {
            res = customizations.get(name);
        }
        return res == null ? config : res;
    }
}
