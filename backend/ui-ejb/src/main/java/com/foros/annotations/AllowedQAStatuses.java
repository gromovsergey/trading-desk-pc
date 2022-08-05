package com.foros.annotations;

import com.foros.model.ApproveStatus;
import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AllowedQAStatuses {
    public ApproveStatus[] values();
}
