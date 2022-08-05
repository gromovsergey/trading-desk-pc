package com.foros.validation.strategy;

import com.foros.validation.constraint.violation.Path;

public interface SubContextSupportStrategy extends ValidationStrategy{

    ValidationStrategy subContextStrategy(Path path);

}
