package com.foros.session.site.creativeApproval;

import com.foros.model.site.SiteCreativeApproval;
import com.foros.model.site.ThirdPartyCreative;
import com.foros.session.query.PartialList;

import java.util.List;

import javax.ejb.Local;

@Local
public interface SiteCreativeApprovalService {
    SiteCreativeApproval update(Long siteId, SiteCreativeApprovalOperation operation);

    PartialList<SiteCreativeApprovalTO> searchCreativeApprovals(CreativeExclusionBySiteSelector selector);

    PartialList<ThirdPartyCreative> searchThirdParty(CreativeExclusionBySiteSelector selector);

    void perform(SiteCreativeApprovalOperations approvalOperations);

    void perform(ThirdPartyCreativesUpdateOperations operations);

    CreativeSiteApprovals sitesByCreative(Long creativeId);

    class CreativeSiteApprovals {
        private List<CreativeSiteApprovalTO> approvalsList;
        private boolean hasRejected;

        public List<CreativeSiteApprovalTO> asList() {
            return approvalsList;
        }

        public void setList(List<CreativeSiteApprovalTO> approvalsList) {
            this.approvalsList = approvalsList;
        }

        public boolean hasRejected() {
            return hasRejected;
        }

        public void hasRejected(boolean hasRejected) {
            this.hasRejected = hasRejected;
        }
    }
}
