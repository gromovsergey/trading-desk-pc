package com.foros.validation.interpolator;

import com.foros.validation.code.ForosError;

public interface ForosErrorResolver {

    ForosError resolve(MessageTemplate template);

    ForosError resolve(String template);
}
