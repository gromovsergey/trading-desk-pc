package com.foros.model.report.birt;

import java.util.HashMap;
import java.util.Map;

public enum BirtReportType {

    DEFAULT(restrictions()
            .restriction(RestrictionType.GET, "BirtReport.defaultGet")
            .restriction(RestrictionType.RUN, "BirtReport.defaultRun")
            .restriction(RestrictionType.UPDATE, "BirtReport.defaultUpdate")
            .asMap()),

    INVOICE(restrictions()
            .restriction(RestrictionType.GET, "BirtReport.invoiceGet")
            .restriction(RestrictionType.RUN, "BirtReport.invoiceRun")
            .restriction(RestrictionType.UPDATE, "BirtReport.invoiceUpdate")
            .asMap());

    private Map<RestrictionType, String> restrictions;

    private BirtReportType(Map<RestrictionType, String> restrictions) {
        this.restrictions = restrictions;
    }

    public Long getId() {
        return (long) ordinal();
    }

    public String getName() {
        return name();
    }

    public String getRestriction(RestrictionType type) {
        return restrictions.get(type);
    }

    public static enum RestrictionType {
        GET, RUN, UPDATE
    }

    private static RestrictionBuilder restrictions() {
        return new RestrictionBuilder();
    }

    private static class RestrictionBuilder {
        private Map<RestrictionType, String> restrictions = new HashMap<RestrictionType, String>();

        private RestrictionBuilder restriction(RestrictionType type, String restriction) {
            restrictions.put(type, restriction);
            return this;
        }

        private Map<RestrictionType, String> asMap() {
            return restrictions;
        }
    }
}