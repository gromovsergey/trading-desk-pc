package com.foros.web.taglib;

import com.foros.restriction.RestrictionService;
import com.foros.session.ServiceLocator;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestrictionTools {
    private static final Logger logger = Logger.getLogger(RestrictionTools.class.getName());

    public static boolean isPermitted(String restrictionName) {
        return isPermittedImpl(restrictionName);
    }

    public static boolean isPermitted(String restrictionName, Object param1) {
        return isPermittedImpl(restrictionName, param1);
    }

    public static boolean isPermitted(String restrictionName, Object[] params) {
        return isPermittedImpl(restrictionName, params);
    }

    private static boolean isPermittedImpl(String restrictionName, Object...params) {
        RestrictionService service =
                ServiceLocator.getInstance().lookup(RestrictionService.class);

        if (logger.isLoggable(Level.FINE)) {
	        logger.fine(MessageFormat.format("Tracing call: isPermitted({0}, {1})",
	                restrictionName, (params == null ? Collections.<Object>emptyList() : params)));
        }
        
        boolean result;
        result = service.isPermitted(restrictionName, params);

        if (logger.isLoggable(Level.FINE)) {
	        logger.fine(MessageFormat.format("Tracing result: isPermitted({0}, {1}) = {2}",
	                restrictionName, (params == null ? Collections.<Object>emptyList() : params), result));
        }
        
        return result;
    }
    
    public static boolean isPermittedAny(String restrictionNames) {
    	String[] names = restrictionNames.split(",");
    	for (String name: names) {
            if (isPermitted(name)) {
                return true;
            }
    	}
    	return false;
    }
}
