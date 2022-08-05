package com.foros.session.channel.geo;

import com.foros.audit.serialize.serializer.DomHelper;
import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.Country;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.GeoChannelAddress;
import com.foros.model.channel.GeoType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingInterceptor;
import com.foros.session.admin.country.CountryService;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.channel.service.ChannelFieldsPreparer;
import com.foros.session.security.AuditService;
import com.foros.session.status.StatusService;
import com.foros.util.PersistenceUtils;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

@LocalBean
@Stateless
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class GeoChannelServiceBean implements GeoChannelService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @EJB
    private CountryService countryService;

    @EJB
    private ValidationService validationService;

    @EJB
    protected StatusService statusService;

    protected ChannelFieldsPreparer channelFieldsPreparer = new ChannelFieldsPreparer() {
        @Override
        protected EntityManager getEM() {
            return em;
        }
    };

    @Override
    public GeoChannel view(Long id) {
        if (id == null) {
            throw new EntityNotFoundException();
        }
        GeoChannel geoChannel = em.find(GeoChannel.class, id);
        if (geoChannel == null) {
            throw new EntityNotFoundException();
        }
        PersistenceUtils.initialize(geoChannel.getChildChannels());
        return geoChannel;
    }

    @Override
    public GeoChannel find(Long id) {
        if (id == null) {
            throw new EntityNotFoundException();
        }
        GeoChannel geoChannel = em.find(GeoChannel.class, id);
        if (geoChannel == null) {
            throw new EntityNotFoundException();
        }
        return geoChannel;
    }

    @Override
    public Collection<GeoChannel> getStates(Country country) {
        return em.createNamedQuery("GeoChannel.findStates", GeoChannel.class)
                .setParameter("countryCode", country.getCountryCode())
                .getResultList();
    }

    @Override
    public Collection<GeoChannel> getOrphanCities(Country country) {
        return em.createNamedQuery("GeoChannel.orphanCities", GeoChannel.class)
                .setParameter("countryCode", country.getCountryCode())
                .getResultList();
    }

    @Override
    public GeoChannel findCountryChannel(String countryCode) {
        try {
            return (GeoChannel) em.createQuery("select g from GeoChannel g " +
                    " where g.geoType='CNTRY' and g.country.countryCode = :countryCode")
                    .setParameter("countryCode", countryCode)
                    .getSingleResult();
        } catch (NoResultException e) {
            // TODO OUI-26357 Create fake country to make FOROS works
            Country country = countryService.find(countryCode);
            GeoChannel geoChannel = new GeoChannel();
            geoChannel.setCountry(country);
            geoChannel.setName( StringUtil.resolveGlobal("country", countryCode, false, Locale.US));
            geoChannel.setGeoType(GeoType.CNTRY);
            return geoChannel;
            // TODO OUI-26357 throw new EntityNotFoundException("Geo channel for for country " + countryCode + " not found");
        }
    }

    @Override
    public List<GeoChannelAddress> searchRUAddress(String geoCode) {
        Document dom;
        List<GeoChannelAddress> result = new ArrayList<>();
        String url = "https://geocode-maps.yandex.ru/1.x/?sco=latlong&geocode=" + geoCode;
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
            if (!"RU".equals(DomHelper.getXPathString(node, ".//geo:GeocoderMetaData/a:AddressDetails/a:Country/a:CountryNameCode", namespaceUris))) {
                continue;
            }

            GeoChannelAddress addr = new GeoChannelAddress();
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
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "GeoChannel.createAddress")
    public GeoChannel findOrCreateAddressChannel(GeoChannel channel) {
        if (channel.getId() == null) {
            GeoChannel existing = findGeoChannelByAddress(channel);
            if (existing != null) {
                return existing;
            }

            validationService.validate("GeoChannel.createAddress", channel).throwIfHasViolations();
            return createAddressChannel(channel);
        }

        return em.find(GeoChannel.class, channel.getId());
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "GeoChannel.createAddress")
    @Validate(validation = "GeoChannel.createAddress", parameters = {"#channel"})
    public GeoChannel createAddressChannel(GeoChannel channel) {
        channel.setName("ADDRESS_");
        ChannelFieldsPreparer.initializeStatuses(channel);
        channel.setDisplayStatus(Channel.LIVE);
        ChannelFieldsPreparer.initializeQaStatus(channel);
        ChannelFieldsPreparer.initializeId(channel);
        channelFieldsPreparer.prepareCountry(channel);
        channel.setVisibility(ChannelVisibility.PUB);
        channel.setParentChannel(findCountryChannel(channel.getCountry().getCountryCode()));

        em.persist(channel);

        channel.setName("ADDRESS_" + channel.getId());
        return channel;
    }

    private GeoChannel findGeoChannelByAddress(GeoChannel channel) {
        try {
            return em.createNamedQuery("GeoChannel.addresses", GeoChannel.class).
                    setParameter("countryCode", channel.getCountry().getCountryCode()).
                    setParameter("address", channel.getAddress()).
                    setParameter("longitude", channel.getCoordinates().getLongitude().setScale(4, BigDecimal.ROUND_HALF_UP)).
                    setParameter("latitude", channel.getCoordinates().getLatitude().setScale(4, BigDecimal.ROUND_HALF_UP)).
                    setParameter("radius", channel.getRadius().getDistance()).
                    setParameter("radiusUnit", channel.getRadius().getRadiusUnit()).
                    getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Restrict(restriction = "GeoChannel.update", parameters = "find('GeoChannel', #channelId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void delete(Long channelId) {
        statusService.delete(find(channelId));
    }

    @Override
    @Restrict(restriction = "GeoChannel.undelete", parameters = "find('GeoChannel', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void undelete(Long channelId) {
        statusService.undelete(find(channelId));
    }
}
