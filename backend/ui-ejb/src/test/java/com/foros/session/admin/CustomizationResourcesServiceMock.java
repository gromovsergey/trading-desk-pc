package com.foros.session.admin;

import java.util.Properties;

public class CustomizationResourcesServiceMock implements CustomizationResourcesService {

    @Override
    public Properties findLangResources(String lang) {
        return new Properties();
    }

}
