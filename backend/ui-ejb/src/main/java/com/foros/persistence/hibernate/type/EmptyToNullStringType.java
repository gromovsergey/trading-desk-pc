package com.foros.persistence.hibernate.type;

import com.foros.util.StringUtil;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class EmptyToNullStringType extends AbstractSingleColumnStandardBasicType<String> {

    private static final StringTypeDescriptor STRING_TYPE_DESCRIPTOR = new StringTypeDescriptor() {
        @Override
        public <X> X unwrap(String value, Class<X> type, WrapperOptions options) {
            return super.unwrap(StringUtil.isPropertyEmpty(value) ? null : value, type, options);
        }
    };

    public EmptyToNullStringType() {
        super(VarcharTypeDescriptor.INSTANCE, STRING_TYPE_DESCRIPTOR);
    }

    public String getName() {
        return EmptyToNullStringType.class.getName();
    }

    public String toString(String value) {
        return value;
    }
}
