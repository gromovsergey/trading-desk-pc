package com.foros.session.admin;

import java.util.Properties;

import javax.ejb.Local;

@Local
public interface CustomizationResourcesService {

    Properties findLangResources(String lang);

}
