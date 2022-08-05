package com.foros.framework.conversion;

import com.foros.model.Identifiable;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public class IdentifiableEntityConverter extends IdentifiableEntityConverterSupport {

    @Override
    protected Identifiable newInstance(Class toClass) {
        Identifiable identifiable;
        try {
            identifiable = (Identifiable) toClass.newInstance();
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
        return identifiable;
    }

}
