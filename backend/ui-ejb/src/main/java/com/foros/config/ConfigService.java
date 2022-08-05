package com.foros.config;

import javax.ejb.Local;

@Local
public interface ConfigService extends Config {
    Config detach();
}
