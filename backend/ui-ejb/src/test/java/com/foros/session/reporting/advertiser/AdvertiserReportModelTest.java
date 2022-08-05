package com.foros.session.reporting.advertiser;

import com.foros.AbstractUnitTest;
import com.foros.jaxb.adapters.AdvertiserReportColumnsAdapter;
import com.foros.jaxb.adapters.LocalizedParseException;
import com.foros.model.JAXBTest;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta;
import com.foros.util.CollectionUtils;
import com.foros.util.StringUtil;
import com.foros.util.mapper.Converter;

import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSchema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;

public class AdvertiserReportModelTest extends AbstractUnitTest {
    @Test
    public void testColumns() throws Exception {
        XSSchema xsSchema = JAXBTest.loadXSSchema();
        List<XSFacet> columnFacets = xsSchema.getSimpleType("AdvertiserReportColumn").getFacets("enumeration");
        List<String> columns = CollectionUtils.convert(columnFacets, new Converter<XSFacet, String>() {
            @Override
            public String item(XSFacet value) {
                return value.getValue().toString();
            }
        });


        Set<String> allowed = AdvertiserReportColumnsAdapter.allowedColumns();

        Set<String> excessive = new TreeSet<>(columns);
        excessive.removeAll(allowed);
        if (!excessive.isEmpty()) {
            Assert.fail("Excessive columns:\n" + excessive + "\n" + getAllowedXsEnumeration(allowed));
        }

        Set<String> missing = new TreeSet<>(allowed);
        missing.removeAll(columns);
        if (!missing.isEmpty()) {
            Assert.fail("Missing columns:\n" + missing + "\n" + getAllowedXsEnumeration(allowed));
        }
    }

    private String getAllowedXsEnumeration(Set<String> allowed) {
        List<String> tags = CollectionUtils.convert(allowed, new Converter<String, String>() {
            @Override
            public String item(String value) {
                return String.format("<xs:enumeration value=\"%s\" />", value);
            }
        });
        return "=====\n" + StringUtil.join(tags) + "\n====\n";
    }

    @Test
    public void testUnmarshalling() throws Exception {
        List<String> unmarshalled = new ArrayList<>(unmarshalColumns(Arrays.asList("IMPRESSIONS", "CLICKS")));
        Assert.assertEquals(
                Arrays.asList(OlapAdvertiserMeta.IMPRESSIONS.getNameKey(), OlapAdvertiserMeta.CLICKS.getNameKey()),
                unmarshalled
        );

        try {
            unmarshalColumns(Arrays.asList("BAD"));
            Assert.fail();
        } catch (LocalizedParseException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    private Set<String> unmarshalColumns(List<String> impressions) throws Exception {
        return new AdvertiserReportColumnsAdapter().unmarshal(new AdvertiserReportColumnsAdapter.XCollection(impressions));
    }
}
