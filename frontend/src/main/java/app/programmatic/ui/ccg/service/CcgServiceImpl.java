package app.programmatic.ui.ccg.service;

import static app.programmatic.ui.common.config.ApplicationConstants.MAX_RESULTS_SIZE_SELECTOR;

import com.foros.rs.client.model.advertising.campaign.CampaignCreativeGroup;
import com.foros.rs.client.model.advertising.campaign.CampaignCreativeGroupSelector;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Result;
import app.programmatic.ui.ccg.dao.model.CcgDisplayStatus;
import app.programmatic.ui.ccg.dao.model.CcgLineItemPart;
import app.programmatic.ui.common.datasource.tools.RsTools;
import app.programmatic.ui.common.error.ForbiddenException;
import app.programmatic.ui.common.foros.service.ForosCcgService;
import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.model.RestrictionCommandBuilder;
import app.programmatic.ui.restriction.service.RestrictionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Service
public class CcgServiceImpl implements CcgService {

    @Autowired
    private ForosCcgService forosService;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private RestrictionService restrictionService;


    @Override
    public CampaignCreativeGroup find(Long id) {
        List<CampaignCreativeGroup> result = findAll(Collections.singletonList(id));
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<CampaignCreativeGroup> findAll(List<Long> ids) {
        CampaignCreativeGroupSelector selector = new CampaignCreativeGroupSelector();
        selector.setGroupIds(ids);

        return forosService.getCcgService().get(selector).getEntities();
    }

    @Override
    public CcgLineItemPart findLineItemPart(Long ccgId) {
        return findLineItemPart(Collections.singletonList(ccgId)).get(0);
    }

    @Override
    public List<CcgLineItemPart> findLineItemPart(Collection<Long> ccgIds) {
        Array array = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", ccgIds.toArray()));
        return jdbcOperations.query("select ccg.ccg_id, ccg.name, ccg.budget, ccg.version, ccg.channel_id, ccg.display_status_id, c.account_id " +
                                    "  from campaigncreativegroup ccg " +
                                    "  inner join campaign c using(campaign_id)" +
                                    "  where ccg_id = any(?)",
                new Object[] { array },
                (ResultSet rs, int rowNum) -> {
                    CcgLineItemPart result = new CcgLineItemPart();

                    result.setCcgId(RsTools.getNullableLong(rs, "ccg_id"));
                    result.setName(rs.getString("name"));
                    result.setAccountId(rs.getLong("account_id"));
                    result.setBudget(rs.getBigDecimal("budget"));
                    result.setVersion(rs.getTimestamp("version"));
                    result.setCcgChannelId(RsTools.getNullableLong(rs, "channel_id"));
                    result.setDisplayStatus(CcgDisplayStatus.valueOf((rs.getInt("display_status_id"))));

                    return result;
                }
        );
    }

    @Override
    public List<CampaignCreativeGroup> findByCampaignId(Long campaignId) {
        CampaignCreativeGroupSelector selector = new CampaignCreativeGroupSelector();
        selector.setCampaignIds(Collections.singletonList(campaignId));
        selector.setPaging(MAX_RESULTS_SIZE_SELECTOR);

        Result<CampaignCreativeGroup> result = forosService.getCcgService().get(selector);
        if (result.getEntities().isEmpty()) {
            return null;
        }

        return result.getEntities();
    }

    @Override
    public Long createOrUpdate(CampaignCreativeGroup ccg) {
        return createOrUpdate(Collections.singletonList(ccg)).get(0);
    }

    @Override
    public List<Long> createOrUpdate(List<CampaignCreativeGroup> ccgs) {
        if (ccgs.isEmpty()) {
            return Collections.emptyList();
        }

        checkCreateOrUpdateRestrictions(ccgs);

        List<Operation<CampaignCreativeGroup>> operationsList = new ArrayList<>(ccgs.size());

        for (CampaignCreativeGroup ccg : ccgs) {
            Operation<CampaignCreativeGroup> operation = new Operation<>();
            operation.setEntity(ccg);
            operation.setType(ccg.getId() == null ? OperationType.CREATE : OperationType.UPDATE);
            operationsList.add(operation);
        }

        Operations<CampaignCreativeGroup> operations = new Operations<>();
        operations.setOperations(operationsList);
        OperationsResult result = forosService.getAdminCcgService().perform(operations);
        return result.getIds();
    }

    private void checkCreateOrUpdateRestrictions(List<CampaignCreativeGroup> ccgs) {
        RestrictionCommandBuilder builder = new RestrictionCommandBuilder();
        for (CampaignCreativeGroup ccg : ccgs) {
            if (ccg.getId() == null) {
                if (ccg.getCampaign().getId() == null) {
                    throw new ForbiddenException();
                }
                builder.add(Restriction.CREATE_CCG, ccg.getCampaign().getId());
                continue;
            }
            builder.add(Restriction.UPDATE_CCG, ccg.getId());
        }
        restrictionService.throwIfNotPermitted(builder);
    }
}
