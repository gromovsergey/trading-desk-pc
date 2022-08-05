package com.foros.security.principal;

import org.springframework.security.core.Authentication;

public interface Tokenable extends Authentication {

    String getToken();

}
