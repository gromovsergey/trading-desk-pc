package com.foros.session.creative;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton(name = "TemporaryCreativePreviewCleaner")
@Startup
public class TemporaryCreativePreviewCleanerBean implements TemporaryCreativePreviewCleaner {
    private static final Logger logger = Logger.getLogger(TemporaryCreativePreviewCleanerBean.class.getName());

    @EJB
    private CreativePreviewService creativePreviewService;

    @Override
    public void proceed() {
        logger.log(Level.INFO, "Temporary Creative Preview Cleaner running.");
        creativePreviewService.deleteAllTemporaryCreativePreviews();
    }
}
