package com.foros.action.xml.generator;


import com.foros.model.channel.GeoChannelAddress;
import com.foros.util.NumberUtil;

public class GeoCoordinatesGenerator implements Generator<GeoChannelAddress> {
    @Override
    public String generate(GeoChannelAddress model) {
        StringBuilder res = new StringBuilder(Constants.XML_HEADER);
        res.append("<coordinates>")
                .append("<latitude>").append(model.getLatitude() != null ? NumberUtil.formatNumber(model.getLatitude(), 4) : "").append("</latitude>")
                .append("<longitude>").append(model.getLongitude() != null ? NumberUtil.formatNumber(model.getLongitude(), 4) : "").append("</longitude>")
                .append("</coordinates>");
        return res.toString();
    }
}
