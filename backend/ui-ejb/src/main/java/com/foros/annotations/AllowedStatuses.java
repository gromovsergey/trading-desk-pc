package com.foros.annotations;

import com.foros.model.Status;
import java.lang.annotation.*;

/**
 * @author oleg_roshka
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AllowedStatuses {
    public Status[] values();
}
