package app.programmatic.ui.common.foros.service;

import com.foros.rs.client.service.DeviceChannelService;

public interface ForosDeviceService {
    DeviceChannelService getDeviceService();

    DeviceChannelService getAdminDeviceService();
}
