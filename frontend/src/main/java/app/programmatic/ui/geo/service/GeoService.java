package app.programmatic.ui.geo.service;

import app.programmatic.ui.geo.dao.model.Address;
import app.programmatic.ui.geo.dao.model.AddressTO;
import app.programmatic.ui.geo.dao.model.Location;
import app.programmatic.ui.geo.validation.ValidateAddress;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;


public interface GeoService {

    Collection<Location> getLocations(Collection<Long> ids, String language);

    Collection<Location> searchLocations(String text, String countryCode, String language);

    Collection<AddressTO> getAddresses(Collection<Long> ids);

    List<AddressTO> searchAddress(String geoCode) throws UnsupportedEncodingException;

    Long createAddressChannel(@ValidateAddress("") Address address);

    Long findAddressChannel(AddressTO addressView, String countryCode);

    Long getCountryChannelId(String countryCode);
}
