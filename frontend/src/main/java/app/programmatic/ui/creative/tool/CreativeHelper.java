package app.programmatic.ui.creative.tool;

import app.programmatic.ui.creative.dao.model.Creative;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcOperations;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class CreativeHelper {
    private static final Logger logger = LoggerFactory.getLogger(CreativeHelper.class);

    public static Long updateOnlyName(Creative creative, Creative oldCreative, JdbcOperations jdbcOperations) {
        int result = 0;
        if (isObjectsFieldsEquals(creative, oldCreative, "name")) {
            logger.info("Update Creative with creativeId (Only name) = {}", creative.getId());
            result = jdbcOperations.update("update creative set name = ? where creative_id = ?",
                    creative.getName(), creative.getId());
            if (result == 0) {
                return null;
            }
        }

        return result == 0 ? 0L : creative.getId();
    }

    public static Long updateOnlyStatus(Creative creative, Creative oldCreative, JdbcOperations jdbcOperations) {
        int result = 0;
        if (isObjectsFieldsEquals(creative, oldCreative, "displayStatus")) {
            if (creative.getDisplayStatus().name().equals(CreativeStatus.LIVE.name())) {
                logger.info("Update Creative with creativeId (Only status) = {}", creative.getId());
                result = jdbcOperations.update("update creative set display_status_id = ?, status = ? where creative_id = ?",
                        CreativeStatus.LIVE.getDisplayStatusId(), CreativeStatus.LIVE.getStatus(), creative.getId());
                if (result == 0) {
                    return null;
                }
            } else if (creative.getDisplayStatus().name().equals(CreativeStatus.INACTIVE.name())) {
                logger.info("Update Creative with creativeId (Only status) = {}", creative.getId());
                result = jdbcOperations.update("update creative set display_status_id = ?, status = ? where creative_id = ?",
                        CreativeStatus.INACTIVE.getDisplayStatusId(), CreativeStatus.INACTIVE.getStatus(), creative.getId());
                if (result == 0) {
                    return null;
                }
            }
        }

        return result == 0 ? 0L : creative.getId();
    }

    private static boolean isObjectsFieldsEquals(Creative newCreative, Creative oldCreative, String fieldToIgnore) {
        PropertyDescriptor[] properties = BeanUtils.getPropertyDescriptors(Creative.class);

        Method method;
        for (PropertyDescriptor property : properties) {
            method = property.getReadMethod();
            method.setAccessible(true);
            Object newField = null;
            Object oldField = null;
            try {
                newField = method.invoke(newCreative);
                oldField = method.invoke(oldCreative);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error(e.getMessage(), e);
            }
            if (!property.getName().equals(fieldToIgnore) && !property.getName().equals("version")) {
                if (newField instanceof ArrayList) {
                    Object newObjField;
                    Object oldObjField;
                    for (int i = 0; i < ((ArrayList<?>) newField).size(); i++) {
                        newObjField = ((ArrayList<?>) newField).get(i);
                        oldObjField = ((ArrayList<?>) oldField).get(i);
                        if (!EqualsBuilder.reflectionEquals(newObjField, oldObjField)) {
                            return false;
                        }
                    }
                    continue;
                } else if (ObjectUtils.notEqual(newField, oldField)) {
                    return false;
                }
            }
        }
        return true;
    }
}
