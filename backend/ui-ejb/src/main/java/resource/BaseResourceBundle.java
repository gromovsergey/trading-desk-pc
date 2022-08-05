package resource;

import com.foros.cache.local.DynamicResourcesLocalCache;
import com.foros.cache.local.LocalCacheValuesProducer;
import com.foros.model.admin.DynamicResource;
import com.foros.session.ServiceLocator;
import com.foros.session.admin.CustomizationResourcesService;
import com.foros.session.admin.DynamicResourcesService;
import com.foros.util.StringUtil;
import com.foros.util.i18n.ResourceMap;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import sun.util.ResourceBundleEnumeration;

abstract public class BaseResourceBundle extends ResourceBundle {
    private DynamicResourcesService dynamicResourcesService;
    private CustomizationResourcesService customizationResourcesService;
    private DynamicResourcesLocalCache resourcesLocalCache;

    private String baseName;
    private String lang;
    private ResourceBundle delegate;

    public BaseResourceBundle(String baseName, String lang) {
        this.baseName = baseName;
        this.lang = lang;
        this.dynamicResourcesService = ServiceLocator.getInstance().lookup(DynamicResourcesService.class);
        this.customizationResourcesService = ServiceLocator.getInstance().lookup(CustomizationResourcesService.class);
        this.resourcesLocalCache = ServiceLocator.getInstance().lookup(DynamicResourcesLocalCache.class);
    }

    @Override
    protected Object handleGetObject(String key) {
        String str = getCustomizationBundle().getProperty(key);
        if (StringUtil.isPropertyNotEmpty(str)) {
            return str;
        }
        str = getBundle().getString(key);
        if (StringUtil.isPropertyNotEmpty(str)) {
            return str;
        }
        try {
            return getDelegate() != null ? getDelegate().getString(key) : null;
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    @Override
    public Enumeration<String> getKeys() {
        return new ResourceBundleEnumeration(getBundle().getKeys(),
                getDelegate() != null ? getDelegate().getKeys() : null);
    }

    private String getResourceName() {
        String langSuffix = "en".equals(lang) ? "" : "_" + lang;
        return baseName.replace('.', '/') + langSuffix + ".properties";
    }
    
    private ResourceBundle getDelegate() {
        if (delegate != null) {
            return delegate;
        }

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(getResourceName());
        if (is != null) {
            try {
                delegate = new PropertyResourceBundle(is);
            }
            catch (Exception e) {
                e.printStackTrace();
            } finally {
                 IOUtils.closeQuietly(is);
            }
		}

        return delegate;
    }

    private Properties getCustomizationBundle() {
        return customizationResourcesService.findLangResources(lang);
    }

    private ResourceMap getBundle() {
        ResourceMap map = (ResourceMap) resourcesLocalCache.get(lang, new LocalCacheValuesProducer() {
            @Override
            public Object getValue() {
                List<DynamicResource> res = dynamicResourcesService.findLangResources(lang);
                return new ResourceMap(res);
            }
        });

        return map;
    }
}
