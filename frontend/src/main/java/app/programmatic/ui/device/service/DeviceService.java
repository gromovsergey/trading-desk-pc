package app.programmatic.ui.device.service;

import app.programmatic.ui.device.dao.model.DeviceNode;
import app.programmatic.ui.device.dao.model.RootDevices;

import java.util.Collection;

public interface DeviceService {

    Collection<DeviceNode> getAvailableDevicesByFlightId(Long flightId);

    Collection<DeviceNode> getAvailableDevicesByAccountId(Long accountId);

    RootDevices availableRootDevices(Long flightId);

    RootDevices availableAccountRootDevices(Long accountId);
}
