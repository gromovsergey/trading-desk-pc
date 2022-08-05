package app.programmatic.ui.common.foros.service;

import com.foros.rs.client.service.AdvertisingChannelService;


public interface ForosChannelService {
    AdvertisingChannelService getChannelService();

    AdvertisingChannelService getAdminChannelService();
}
