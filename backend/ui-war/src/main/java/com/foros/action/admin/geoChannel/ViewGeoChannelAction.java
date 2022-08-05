package com.foros.action.admin.geoChannel;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.channel.GeoChannel;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.geo.GeoChannelService;
import com.foros.util.EntityUtils;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;
import java.util.LinkedHashSet;
import java.util.Set;

public class ViewGeoChannelAction extends BaseActionSupport implements ModelDriven<GeoChannel>, BreadcrumbsSupport {
    @EJB
    private GeoChannelService geoChannelService;

    @EJB
    private CountryService countryService;

    @EJB
    private CurrentUserService currentUserService;

    private String stateLabel;
    private String cityLabel;

    private GeoChannel geoChannel = new GeoChannel();

    @ReadOnly
    @Restrict(restriction = "GeoChannel.view", parameters = "#target.model.id")
    public String view() {
        geoChannel = geoChannelService.view(geoChannel.getId());
        checkDeletedChildren(geoChannel);
        Country country = countryService.find(geoChannel.getCountry().getCountryCode());
        GeoChannelHelper helper = new GeoChannelHelper(country);
        stateLabel = helper.getStateLabel();
        cityLabel = helper.getCityLabel();
        return SUCCESS;
    }

    @Override
    public GeoChannel getModel() {
        return geoChannel;
    }

    public String getStateLabel() {
        return stateLabel;
    }

    public String getCityLabel() {
        return cityLabel;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new GeoChannelsBreadCrumbsElement()).add(new GeoChannelBreadCrumbsElement(geoChannel));
    }

    private void checkDeletedChildren(GeoChannel target) {
        boolean deletedObjectsVisible = currentUserService.getUser().isDeletedObjectsVisible();
        Set<GeoChannel> result = new LinkedHashSet<>(target.getChildChannels().size());
        for (GeoChannel channel : target.getChildChannels()) {
            if (channel.getStatus().equals(Status.DELETED)) {
                if (!deletedObjectsVisible) {
                    continue;
                }
                channel.setName(EntityUtils.appendStatusSuffix(channel.getName(), Status.DELETED));
            }
            result.add(channel);
        }
        target.setChildChannels(result);
    }
}
