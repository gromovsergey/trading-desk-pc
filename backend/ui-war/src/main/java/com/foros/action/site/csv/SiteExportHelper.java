package com.foros.action.site.csv;

import com.foros.action.bulk.CsvRow;
import com.foros.action.download.FileDownloadResult;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.serializer.CsvSerializer;
import com.foros.reporting.serializer.formatter.NullValueFormatter;
import com.foros.reporting.serializer.formatter.YesNoFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryChain;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.query.strategy.IterationStrategy;
import com.foros.reporting.tools.query.strategy.SimpleIterationStrategy;
import com.foros.security.currentuser.CurrentUserSettingsHolder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SiteExportHelper {

    public static final ValueFormatterRegistry FULL_REGISTRY = newRegistry();
    public static final ValueFormatterRegistry SITE_REGISTRY = newSiteRegistry();

    public static final String EXPORT_FILE_NAME = "Sites";

    private static ValueFormatterRegistry newSiteRegistry() {
        ValueFormatterRegistryImpl noTagRegistry = ValueFormatterRegistries.registry();
        for (SiteFieldCsv column : SiteFieldCsv.values()) {
            if (Tag.class.equals(column.getBeanType())) {
                noTagRegistry.column(column, NullValueFormatter.INSTANCE);
            }
        }
        ValueFormatterRegistryChain chain = ValueFormatterRegistries.chain();
        chain.registry(FULL_REGISTRY);
        chain.registry(noTagRegistry);
        return chain;
    }

    private SiteExportHelper() {
    }

    private static String composeFileName(String fileName) {
        return (fileName + ".csv").replaceAll(" ", "_");
    }

    public static void serialize(
            HttpServletRequest request,
            HttpServletResponse response,
            String fileName,
            java.util.Collection<Site> sites,
            MetaData<SiteFieldCsv> metaData) throws IOException {
        FileDownloadResult.setDownloadHeaders(request, response, composeFileName(fileName));
        response.setHeader("Content-type", "text/csv");

        OutputStream os = response.getOutputStream();
        serialize(os, sites, metaData);
    }

    public static void serialize(OutputStream os, Collection<Site> sites, MetaData<SiteFieldCsv> metaData) {
        Locale locale = CurrentUserSettingsHolder.getLocale();
        CsvSerializer serializer = new CsvSerializer(os, locale, Integer.MAX_VALUE);
        serializer.registry(FULL_REGISTRY, SiteCsvRow.SITE_TAG);
        serializer.registry(SITE_REGISTRY, SiteCsvRow.SITE);
        serializer.registry(ValueFormatterRegistries.defaultUnparsedRowRegistry(), CsvRow.UNPARSED_ROW_TYPE);
        IterationStrategy is = new SimpleIterationStrategy(metaData);
        is.process(new SiteRowSource(metaData, new SiteTagsIterator(sites, metaData.getColumns().contains(SiteFieldCsv.ValidationStatus))), serializer);
    }

    private static ValueFormatterRegistry newRegistry() {
        ValueFormatterRegistryImpl custom = ValueFormatterRegistries.registry();
        custom.column(SiteFieldCsv.TAG_PRICING, new TagPricingsFormatter());
        custom.column(SiteFieldCsv.TAG_SIZES, new CreativeSizesFormatter());
        custom.column(SiteFieldCsv.TAG_ALLOW_EXPANDABLE, new YesNoFormatter());
        return ValueFormatterRegistries.bulkDefaultAnd(custom);
    }
}
