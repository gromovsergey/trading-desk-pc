package com.foros.session;

import com.foros.session.db.DBConstraint;
import com.foros.util.ExceptionUtil;
import com.foros.util.VersionCollisionException;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.sql.SQLException;
import javax.persistence.EntityNotFoundException;
import org.hibernate.WrongClassException;

public class PersistenceExceptionHelper {

    public static Exception handle(Exception e) {
        Exception handled = null;

        Exception root = ExceptionUtil.getRootException(e);

        if (root instanceof WrongClassException) {
            handled = new EntityNotFoundException("Entity with id=" + ((WrongClassException) root).getIdentifier() + " not found");
        }

        if (handled == null) {
            handled = handleBySQLState(root);
        }

        if (handled == null) {
            handled = handleConstraintViolation(root);
        }

        if (handled == null) {
            handled = e;
        }

        return handled;
    }

    private static Exception handleBySQLState(Exception root) {
        SQLException sqlException = ExceptionUtil.getCause(root, SQLException.class);
        if (sqlException == null) {
            return null;
        }

        String sqlState = sqlException.getSQLState();
        if (sqlState == null) {
            return null;
        }

        switch (sqlState) {
            case "U0017":
                return ConstraintViolationException
                        .newBuilder("campaign.errors.budgetOutOfRange")
                        .withPath("allocatedAmount")
                        .build();
            case "U0018":
                return new VersionCollisionException();
            default:
                return null;
        }
    }

    private static Exception handleConstraintViolation(Exception e) {

        DBConstraint constraint = DBConstraint.fromException(e);
        if (constraint == null) {
            // not a constraint violation
            return null;
        }

        return ConstraintViolationException
                .newBuilder(constraint.getResourceKey())
                .withPath(constraint.getField())
                .build();
    }
}
