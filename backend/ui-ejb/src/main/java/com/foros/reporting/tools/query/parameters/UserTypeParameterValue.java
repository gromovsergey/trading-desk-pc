package com.foros.reporting.tools.query.parameters;

import org.hibernate.usertype.UserType;
import org.springframework.jdbc.core.SqlParameterValue;

public class UserTypeParameterValue extends SqlParameterValue {

    private UserType type;

    public UserTypeParameterValue(String name, UserType type, Object value) {
        super(new UserTypeParameter(name, type), value);
        this.type = type;
    }

    public UserType getType() {
        return type;
    }
}
