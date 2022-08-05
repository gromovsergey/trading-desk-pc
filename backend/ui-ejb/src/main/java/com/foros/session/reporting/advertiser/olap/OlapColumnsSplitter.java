package com.foros.session.reporting.advertiser.olap;

import com.foros.reporting.meta.olap.OlapColumn;

import java.util.List;
import java.util.Set;

public abstract class OlapColumnsSplitter {
    protected final OlapColumnsSplitter next;

    protected OlapColumnsSplitter() {
        this(null);
    }

    protected OlapColumnsSplitter(OlapColumnsSplitter next) {
        this.next = next;
    }

    public abstract void process(OlapColumn column);

    public static class NetGross extends OlapColumnsSplitter {
        private OlapAdvertiserReportParameters.CostAndRates costAndRates;

        public NetGross(OlapColumnsSplitter next, OlapAdvertiserReportParameters.CostAndRates costAndRates) {
            super(next);
            this.costAndRates = costAndRates;
        }

        @Override
        public void process(OlapColumn column) {
            OlapAdvertiserMeta.NetGrossPair netGrossPair = OlapAdvertiserMeta.NET_GROSS_TRIPLETS.get(column);

            if (netGrossPair == null || costAndRates == null) {
                // keep selected
                next.process(column);
            } else {
                // replace with Net/Gross
                switch (costAndRates) {
                    case NET:
                        next.process(netGrossPair.getNet());
                        break;
                    case GROSS:
                        next.process(netGrossPair.getGross());
                        break;
                    case BOTH:
                        next.process(netGrossPair.getNet());
                        next.process(netGrossPair.getGross());
                        break;
                }
            }
        }
    }

    public static class WalledGarden extends OlapColumnsSplitter {

        public WalledGarden(OlapColumnsSplitter next) {
            super(next);
        }

        @Override
        public void process(OlapColumn column) {
            List<OlapColumn> wgPair = OlapAdvertiserMeta.WG_TRIPLETS.get(column);
            if (wgPair == null) {
                // keep selected
                next.process(column);
            } else {
                for (OlapColumn wgColumn : wgPair) {
                    next.process(wgColumn);
                }
            }
        }
    }

    public static class Result extends OlapColumnsSplitter {
        private Set<OlapColumn> result;

        public Result(Set<OlapColumn> result) {
            this.result = result;
        }

        @Override
        public void process(OlapColumn column) {
            result.add(column);
        }
    }
}
