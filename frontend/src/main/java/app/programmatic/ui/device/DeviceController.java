package app.programmatic.ui.device;

import app.programmatic.ui.device.dao.model.DeviceNode;
import app.programmatic.ui.device.service.DeviceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Collection;


@RestController
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @RequestMapping(method = RequestMethod.GET, path = "/rest/device", produces = "application/json")
    public Collection<DeviceNode> getAvailableDevices(@RequestParam(value = "flightId", required = false) Long flightId,
                                                      @RequestParam(value = "lineItemId", required = false) Long lineItemId,
                                                      @RequestParam(value = "accountId", required = false) Long accountId)
                                                          throws MissingServletRequestParameterException {
        int paramsCount = flightId != null ? 1 : 0;
        paramsCount += lineItemId != null ? 1 : 0;
        paramsCount += accountId != null ? 1 : 0;
        if (paramsCount != 1) {
            throw new MissingServletRequestParameterException("lineItemId|flightId|accountId", "Long");
        }

        if (accountId != null) {
            return deviceService.getAvailableDevicesByAccountId(accountId);
        }
        return deviceService.getAvailableDevicesByFlightId(flightId != null ? flightId : lineItemId);
    }
}
