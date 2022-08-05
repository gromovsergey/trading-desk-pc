package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.action.BaseActionSupport;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.EntityTO;
import com.foros.session.bulk.BulkOperation;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignService;
import com.foros.util.StringUtil;
import com.foros.util.comparator.StatusNameTOComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.ejb.EJB;

public class CcgEditBulkActionSupport extends BaseActionSupport {
    @EJB
    protected CampaignService campaignService;

    @EJB
    protected CampaignCreativeGroupService ccgService;

    // params
    protected Long campaignId;
    protected List<Long> ids = new ArrayList<>();

    // data
    private Set<Long> idsWithError = new LinkedHashSet<>();
    private List<EntityTO> groupsWithError;
    private Campaign campaign;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Campaign getCampaign() {
        if (campaign == null) {
            campaign = campaignService.find(campaignId);
        }
        return campaign;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Set<Long> getIdsWithError() {
        return idsWithError;
    }

    public AdvertiserAccount getAccount() {
        return getCampaign().getAccount();
    }

    public List<EntityTO> getGroupsWithError() {
        if (groupsWithError == null) {
            groupsWithError = idsWithError.isEmpty() ? Collections.<EntityTO>emptyList() :  ccgService.getIndexByIds(idsWithError);
        }
        return groupsWithError;
    }

    protected void perform(BulkOperation<CampaignCreativeGroup> operation) {
        ccgService.perform(getAccount().getId(), ids, operation);
    }

    public String addGroupError(int index) {
        Long id = ids.get(index);
        idsWithError.add(id);
        return "groups[" + id + "]";
    }

    public Map<String, Set<EntityTO>> getGroupsByError() {
        Map<String, Set<EntityTO>> map = new TreeMap<>(StringUtil.getLexicalComparator());
        Map<String, List<String>> fieldErrors = getFieldErrors();

        for (EntityTO to : getGroupsWithError()) {
            List<String> errors = fieldErrors.get("groups[" + to.getId() + "]");
            for (String error : errors) {
                Set<EntityTO> tos = map.get(error);
                if (tos == null) {
                    tos = new TreeSet<>(StatusNameTOComparator.INSTANCE);
                    map.put(error, tos);
                }
                tos.add(to);
            }
        }
        return map;
    }
}
