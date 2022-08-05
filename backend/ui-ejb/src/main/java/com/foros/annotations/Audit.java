package com.foros.annotations;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.changes.inspection.ChangeNode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Audit {
    Class<? extends AuditSerializer> serializer() default AuditSerializer.class;
    Class<? extends ChangeNode.Factory> nodeFactory() default ChangeNode.Factory.class;
}
