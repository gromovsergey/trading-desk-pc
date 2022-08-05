package com.foros.restriction;

import com.foros.validation.ValidationContext;
import javax.ejb.Local;

/**
 * Service for evaluating restrictions.
 * 
 * @author pavel
 * @see com.foros.restriction.annotation.Restriction
 */
@Local
public interface RestrictionService {

    /**
     * Determines whether restriction is satisfied or not.
     * @param restrictionName the name specified in restriction annotation.
     * @param params restriction parameters.
     * @return {@code true} if restriction is satisfied, {@code false} - otherwise.
     */
    boolean isPermitted(String restrictionName, Object... params);

    /**
     * Determines whether restriction is satisfied or not.
     * @param restrictionName the name specified in restriction annotation.
     * @param param restriction parameter.
     * @return {@code true} if restriction is satisfied, {@code false} - otherwise.
     */
    boolean isPermitted(String restrictionName, Object param);

    /**
     * Determines whether restriction is satisfied or not.
     * @param restrictionName the name specified in restriction annotation.
     * @return {@code true} if restriction is satisfied, {@code false} - otherwise.
     */
    boolean isPermitted(String restrictionName);

    /**
     * Validate passed restriction and add constraints in context
     *
     * @param context validation context
     * @param restrictionName  the name specified in restriction annotation
     * @param params restriction parameters
     */
    void validateRestriction(ValidationContext context, String restrictionName, Object... params);
}
