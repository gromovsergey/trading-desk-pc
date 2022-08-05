package com.foros.jaxb.adapters;

import com.foros.reporting.meta.DbColumn;
import com.foros.session.reporting.conversions.ConversionsMeta;
import com.foros.validation.code.InputErrors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ConversionsReportColumnsAdapter extends XmlAdapter<ConversionsReportColumnsAdapter.ConversionsReportColumns, List<String>> {
    private static final Map<String, String> marshallerMap = initMarshallerMap();
    private static final Map<String, String> unmarshallerMap = initUnmarshallerMap();

    @Override
    public List<String> unmarshal(ConversionsReportColumnsAdapter.ConversionsReportColumns v) throws Exception {
        if (v == null || v.list == null) {
            return null;
        }

        ArrayList<String> columnsWithError = new ArrayList<>(v.list.size());
        ArrayList<String> result = new ArrayList<>(v.list.size());
        for (String value : v.list) {
            String mappedValue = unmarshallerMap.get(value);
            if (mappedValue == null) {
                columnsWithError.add(value);
                continue;
            }
            result.add(mappedValue);
        }

        throwOnError(columnsWithError, unmarshallerMap.keySet());
        return result;
    }

    @Override
    public ConversionsReportColumnsAdapter.ConversionsReportColumns marshal(List<String> list) throws Exception {
        if (list == null) {
            return null;
        }

        ArrayList<String> columnsWithError = new ArrayList<>(list.size());
        ArrayList<String> result = new ArrayList<>(list.size());
        for (String value : list) {
            String mappedValue = marshallerMap.get(value);
            if (mappedValue == null) {
                columnsWithError.add(value);
                continue;
            }
            result.add(mappedValue);
        }

        throwOnError(columnsWithError, marshallerMap.keySet());
        return new ConversionsReportColumnsAdapter.ConversionsReportColumns(result);
    }

    @XmlType
    @XmlRootElement
    public static final class ConversionsReportColumns {
        @XmlElement(name = "column")
        public Collection<String> list;

        public ConversionsReportColumns() {
        }

        public ConversionsReportColumns(Collection<String> list) {
            this.list = list;
        }
    }

    private void throwOnError(List<String> columnsWithError, Set<String> allowedColumns) {
        if (!columnsWithError.isEmpty()) {
            throw new LocalizedParseException(
                    InputErrors.XML_ENUM_PARSE_ERROR,
                    "errors.unexpectedEnumValues",
                    columnsWithError, allowedColumns
            );
        }
    }

    private static final Map<String, String> initMarshallerMap() {
        HashMap<String, String> result = new HashMap<>(NameMapping.values().length);
        for (NameMapping value : NameMapping.values()) {
            result.put(value.getDbColumn().getNameKey(), value.toString());
        }
        return result;
    }

    private static final Map<String, String> initUnmarshallerMap() {
        HashMap<String, String> result = new HashMap<>(NameMapping.values().length);
        for (NameMapping value : NameMapping.values()) {
            result.put(value.toString(), value.getDbColumn().getNameKey());
        }
        return result;
    }

    private enum NameMapping {
        IMPRESSIONS(ConversionsMeta.IMPRESSIONS),
        CLICKS(ConversionsMeta.CLICKS),
        CTR(ConversionsMeta.CTR),
        POST_IMP_CONV(ConversionsMeta.POST_IMP_CONV),
        POST_IMP_CR(ConversionsMeta.POST_IMP_CR),
        POST_CLICK_CONV(ConversionsMeta.POST_CLICK_CONV),
        POST_CLICK_CR(ConversionsMeta.POST_CLICK_CR),
        COST(ConversionsMeta.COST),
        REVENUE(ConversionsMeta.REVENUE),
        ROI(ConversionsMeta.ROI),
        TTC_IMPRESSIONS(ConversionsMeta.TTC_IMPRESSIONS),
        TTC_CLICKS(ConversionsMeta.TTC_CLICKS),
        POST_IMP_1(ConversionsMeta.POST_IMP_1),
        POST_IMP_2_7(ConversionsMeta.POST_IMP_2_7),
        POST_IMP_8_30(ConversionsMeta.POST_IMP_8_30),
        POST_CLICK_1(ConversionsMeta.POST_CLICK_1),
        POST_CLICK_2_7(ConversionsMeta.POST_CLICK_2_7),
        POST_CLICK_8_30(ConversionsMeta.POST_CLICK_8_30),
        DATE(ConversionsMeta.DATE),
        ADVERTISER_ID(ConversionsMeta.ADVERTISER_ID),
        ADVERTISER_VISIBLE(ConversionsMeta.ADVERTISER_VISIBLE),
        ADVERTISER(ConversionsMeta.ADVERTISER),
        CAMPAIGN_ID(ConversionsMeta.CAMPAIGN_ID),
        CAMPAIGN_VISIBLE(ConversionsMeta.CAMPAIGN_VISIBLE),
        CAMPAIGN(ConversionsMeta.CAMPAIGN),
        CREATIVE_GROUP_ID(ConversionsMeta.CREATIVE_GROUP_ID),
        CREATIVE_GROUP_VISIBLE(ConversionsMeta.CREATIVE_GROUP_VISIBLE),
        CREATIVE_GROUP(ConversionsMeta.CREATIVE_GROUP),
        CHANNEL_ID(ConversionsMeta.CHANNEL_ID),
        CHANNEL_VISIBLE(ConversionsMeta.CHANNEL_VISIBLE),
        CHANNEL_ACCOUNT_ROLE_ID(ConversionsMeta.CHANNEL_ACCOUNT_ROLE_ID),
        CHANNEL(ConversionsMeta.CHANNEL),
        CREATIVE_ID(ConversionsMeta.CREATIVE_ID),
        CREATIVE_VISIBLE(ConversionsMeta.CREATIVE_VISIBLE),
        CREATIVE(ConversionsMeta.CREATIVE),
        CONVERSION_ID(ConversionsMeta.CONVERSION_ID),
        CONVERSION_VISIBLE(ConversionsMeta.CONVERSION_VISIBLE),
        CONVERSION(ConversionsMeta.CONVERSION),
        CONVERSION_CATEGORY(ConversionsMeta.CONVERSION_CATEGORY),
        ORDER_ID(ConversionsMeta.ORDER_ID),
        PUBLISHER_ID(ConversionsMeta.PUBLISHER_ID),
        PUBLISHER_VISIBLE(ConversionsMeta.PUBLISHER_VISIBLE),
        PUBLISHER(ConversionsMeta.PUBLISHER);

        private final DbColumn dbColumn;

        NameMapping(DbColumn dbColumn) {
            this.dbColumn = dbColumn;
        }

        public DbColumn getDbColumn() {
            return dbColumn;
        }
    }
}
