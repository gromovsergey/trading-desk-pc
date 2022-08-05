package com.foros.session.creative;

import com.foros.model.DisplayStatus;
import com.foros.session.EntityTO;

import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

@Local
public interface CreativeService {
    void bulkUpdateStatus(List<Long> ids, String changeType, String declineReason);

    int findPendingFOROSCreativesCount();

    List<CreativeTO> findCreatives(Long accountId, List<DisplayStatus> displayStatuses, Long campaignId, Long sizeId, boolean allowTestAccounts, boolean allowDeletedOwner, int from, int count, CreativeSortType orderBy);

    int findCreativesCount(Long accountId, List<DisplayStatus> displayStatuses, Long campaign, Long size, boolean allowTestAccounts, boolean allowDeletedOwner);

    List<CreativeTO> findPendingFOROSCreatives(int firstRow, int maxResults);

    boolean isBatchActionPossible(Collection<Long> ids, String action);

    Collection<EntityTO> findCreativesForReport(Long accountId, Long campaignId, Long groupId, String name, int maxResults);

    List<EntityTO> getIndexByIds(Collection<Long> creativeIds);

    void resetRejectedCreativeExclusions(Long creativeId);

    void resetApprovedCreativeExclusions(Long creativeId);
}
