package app.programmatic.ui.geo.service;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

import app.programmatic.ui.email.service.EmailServiceImpl;
import app.programmatic.ui.geo.tool.DomHelper;
import app.programmatic.ui.geo.dao.AddressRepository;
import app.programmatic.ui.geo.dao.model.Address;
import app.programmatic.ui.geo.dao.model.AddressTO;
import app.programmatic.ui.geo.dao.model.Location;
import app.programmatic.ui.geo.dao.model.RadiusUnit;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
@Validated
public class GeoServiceImpl implements GeoService {
    private static final int MAX_LOCALIZED_CHANNEL_ROWS = 100;
    private static final Logger logger = Logger.getLogger(GeoServiceImpl.class.getName());

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private AddressRepository addressRepository;


    @Override
    public Collection<Location> getLocations(Collection<Long> ids, String language) {
        if (ids.size() > MAX_LOCALIZED_CHANNEL_ROWS) {
            logger.log(Level.WARNING, "Need to localize " + ids.size()  + " (more than " + MAX_LOCALIZED_CHANNEL_ROWS + ") geo channels! ");
        }

        Array idsArray = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", ids.toArray()));
        return jdbcOperations.query(
                "select * from entityqueries.geo_channel_localized_search_by_ids(?, ?, ?, ?)",
                new Object[] { idsArray, language, MAX_LOCALIZED_CHANNEL_ROWS, 0},
                (ResultSet rs, int ind) -> new Location(
                        rs.getLong("channel_id"),
                        rs.getString("full_localized_name"))
        );
    }

    @Override
    public Collection<AddressTO> getAddresses(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        Array array = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", ids.toArray()));
        return jdbcOperations.query(
                "select * from channel where channel_type = 'G' and address is not null and channel_id = any(?) ",
                new Object[]{ array },
                (ResultSet rs, int index) -> new AddressTO(
                        rs.getLong("channel_id"),
                        rs.getString("country_code"),
                        rs.getString("address"),
                        rs.getLong("radius"),
                        RadiusUnit.valueOf(rs.getString("radius_units"))));
    }

    @Override
    public Collection<Location> searchLocations(String text, String countryCode, String language) {
        return jdbcOperations.query(
                "select * from entityqueries.geo_channel_localized_quick_search(?, ?, ?)",
                new Object[] { countryCode, text, language },
                (ResultSet rs, int ind) -> new Location(
                        rs.getLong("channel_id"),
                        rs.getString("full_localized_name"))
        );
    }

    @Override
    public List<AddressTO> searchAddress(String geoCode) throws UnsupportedEncodingException {
        Document dom;
        List<AddressTO> result = new ArrayList<>();
        String url = "https://geocode-maps.yandex.ru/1.x/?sco=latlong&geocode=" + URLEncoder.encode(geoCode, "UTF-8");
        try (InputStream in = (new URL(url).openStream())) {
            dom = DocumentHelper.parseText(IOUtils.toString(in, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        Map<String, String> namespaceUris = new HashMap<>();
        namespaceUris.put("gml", "http://www.opengis.net/gml");
        namespaceUris.put("geo", "http://maps.yandex.ru/geocoder/1.x");
        namespaceUris.put("a", "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0");
        for(Node node : DomHelper.getXPathNodes(dom, "//gml:featureMember", namespaceUris)) {
            if (!LOCALE_RU.getCountry().equals(
                    DomHelper.getXPathString(node, ".//geo:GeocoderMetaData/a:AddressDetails/a:Country/a:CountryNameCode", namespaceUris))) {
                continue;
            }

            AddressTO addr = new AddressTO();
            String pos = DomHelper.getXPathString(node, ".//gml:Point/gml:pos", namespaceUris);
            try (Scanner s = new Scanner(pos)) {
                addr.setLongitude(new BigDecimal(s.next()).setScale(4, BigDecimal.ROUND_HALF_UP));
                addr.setLatitude(new BigDecimal(s.next()).setScale(4, BigDecimal.ROUND_HALF_UP));
            }
            addr.setAddress(DomHelper.getXPathString(node, ".//geo:GeocoderMetaData/geo:text", namespaceUris));
            result.add(addr);
        }

        return result;
    }

    @Override
    @Transactional
    public Long createAddressChannel(Address address) {
        addressRepository.save(address);
        address.setName("ADDRESS " + address.getId()) ;
        return address.getId();
    }

    @Override
    public Long findAddressChannel(AddressTO addressView, String countryCode) {
        List<Long> ids = jdbcOperations.query("select channel_id from channel " +
                        " where country_code = ? " +
                        " and address = ? " +
                        " and latitude = ? " +
                        " and longitude = ? " +
                        " and radius = ? " +
                        " and radius_units = ? ",
                new Object[]{
                        countryCode,
                        addressView.getAddress(),
                        addressView.getLatitude(),
                        addressView.getLongitude(),
                        addressView.getRadius(),
                        addressView.getRadiusUnits().toString()
                },
                (ResultSet rs, int index) ->   rs.getLong("channel_id")
        );
        return ids.isEmpty() ? null : ids.get(0);
    }

    @Override
    public Long getCountryChannelId(String countryCode) {
        return jdbcOperations.queryForObject("select channel_id from channel where geo_type = 'CNTRY' and country_code= ?",
                new Object[]{ countryCode },
                Long.class);
    }
}
