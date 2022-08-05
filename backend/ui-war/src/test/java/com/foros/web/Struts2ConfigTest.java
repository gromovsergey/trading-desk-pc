package com.foros.web;

import com.foros.framework.ReadOnly;

import static org.junit.Assert.assertFalse;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import group.Struts2;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.struts2.StrutsJUnit4TestCase;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.web.context.WebApplicationContext;

public class Struts2ConfigTest extends StrutsJUnit4TestCase {

    private static Set<String> noVersionAllowed = new HashSet<>(Arrays.asList(
        "resource : admin/resource/htmlName/save",
        "resource : admin/DynamicResource/save",
        "resource : admin/resource/CategoryChannel/save",
        "resource : admin/resource/Option/save",
        "creativeCategory : admin/resource/CreativeCategory/save",
        "triggers : admin/Triggers/update",
        "discoverChannel : linkDiscoverChannel",
        "advertiserAccount : admin/advertiser/account/terms/save",
        "publisherAccount : admin/publisher/account/terms/save",
        "ispAccount : admin/isp/account/terms/save",
        "cmpAccount : admin/cmp/account/terms/save",
        "displayCreative : admin/creative/updates",
        "displayCreative : advertiser/creative/updates",
        "campaignGroupBulkFrequencyCaps : save",
        "campaignGroupBulkRates : save",
        "campaignGroupBulkBidStrategy : save",
        "campaignGroupBulkSiteTargeting : save",
        "displayCreative : admin/creative/saveLinks",
        "displayCreative : advertiser/creative/saveLinks",
        "action : */Action/saveLinks",
        "campaignGroupBulkGeotarget : save",
        "displayCreative : admin/creative/saveclickUrls",
        "displayCreative : advertiser/creative/saveclickUrls",
        "campaignGroupBulkClickUrls : save",
        "campaignGroupBulkDeviceTargeting : save"
    ));

    @Override
    protected void setupBeforeInitDispatcher() throws Exception {
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, new StaticApplicationContext());
    }

    @Test
    @Category(Struts2.class)
    public void version() throws Exception {
        RuntimeConfiguration rc = configuration.getRuntimeConfiguration();
        Map<String,Map<String,ActionConfig>> actionConfigs = rc.getActionConfigs();

        boolean isIncorrect = false;
        for (Map<String, ActionConfig> configMap : actionConfigs.values()) {
            for (ActionConfig config : configMap.values()) {
                isIncorrect = isActionConfigIncorrect(config) || isIncorrect;
            }
        }

        assertFalse("Some errors was found", isIncorrect);
    }

    private boolean isActionConfigIncorrect(ActionConfig config) throws ClassNotFoundException, NoSuchMethodException {
        String actionName = config.getName();
        if (actionName.startsWith("xml/")) {
            return false;
        }

        String className = config.getClassName();
        String methodName = config.getMethodName();

        if (methodName == null) {
            return false;
        }

        String fullName = config.getPackageName() + " : " + actionName;
        if (noVersionAllowed.contains(fullName)) {
            return false;
        }

        if (isWildcard(className)) {
            return false;
        }

        if (!(methodName.equals("save") || methodName.equals("update"))) {
            return false;
        }

        Class<?> actionClass = Class.forName(className);

        if (isWildcard(methodName)) {
            return false;
        }

        Method method = actionClass.getMethod(methodName);
        boolean isReadOnly = method.getAnnotation(ReadOnly.class) != null;

        if (isReadOnly) {
            return false;
        }

        // all update methods should have input and version
        Set<String> results = config.getResults().keySet();
        if (!results.contains("version")) {
            System.out.println("Action: " + fullName + " has no version result");
            return true;
        }
        return false;
    }

    private boolean isWildcard(String name) {
        return name.contains("{1}") || name.contains("{2}");
    }
}
