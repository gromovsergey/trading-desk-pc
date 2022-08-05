package com.foros.session.security;

import javax.ejb.Local;

@Local
public interface LastLoginService {

    void update(String token, String ip);

}
