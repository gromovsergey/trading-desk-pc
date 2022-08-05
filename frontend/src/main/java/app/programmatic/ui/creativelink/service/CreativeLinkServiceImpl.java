package app.programmatic.ui.creativelink.service;

import static app.programmatic.ui.common.config.ApplicationConstants.MAX_RESULTS_SIZE;
import static app.programmatic.ui.common.config.ApplicationConstants.MAX_RESULTS_SIZE_SELECTOR;

import com.foros.rs.client.model.advertising.campaign.CreativeLink;
import com.foros.rs.client.model.advertising.campaign.CreativeLinkSelector;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.foros.service.ForosCreativeLinkService;
import app.programmatic.ui.common.tool.foros.ForosHelper;
import app.programmatic.ui.creative.dao.model.CreativeDisplayStatus;
import app.programmatic.ui.creativelink.dao.model.CreativeLinkDisplayStatus;
import app.programmatic.ui.creativelink.dao.model.CreativeLinkOperation;
import app.programmatic.ui.creativelink.dao.model.CreativeLinkStat;
import app.programmatic.ui.localization.service.LocalizationService;
import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.service.RestrictionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class CreativeLinkServiceImpl implements CreativeLinkService {

    @Autowired
    private ForosCreativeLinkService forosService;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private LocalizationService localizationService;


    @Override
    public List<CreativeLink> findByCcgId(Long ccgId) {
        CreativeLinkSelector selector = new CreativeLinkSelector();
        selector.setGroupIds(Collections.singletonList(ccgId));
        selector.setPaging(MAX_RESULTS_SIZE_SELECTOR);

        return forosService.getCreativeLinkService().get(selector).getEntities();
    }

    @Override
    public void createOrUpdate(List<CreativeLink> links) {
        List<Operation<CreativeLink>> operationsList = new ArrayList<>(links.size());
        for (CreativeLink link : links) {
            Operation<CreativeLink> operation = new Operation<>();
            operation.setType(link.getId() == null ? OperationType.CREATE : OperationType.UPDATE);
            operation.setEntity(link);

            operationsList.add(operation);
        }

        Operations<CreativeLink> operations = new Operations<>();
        operations.setOperations(operationsList);
        forosService.getCreativeLinkService().perform(operations);
    }

    @Override
    public List<CreativeLinkStat> getStatsByCcgId(Long ccgId) {
        restrictionService.throwIfNotPermitted(Restriction.VIEW_CCG, ccgId);

        List<CreativeLinkStat> resultList = jdbcOperations.query(
                "select * from statqueries.cc_stats_for_ccg(?::int, ?::date, ?::date, ?::bool, ?::bool, ?::int, ?::int)",
                new Object[]{
                        ccgId,
                        null,
                        null,
                        Boolean.FALSE,
                        Boolean.TRUE,
                        0,
                        MAX_RESULTS_SIZE
                },
                (ResultSet rs, int rowNum) -> {
                    CreativeLinkStat result = new CreativeLinkStat();

                    result.setCreativeId(rs.getLong("creative_id"));
                    result.setCreativeName(rs.getString("creative_name"));
                    result.setCreativeDisplayStatus(CreativeDisplayStatus.valueOf(rs.getInt("creative_display_status_id")).getMajorStatus());
                    result.setDisplayStatus(CreativeLinkDisplayStatus.valueOf(rs.getInt("cc_display_status_id")).getMajorStatus());
                    result.setSizeId(rs.getLong("size_id"));
                    result.setSizeName(rs.getString("size_name"));
                    result.setTemplateId(rs.getLong("template_id"));
                    result.setTemplateName(rs.getString("template_name"));
                    result.setImps(rs.getLong("imps"));
                    result.setClicks(rs.getLong("clicks"));
                    result.setCtr(rs.getBigDecimal("ctr").setScale(2, BigDecimal.ROUND_HALF_UP));
                    result.setUniqueUsers(rs.getLong("unique_users"));

                    return result;
                });

        return localizationService.processCreativeLinksStats(resultList);
    }

    @Override
    public MajorDisplayStatus changeStatusByCreativeId(Long ccgId, Long creativeId, CreativeLinkOperation operation) {
        Long ccId = jdbcOperations.queryForObject(
                "select cc_id from campaigncreative where ccg_id = ? and creative_id = ? and status != 'D'",
                new Object[] { ccgId, creativeId },
                Long.class);
        CreativeLink creativeLink = new CreativeLink();
        creativeLink.setId(ccId);
        ForosHelper.changeEntityStatus(creativeLink, ForosHelper.isChangeStatusOperation(operation));
        createOrUpdate(Collections.singletonList(creativeLink));

        return findCreativeLinkStatus(ccId);
    }

    private MajorDisplayStatus findCreativeLinkStatus(Long creativeLinkId) {
        Integer displayStatusId = jdbcOperations.queryForObject(
                "select display_status_id from campaigncreative where cc_id = ?",
                new Object[] { creativeLinkId },
                Integer.class);
        return CreativeLinkDisplayStatus.valueOf(displayStatusId).getMajorStatus();
    }
}
