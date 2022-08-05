package com.foros.session.db;

import java.lang.reflect.Field;
import java.util.Map;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

public class PGExceptionInspector {
    private static final String UK_VIOLATION = "23505";
    private static final char MSG_PART_CONSTRAINT = 'n';
    private static final Field messagePartsField = initMessagePartsField();

    private static Field initMessagePartsField() {
        try {
            Field res = ServerErrorMessage.class.getDeclaredField("m_mesgParts");
            res.setAccessible(true);
            return res;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getPart(ServerErrorMessage sem, char key) {
        try {
            Map messageParts = (Map) messagePartsField.get(sem);
            return (String) messageParts.get(key);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static String getConstraint(PSQLException e) {
        if (!UK_VIOLATION.equals(e.getSQLState())) {
            return null;
        }
        return getPart(e.getServerErrorMessage(), MSG_PART_CONSTRAINT);
    }
}
