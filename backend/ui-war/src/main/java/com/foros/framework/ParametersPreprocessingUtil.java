package com.foros.framework;

import com.foros.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import org.apache.commons.beanutils.PropertyUtils;

public class ParametersPreprocessingUtil {

    public static void trimParameters(Object source, Map parameters, boolean trimAll, ParameterTrimmer parameterTrimmer) {
        Trim typeAnnotation = source.getClass().getAnnotation(Trim.class);
        Set<String> paramsToTrim = new HashSet<String>();
        boolean hasInclude = false;

        if (typeAnnotation != null && typeAnnotation.include().length > 0) {
            paramsToTrim.addAll(Arrays.asList(typeAnnotation.include()));
            hasInclude = true;
        }

        if (trimAll && !hasInclude) {
            paramsToTrim.addAll(parameters.keySet());
        }

        if (typeAnnotation != null && typeAnnotation.exclude().length > 0) {
            paramsToTrim.removeAll(Arrays.asList(typeAnnotation.exclude()));
        }

        for (String parameterName : paramsToTrim) {
            parameterTrimmer.trimParameter(parameters, parameterName);
        }
    }

    public static void replaceParameters(Object source) {
        NumericField typeAnnotation = source.getClass().getAnnotation(NumericField.class);
        if (typeAnnotation != null) {
            for (String parameterName : typeAnnotation.value()) {
                try {
                    Object value = PropertyUtils.getProperty(source, parameterName);
                    if (value instanceof String) {
                        PropertyUtils.setProperty(source, parameterName, StringUtil.spaceToNbsp((String) value));
                    }
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                } catch (NoSuchMethodException e) {
                }
            }
        }
    }
}
