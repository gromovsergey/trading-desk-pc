package app.programmatic.ui.device.service;

import com.foros.rs.client.model.device.DeviceChannel;
import com.foros.rs.client.model.device.DeviceChannelSelector;
import app.programmatic.ui.common.foros.service.ForosDeviceService;
import app.programmatic.ui.common.validation.exception.EntityNotFoundException;
import app.programmatic.ui.device.dao.model.Device;
import app.programmatic.ui.device.dao.model.DeviceNode;
import app.programmatic.ui.device.dao.model.RootDevices;
import app.programmatic.ui.device.tool.DeviceBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.annotation.PostConstruct;


@Service
public class DeviceServiceImpl implements DeviceService {
    private static final String APPLICATIONS_DEVICES_NAME = "Applications";
    private static final String BROWSERS_DEVICES_NAME = "Browsers";
    private static final String NON_MOBILE_DEVICES_NAME = "Non-mobile Devices";
    private static final String MOBILE_DEVICES_NAME = "Mobile Devices";

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private ForosDeviceService forosDeviceService;

    private Device applications;
    private Device browsers;
    private Device mobile;
    private Device nonMobile;

    @Override
    public Collection<DeviceNode> getAvailableDevicesByFlightId(Long flightId) {
        List<Long> accountDeviceIds = flightDeviceIds(flightId);
        List<DeviceChannel> accountDevices = getAvailableDevicesByAccountId(accountDeviceIds);
        return DeviceBuilder.buildTree(accountDevices);
    }

    @Override
    public Collection<DeviceNode> getAvailableDevicesByAccountId(Long accountId) {
        List<Long> accountDeviceIds = accountDeviceIds(accountId);
        List<DeviceChannel> accountDevices = getAvailableDevicesByAccountId(accountDeviceIds);
        return DeviceBuilder.buildTree(accountDevices);
    }

    private List<DeviceChannel> getAvailableDevicesByAccountId(List<Long> accountDeviceIds) {
        DeviceChannelSelector selector = new DeviceChannelSelector();
        selector.setChannelIds(accountDeviceIds);
        return forosDeviceService.getAdminDeviceService().fetcher().fetch(selector);
    }

    @Override
    public RootDevices availableRootDevices(Long flightId) {
        List<Long> parentIds = flightDeviceIds(flightId);
        return availableRootDevices(parentIds);
    }

    private List<Long> flightDeviceIds(Long flightId) {
        List<Long> result = jdbcOperations.query(
                "select distinct(atdc.device_channel_id) from insertionorder io " +
                        "  inner join account a1 on a1.account_id = io.account_id " +
                        "  left join account a2 on a2.account_id = a1.agency_account_id " +
                        "  inner join accounttypedevicechannel atdc on atdc.account_type_id = coalesce(a1.account_type_id, a2.account_type_id) " +
                        "  left join flight f on f.io_id = io.io_id " +
                        "  left join flight li on li.parent_id = f.flight_id " +
                        "  where (f.flight_id = ? or li.flight_id = ?)",
                new Object[] { flightId, flightId },
                (ResultSet rs, int ind) -> rs.getLong("device_channel_id"));
        if (result.isEmpty()) {
            throw new EntityNotFoundException(flightId);
        }
        return result;
    }

    private RootDevices availableRootDevices(List<Long> parentIds) {
        boolean applicationsAvailable = parentIds.contains(applications.getId());
        boolean browsersAvailable = parentIds.contains(browsers.getId());
        boolean mobileAvailable = parentIds.contains(mobile.getId());
        boolean nonMobileAvailable = parentIds.contains(nonMobile.getId());

        if (applicationsAvailable && browsersAvailable && mobileAvailable && nonMobileAvailable) {
            return new RootDevices();
        }

        HashSet<Device> result = new HashSet<>(4);
        if (applicationsAvailable) {
            result.add(applications);
        }
        if (browsersAvailable) {
            result.add(browsers);
        }
        if (mobileAvailable) {
            result.add(mobile);
        }
        if (nonMobileAvailable) {
            result.add(nonMobile);
        }

        return new RootDevices(result);
    }

    @Override
    public RootDevices availableAccountRootDevices(Long accountId) {
        List<Long> parentIds = accountDeviceIds(accountId);
        return availableRootDevices(parentIds);
    }

    private List<Long> accountDeviceIds(Long accountId) {
        List<Long> result = jdbcOperations.query(
                "select atdc.device_channel_id from account a1 " +
                        "  left join account a2 on a2.account_id = a1.agency_account_id " +
                        "  inner join accounttypedevicechannel atdc on atdc.account_type_id = coalesce(a1.account_type_id, a2.account_type_id) " +
                        "  where a1.account_id = ?",
                new Object[] { accountId },
                (ResultSet rs, int ind) -> rs.getLong("device_channel_id"));
        if (result.isEmpty()) {
            throw new EntityNotFoundException(accountId);
        }
        return result;
    }

    @PostConstruct
    public void init() {
        applications = findByName(APPLICATIONS_DEVICES_NAME);
        browsers = findByName(BROWSERS_DEVICES_NAME);
        mobile = findByName(MOBILE_DEVICES_NAME);
        nonMobile = findByName(NON_MOBILE_DEVICES_NAME);
    }

    private Device findByName(String name) {
        return jdbcOperations.queryForObject("select channel_id from channel where channel_type = 'V' and name = ?",
                new Object[] { name },
                (ResultSet rs, int ind) -> new Device(rs.getLong("channel_id"), name));
    }
}
