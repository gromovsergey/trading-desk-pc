package com.foros.action.admin.country.ctra;

import com.foros.action.BaseActionSupport;
import com.foros.model.ctra.CTRAlgorithmData;
import com.foros.session.NamedTO;
import com.foros.session.admin.country.ctra.CTRAlgorithmService;

import com.opensymphony.xwork2.ModelDriven;
import java.util.Collection;
import java.util.LinkedHashSet;
import javax.ejb.EJB;

public class CTRAlgorithmActionSupport extends BaseActionSupport implements ModelDriven<CTRAlgorithmData> {
    @EJB
    protected CTRAlgorithmService ctrAlgorithmService;

    protected CTRAlgorithmData data = new CTRAlgorithmData();

    protected String campaignExclusionsText;

    protected Collection<Long> advertiserExclusionsIds = new LinkedHashSet<Long>();

    private Collection<NamedTO> namedAdvertiserExclusions;

    private String id;

    @Override
    public CTRAlgorithmData getModel() {
        return data;
    }

    public String getCampaignExclusionsText() {
        return campaignExclusionsText;
    }

    public Collection<NamedTO> getNamedAdvertiserExclusions() {
        if (namedAdvertiserExclusions == null) {
            namedAdvertiserExclusions = ctrAlgorithmService.displayAdvertisers(advertiserExclusionsIds);
        }
        return namedAdvertiserExclusions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
