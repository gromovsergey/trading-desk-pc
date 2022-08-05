package com.foros.session.channel;

import com.foros.session.site.WDTagPreviewService;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "DiscoverCustomizationFileChecker")
@Startup
public class DiscoverCustomizationFileCheckerBean implements DiscoverCustomizationFileChecker {
    private static final Logger logger = Logger.getLogger(DiscoverCustomizationFileCheckerBean.class.getName());
    
    @EJB
    WDTagPreviewService previewService;

    @Override
    public void proceed() {
        logger.log(Level.INFO, "Discover customization checker running.");
        previewService.updateObsoletePreviews();
    }
}
