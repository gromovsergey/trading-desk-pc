package com.foros.jaxb.adapters;

import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.ResolvableMetaData;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta;
import com.foros.session.reporting.advertiser.olap.OlapDisplayAdvertiserMeta;
import com.foros.session.reporting.advertiser.olap.OlapTextAdvertiserMeta;
import com.foros.validation.code.InputErrors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AdvertiserReportColumnsAdapter extends XmlAdapter<AdvertiserReportColumnsAdapter.XCollection, Set<String>> {

    private static final Map<String, String> fieldToName = new LinkedHashMap<String, String>();
    private static final Map<String, String> nameToField = new LinkedHashMap<String, String>();

    static {
        Set fieldsFromMeta = findAllFields();

        Field[] fields = OlapAdvertiserMeta.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.endsWith("_FOROS") || fieldName.endsWith("_WG") || fieldName.endsWith("_NET") || fieldName.endsWith("_GROSS")) {
                // can't directly specify those columns instead of Self Service Cost
                if (!fieldName.startsWith("SELF_SERVICE_COST")) {
                    continue;
                }
            }
            Object value = get(field);
            if (fieldsFromMeta.contains(value)) {
                Column column = (Column) value;
                fieldToName.put(fieldName, column.getNameKey());
                nameToField.put(column.getNameKey(), fieldName);
            }
        }
    }

    private static Object get(Field field) {
        Object value;
        try {
            value = field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    private static Set findAllFields() {
        List<Class<? extends OlapAdvertiserMeta>> metas = Arrays.asList(OlapTextAdvertiserMeta.class, OlapDisplayAdvertiserMeta.class);
        Set res = new HashSet<>();

        for (Class<? extends OlapAdvertiserMeta> clazz : metas) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if(field.getType().isAssignableFrom(ResolvableMetaData.class)) {
                    ResolvableMetaData rm = (ResolvableMetaData) get(field);
                    //noinspection unchecked
                    res.addAll(rm.getColumns());
                }
            }
        }
        return res;
    }

    public static Set<String> allowedColumns() {
        return Collections.unmodifiableSet(fieldToName.keySet());
    }


    public static String toAPIName(String nameKey) {
        return nameToField.get(nameKey);
    }

    @Override
    public Set<String> unmarshal(XCollection v) throws Exception {
        if (v == null || v.list == null) {
            return null;
        }

        return convert(v.list, new LinkedHashSet<String>(v.list.size()), fieldToName);
    }

    @Override
    public XCollection marshal(Set<String> list) throws Exception {
        if (list == null) {
            return null;
        }

        List<String> res = convert(list, new ArrayList<String>(list.size()), nameToField);
        return new XCollection(res);
    }

    private <T extends Collection<String>> T convert(Collection<String> src, T res, Map<String, String> namesMap) {
        List<String> invalidColumns = null;
        for (String str : src) {
            String converted = namesMap.get(str);
            if (converted == null) {
                if (invalidColumns == null) {
                    invalidColumns = new ArrayList<>();
                }
                invalidColumns.add(str);
            }
            res.add(converted);
        }
        if (invalidColumns != null) {
            throw new LocalizedParseException(
                    InputErrors.XML_ENUM_PARSE_ERROR,
                    "errors.unexpectedEnumValues",
                    invalidColumns, allowedColumns()
            );
        }
        return res;
    }

    @XmlType
    @XmlRootElement
    public static final class XCollection {
        @XmlElement(name = "column")
        public Collection<String> list;

        public XCollection() {
        }

        public XCollection(Collection<String> list) {
            this.list = list;
        }
    }
}
