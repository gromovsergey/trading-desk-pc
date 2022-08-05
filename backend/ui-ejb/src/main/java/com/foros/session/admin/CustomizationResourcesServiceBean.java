package com.foros.session.admin;

import com.foros.config.ConfigService;
import com.foros.util.customization.CustomizationHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.apache.commons.lang.StringUtils;

@Stateless(name = "CustomizationResourcesService")
public class CustomizationResourcesServiceBean implements CustomizationResourcesService {
    private static final String L10N = "L10n";
    private static final String BASE_NAME = "resources";
    private static final String PROPERTIES_SUFFIX = ".properties";
    private static final Logger logger = Logger.getLogger(CustomizationResourcesServiceBean.class.getName());

    private static final ConcurrentHashMap<String, Properties> propertiesMap = new ConcurrentHashMap<>();

    @EJB
    private ConfigService configService;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Properties findLangResources(String lang) {
        String customizationName = CustomizationHelper.getCustomizationName();
        if (StringUtils.isBlank(customizationName)) {
            return new Properties();
        }

        String customizationKey = customizationName + lang;
        Properties properties = propertiesMap.get(customizationKey);
        if (properties != null) {
            return properties;
        }

        return initProperties(customizationKey, lang);
    }

    private Properties initProperties(String customizationKey, String lang) {
        Properties properties = new Properties();
        Path resourcesPath = Paths.get(CustomizationHelper.getCustomizationRoot(configService))
                .resolve(CustomizationHelper.getCustomizationName())
                .resolve(L10N)
                .resolve(getResourceName(lang));

        String fileName = resourcesPath.toString();
        try (InputStream is = new FileInputStream(fileName)) {
            properties.load(is);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "File '" + fileName + "' with localization resource not found. " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An exception in CustomizationResourcesServiceBean occurred. File '" + fileName + "'", e);
        }

        Properties currentValue = propertiesMap.putIfAbsent(customizationKey, properties);
        return currentValue != null ? currentValue : properties;
    }

    private String getResourceName(String lang) {
        String langSuffix = "en".equals(lang) ? "" : "_" + lang;
        return BASE_NAME + langSuffix + PROPERTIES_SUFFIX;
    }
}
