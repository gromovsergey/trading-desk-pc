package com.foros.action.admin.geoChannel;

import com.foros.action.BaseActionSupport;
import com.foros.session.channel.geo.GeoChannelService;

import javax.ejb.EJB;

public class StatusGeoChannelAction extends BaseActionSupport {

    @EJB
    private GeoChannelService geoChannelService;

    private Long id;

    public String delete() {
        geoChannelService.delete(id);
        return SUCCESS;
    }

    public String undelete() {
        geoChannelService.undelete(id);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
