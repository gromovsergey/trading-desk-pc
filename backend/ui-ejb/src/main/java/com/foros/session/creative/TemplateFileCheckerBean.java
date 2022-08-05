package com.foros.session.creative;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "TemplateFileChecker")
@Startup
public class TemplateFileCheckerBean implements TemplateFileChecker {
    private static final Logger logger = Logger.getLogger(TemplateFileCheckerBean.class.getName());

    @EJB
    private CreativePreviewService creativePreviewService;

    @Override
    public void proceed() {
        logger.log(Level.INFO, "Template file checker running.");
        creativePreviewService.deleteObsoletePreviews();
    }
}
