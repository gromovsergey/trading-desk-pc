package app.programmatic.ui.geo;

import app.programmatic.ui.geo.dao.model.AddressTO;
import app.programmatic.ui.geo.dao.model.Location;
import app.programmatic.ui.geo.service.GeoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;


@RestController
public class GeoController {
    @Autowired
    private GeoService geoService;


    @RequestMapping(method = RequestMethod.GET, path = "/rest/geo/location/search", produces = "application/json")
    public Collection<Location> searchLocations(@RequestParam(value = "text") String text,
                                                @RequestParam(value = "country") String countryCode,
                                                @RequestParam(value = "language") String language) {
        return geoService.searchLocations(text, countryCode, language);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/geo/location/list", produces = "application/json")
    public Collection<Location> getLocations(@RequestParam(value = "geoIds") List<Long> geoIds,
                                             @RequestParam(value = "language") String language) {
        return geoService.getLocations(geoIds, language);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/geo/address/list", produces = "application/json")
    public Collection<AddressTO> getAddresses(@RequestParam(value = "geoIds") List<Long> geoIds) {
        return geoService.getAddresses(geoIds);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/geo/address/search", produces = "application/json")
    public List<AddressTO> searchAddresses(@RequestParam(value = "geoCode") String geoCode) throws UnsupportedEncodingException {
        return geoService.searchAddress(geoCode);
    }
}
