package app.programmatic.ui.campaign.service;

import com.foros.rs.client.model.advertising.campaign.Campaign;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import app.programmatic.ui.campaign.dao.model.CampaignDisplayStatus;
import app.programmatic.ui.campaign.dao.model.CampaignFlightPart;
import app.programmatic.ui.campaign.tool.CampaignBuilder;
import app.programmatic.ui.common.foros.service.ForosCampaignService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private ForosCampaignService forosService;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Override
    public Campaign find(Long id) {
        return jdbcOperations.queryForObject("select c.name, c.account_id, c.freq_cap_id, c.status, c.sold_to_user_id, " +
                        "c.bill_to_user_id, c.commission, c.max_pub_share, c.date_start, c.date_end, c.marketplace, " +
                        "c.campaign_type, c.daily_budget, c.delivery_pacing, " +
                        "fc.period, fc.window_length, fc.window_count, fc.life_count" +
                        "  from campaign c " +
                        "  left join freqcap fc using(freq_cap_id) " +
                        "  where c.campaign_id = ?::int",
                new Object[] { id },
                (ResultSet rs, int index) -> CampaignBuilder.build(rs, id));
    }

    @Override
    public Long findId(Long accountId, String name) {
        try {
            return jdbcOperations.queryForObject("select campaign_id from campaign where account_id = ?::int and name = ?",
                    new Object[]{accountId, name},
                    Long.class);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public Long createOrUpdate(Campaign campaign) {
        Operation<Campaign> operation = new Operation<>();
        operation.setEntity(campaign);
        operation.setType(campaign.getId() == null ? OperationType.CREATE : OperationType.UPDATE);

        Operations<Campaign> operations = new Operations<>();
        operations.setOperations(Collections.singletonList(operation));

        OperationsResult result = forosService.getCampaignService().perform(operations);
        return result.getIds().get(0);
    }

    @Override
    public CampaignFlightPart findFlightPart(Long campaignId) {
        return findFlightPart(Collections.singletonList(campaignId)).get(0);
    }

    @Override
    public List<CampaignFlightPart> findFlightPart(Collection<Long> campaignIds) {
        Array array = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", campaignIds.toArray()));
        return jdbcOperations.query("select display_status_id from campaign where campaign_id = any(?)",
                new Object[] { array },
                (ResultSet rs, int rowNum) -> new CampaignFlightPart(
                        CampaignDisplayStatus.valueOf((rs.getInt("display_status_id"))))
        );
    }
}
