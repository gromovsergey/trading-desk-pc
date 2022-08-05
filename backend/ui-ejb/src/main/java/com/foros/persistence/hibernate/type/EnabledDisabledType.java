package com.foros.persistence.hibernate.type;

import org.hibernate.type.CharBooleanType;

public class EnabledDisabledType extends CharBooleanType {

    public EnabledDisabledType() {
        this('E', 'D');
    }

    public EnabledDisabledType(char characterValueTrue, char characterValueFalse) {
        super(characterValueTrue, characterValueFalse);
    }
}
