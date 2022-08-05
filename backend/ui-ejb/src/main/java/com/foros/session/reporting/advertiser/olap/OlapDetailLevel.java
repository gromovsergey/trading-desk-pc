package com.foros.session.reporting.advertiser.olap;

import com.phorm.oix.olap.OlapIdentifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum OlapDetailLevel {
    Account(
            OlapTextAdvertiserMeta.ACCOUNT_PERFORMANCE_DESC,
            OlapDisplayAdvertiserMeta.ACCOUNT_PERFORMANCE_DESC,
            OlapVideoAdvertiserMeta.ACCOUNT_PERFORMANCE_DESC,
            OlapGeneralAdvertiserMeta.ACCOUNT_PERFORMANCE_DESC,
            "advertiser_report_cc",
            new Filter[] {},
            Filter.ADV_CUBE_IDENTIFIERS
    ),

    Advertiser(
            OlapTextAdvertiserMeta.ADVERTISER_PERFORMANCE_DESC,
            OlapDisplayAdvertiserMeta.ADVERTISER_PERFORMANCE_DESC,
            OlapVideoAdvertiserMeta.ADVERTISER_PERFORMANCE_DESC,
            OlapGeneralAdvertiserMeta.ADVERTISER_PERFORMANCE_DESC,
            "advertiser_report_cc",
            new Filter[] {
                    Filter.Advertiser
            },
            Filter.ADV_CUBE_IDENTIFIERS
    ),

    Campaign(
            OlapTextAdvertiserMeta.CAMPAIGN_PERFORMANCE_DESC,
            OlapDisplayAdvertiserMeta.CAMPAIGN_PERFORMANCE_DESC,
            OlapVideoAdvertiserMeta.CAMPAIGN_PERFORMANCE_DESC,
            OlapGeneralAdvertiserMeta.CAMPAIGN_PERFORMANCE_DESC,
            "advertiser_report_cc",
            new Filter[] {
                    Filter.Advertiser,
                    Filter.Campaign
            },
            Filter.ADV_CUBE_IDENTIFIERS
    ),

    CreativeGroup(
            null,
            OlapDisplayAdvertiserMeta.CCG_PERFORMANCE_DESC,
            OlapVideoAdvertiserMeta.CCG_PERFORMANCE_DESC,
            OlapGeneralAdvertiserMeta.CCG_PERFORMANCE_DESC,
            "advertiser_report_ccg",
            new Filter[] {
                    Filter.Advertiser,
                    Filter.Campaign,
                    Filter.Group
            },
            Filter.CCG_CUBE_IDENTIFIERS
    ),

    AdGroup(
            OlapTextAdvertiserMeta.CCG_PERFORMANCE_DESC,
            null,
            null,
            null,
            "advertiser_report_ccg",
            new Filter[] {
                    Filter.Advertiser,
                    Filter.Campaign,
                    Filter.Group
            },
            Filter.CCG_CUBE_IDENTIFIERS
    ),

    Creative(
            null,
            OlapDisplayAdvertiserMeta.CREATIVE_PERFORMANCE_DESC,
            OlapVideoAdvertiserMeta.CREATIVE_PERFORMANCE_DESC,
            OlapGeneralAdvertiserMeta.CREATIVE_PERFORMANCE_DESC,
            "advertiser_report_cc",
            new Filter[] {
                    Filter.Advertiser,
                    Filter.Campaign,
                    Filter.Group,
                    Filter.Creative
            },
            Filter.ADV_CUBE_IDENTIFIERS
    ),

    TextAd(
            OlapTextAdvertiserMeta.TEXT_AD_PERFORMANCE_DESC,
            null,
            null,
            null,
            "advertiser_report_cc",
            new Filter[] {
                    Filter.Advertiser,
                    Filter.Campaign,
                    Filter.Group,
                    Filter.Creative
            },
            Filter.ADV_CUBE_IDENTIFIERS
    ),

    Keyword(
            OlapTextAdvertiserMeta.KEYWORD_PERFORMANCE_DESC,
            null,
            null,
            null,
            "advertiser_report_kw",
            new Filter[] {
                    Filter.Advertiser,
                    Filter.Campaign,
                    Filter.Group,
                    Filter.Creative,
                    Filter.Keyword
            },
            Filter.KW_CUBE_IDENTIFIERS
    );

    public static final Collection<OlapDetailLevel> DISPLAY_DETAIL_LEVELS = Collections.unmodifiableCollection(Arrays.asList(
            Account,
            Advertiser,
            Campaign,
            CreativeGroup,
            Creative
    ));

    public static final Collection<OlapDetailLevel> TEXT_DETAIL_LEVELS = Collections.unmodifiableCollection(Arrays.asList(
            Account,
            Advertiser,
            Campaign,
            AdGroup,
            TextAd,
            Keyword
    ));

    public static final Collection<OlapDetailLevel> VIDEO_DETAIL_LEVELS = Collections.unmodifiableCollection(Arrays.asList(
            Account,
            Advertiser,
            Campaign,
            CreativeGroup,
            Creative
    ));

    private final OlapAdvertiserReportDescription textDescription;
    private final OlapAdvertiserReportDescription displayDescription;
    private final OlapAdvertiserReportDescription videoDescription;
    private final OlapAdvertiserReportDescription generalDescription;
    private final String cube;
    private final Map<Filter, OlapIdentifier> cubeIdentifiers;
    private final Set<Filter> availableFilters;

    OlapDetailLevel(
            OlapAdvertiserReportDescription textDescription,
            OlapAdvertiserReportDescription displayDescription,
            OlapAdvertiserReportDescription videoDescription,
            OlapAdvertiserReportDescription generalDescription,
            String cube,
            Filter[] filters,
            Map<Filter, OlapIdentifier> cubeIdentifiers
    ) {
        this.displayDescription = displayDescription;
        this.textDescription = textDescription;
        this.videoDescription = videoDescription;
        this.generalDescription = generalDescription;
        this.cube = cube;
        this.cubeIdentifiers = cubeIdentifiers;
        this.availableFilters = Collections.unmodifiableSet(new HashSet<Filter>(Arrays.asList(filters)));
    }

    public String getNameKey() {
        return  "report.input.field.reportType." + name();
    }

    public OlapAdvertiserReportDescription getDisplayDescription() {
        return displayDescription;
    }

    public OlapAdvertiserReportDescription getTextDescription() {
        return textDescription;
    }

    public OlapAdvertiserReportDescription getVideoDescription() {
        return videoDescription;
    }

    public OlapAdvertiserReportDescription getGeneralDescription() {
        return generalDescription;
    }

    public String getCube() {
        return cube;
    }

    public Set<Filter> getAvailableFilters() {
        return availableFilters;
    }

    public OlapIdentifier resolveIdentifier(Filter filter) {
        return cubeIdentifiers.get(filter);
    }

    public enum Filter {
        Type,
        Agency,
        Advertiser,
        Campaign,
        Group,
        Creative,
        Keyword,
        Size;

        private static final Map<Filter, OlapIdentifier> ADV_CUBE_IDENTIFIERS = new HashMap<Filter, OlapIdentifier>() {{
            put(Filter.Type, OlapAdvertiserMeta.Levels.CCG_TYPE);
            put(Filter.Agency, OlapAdvertiserMeta.Levels.AGENCY_ID_ADV);
            put(Filter.Advertiser, OlapAdvertiserMeta.Levels.ADVERTISER_ID_ADV);
            put(Filter.Campaign, OlapAdvertiserMeta.Levels.CAMPAIGN_ID_ADV);
            put(Filter.Group, OlapAdvertiserMeta.Levels.CREATIVE_GROUP_ID_ADV);
            put(Filter.Creative, OlapAdvertiserMeta.Levels.CC_ID_ADV);
            put(Filter.Keyword, OlapAdvertiserMeta.Levels.KEYWORD_NAME);
            put(Filter.Size, OlapAdvertiserMeta.Levels.CREATIVE_SIZE);
        }};

        private static final Map<Filter, OlapIdentifier> CCG_CUBE_IDENTIFIERS = new HashMap<Filter, OlapIdentifier>() {{
            put(Filter.Type, OlapAdvertiserMeta.Levels.CCG_TYPE);
            put(Filter.Agency, OlapAdvertiserMeta.Levels.AGENCY_ID_ADV);
            put(Filter.Advertiser, OlapAdvertiserMeta.Levels.ADVERTISER_ID_ADV);
            put(Filter.Campaign, OlapAdvertiserMeta.Levels.CAMPAIGN_ID_CCG);
            put(Filter.Group, OlapAdvertiserMeta.Levels.CREATIVE_GROUP_ID_CCG);
        }};

        private static final Map<Filter, OlapIdentifier> KW_CUBE_IDENTIFIERS = new HashMap<Filter, OlapIdentifier>() {{
            put(Filter.Agency, OlapAdvertiserMeta.Levels.AGENCY_ID_ADV);
            put(Filter.Advertiser, OlapAdvertiserMeta.Levels.ADVERTISER_ID_ADV);
            put(Filter.Campaign, OlapAdvertiserMeta.Levels.CAMPAIGN_ID_ADV);
            put(Filter.Group, OlapAdvertiserMeta.Levels.CREATIVE_GROUP_ID_ADV);
            put(Filter.Creative, OlapAdvertiserMeta.Levels.CC_ID_ADV);
            put(Filter.Keyword, OlapAdvertiserMeta.Levels.KEYWORD_NAME);
            put(Filter.Size, OlapAdvertiserMeta.Levels.CREATIVE_SIZE);
        }};

    }
}
