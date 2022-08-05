package app.programmatic.ui.common.foros.service;

import com.foros.rs.client.Foros;
import com.foros.rs.client.service.*;

import org.springframework.beans.factory.annotation.Autowired;


public class SourceServiceImpl implements ForosServiceConfigurator, ForosCampaignService, ForosAccountService, ForosRestrictionService,
        ForosCreativeService, ForosChannelService, ForosCcgService, ForosCreativeLinkService, ForosConversionService, ForosDeviceService,
        ForosFileService, ForosReportService {
    private static final ThreadLocal<Foros> forosThreadLocal = new ThreadLocal<>();

    @Autowired
    private ForosBuilder forosBuilder;

    @Override
    public void configure(String userToken, String userKey) {
        forosThreadLocal.set(forosBuilder.getRestrictedForos(userToken, userKey));
    }


    @Override
    public CampaignService getCampaignService() {
        return forosThreadLocal.get().getCampaignService();
    }

    @Override
    public AccountService getAccountService() {
        return forosThreadLocal.get().getAccountService();
    }

    @Override
    public AccountService getAdminAccountService() {
        return forosBuilder.getAdminForos().getAccountService();
    }

    @Override
    public RestrictionService getRestrictionService() {
        return forosThreadLocal.get().getRestrictionService();
    }

    @Override
    public CreativeService getCreativeService() {
        return forosThreadLocal.get().getCreativeService();
    }

    @Override
    public CreativeSizeService getSizeService() {
        return forosThreadLocal.get().getCreativeSizeService();
    }

    @Override
    public CreativeSizeService getAdminSizeService() {
        return forosBuilder.getAdminForos().getCreativeSizeService();
    }

    @Override
    public CreativeTemplateService getTemplateService() {
        return forosThreadLocal.get().getCreativeTemplateService();
    }

    @Override
    public CreativeTemplateService getAdminTemplateService() {
        return forosBuilder.getAdminForos().getCreativeTemplateService();
    }

    @Override
    public CreativeCategoryService getCreativeCategoryService() {
        return forosThreadLocal.get().getCreativeCategoryService();
    }

    @Override
    public CreativeCategoryService getAdminCreativeCategoryService() {
        return forosBuilder.getAdminForos().getCreativeCategoryService();
    }

    @Override
    public AdvertisingChannelService getChannelService() {
        return forosThreadLocal.get().getAdvertisingChannelService();
    }

    @Override
    public AdvertisingChannelService getAdminChannelService() {
        return forosBuilder.getAdminForos().getAdvertisingChannelService();
    }

    @Override
    public CampaignCreativeGroupService getCcgService() {
        return forosThreadLocal.get().getCampaignCreativeGroupService();
    }

    @Override
    public CampaignCreativeGroupService getAdminCcgService() {
        return forosBuilder.getAdminForos().getCampaignCreativeGroupService();
    }

    @Override
    public CreativeLinkService getCreativeLinkService() {
        return forosThreadLocal.get().getCreativeLinkService();
    }

    @Override
    public ConversionService getConversionService() {
        return forosThreadLocal.get().getConversionService();
    }

    @Override
    public DeviceChannelService getDeviceService() {
        return forosThreadLocal.get().getDeviceChannelService();
    }

    @Override
    public DeviceChannelService getAdminDeviceService() {
        return forosBuilder.getAdminForos().getDeviceChannelService();
    }

    @Override
    public FilesService getFilesService() {
        return forosThreadLocal.get().getFilesService();
    }

    @Override
    public FilesService getAdminFilesService() {
        return forosBuilder.getAdminForos().getFilesService();
    }

    @Override
    public ReportService getReportService() {
        return forosThreadLocal.get().getReportService();
    }

    @Override
    public ReportService getAdminReportService() {
        return forosBuilder.getAdminForos().getReportService();
    }

    @Override
    public void cleanUp() {
        forosThreadLocal.remove();
    }
}
