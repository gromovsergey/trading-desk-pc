package com.foros.util;

import com.foros.session.db.DBConstraint;
import com.foros.validation.ValidationContext;
import com.foros.validation.util.ValidationUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

public class CopyFlusher {
    public static void flush(EntityManager em, String entityName, DBConstraint constraint) {
        try {
            em.flush();
        } catch (PersistenceException e) {
            if (constraint.match(e)) {
                ValidationContext context = ValidationUtil.createContext();
                context.addConstraintViolation("errors.concurrentCopy")
                        .withParameters(entityName);
                context.throwIfHasViolations();
            }
            throw e;
        }

    }
}
