package com.foros.session.channel.triggerQA;

import com.foros.model.ApproveStatus;
import com.foros.model.DisplayStatus;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.channel.ChannelTO;
import com.foros.session.channel.TriggerService;
import com.foros.session.channel.exceptions.UpdateException;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.EntityUtils;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import com.foros.util.jpa.DetachedList;
import com.foros.util.mapper.Converter;
import com.foros.util.posgress.PGArray;
import com.foros.util.posgress.PGRow;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityNotFoundException;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "TriggerQAService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class })
public class TriggerQAServiceBean implements TriggerQAService {

    @EJB
    protected HibernateWorkExecutorService executor;

    @EJB
    private TriggerService triggerService;

    @EJB
    private LoggingJdbcTemplate loggingJdbcTemplate;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CampaignService campaignService;

    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    private SearchChannelService searchChannelService;

    @Override
    @Restrict(restriction = "TriggerQA.view")
    public DetachedList<TriggerQATO> search(final TriggerQASearchParameters params) {
        final List<TriggerQATO> triggerTOList = loggingJdbcTemplate.query("select * from trigger.Trigger_QA_Search("
                + " ?::character, ?::character(2), ?::character, ?::varchar[],"
                + " ?::varchar[], ?::character[], ?::integer, ?::character,"
                + " ?::integer[], ?::varchar[], ?::integer[], ?::bigint, ?::integer,"
                + " ?::integer, ?::integer, ?::bigint, ?::varchar,"
                + " ?::varchar, ?::integer, ?::integer, ?::boolean)", new Object[] {
                params.getTriggerChannelType() == null ? null : params.getTriggerChannelType().getLetter(),
                params.getCountry(),
                params.getType() == null ? null : params.getType().getLetter(),
                loggingJdbcTemplate.createArray("varchar", params.getKeywordSearchPhrase()),
                loggingJdbcTemplate.createArray("varchar", params.getURLSearchPhrase()),
                loggingJdbcTemplate.createArray("varchar", getQaStatuses(params)),
                getAccountId(params),
                getFilterBy(params),
                loggingJdbcTemplate.createArray("integer", getRolesIds(params)),
                loggingJdbcTemplate.createArray("varchar", getVisibilities(params)),
                loggingJdbcTemplate.createArray("integer", getDisplayStatuses(params)),
                params.getChannelId(),
                params.getAdvertiserId(),
                params.getCampaignId(),
                params.getCcgId(),
                params.getDiscoverChannelListId(),
                params.getSortOrder().getOrderColumn(),
                params.getSortOrder().getOrderDirection(),
                params.getMaxResults() + 1,
                params.getFirstRow(),
                currentUserService.getUser().isDeletedObjectsVisible()
        }, new RowMapper<TriggerQATO>() {

            @Override
            public TriggerQATO mapRow(ResultSet rs, int rowNum) throws SQLException {
                TriggerQATO to = new TriggerQATO();
                to.setId(rs.getLong("trigger_id"));
                to.setOriginalTrigger(rs.getString("trigger"));
                to.setQaStatus(ApproveStatus.valueOf(rs.getString("qa_status").charAt(0)));
                to.setTriggerType(TriggerQAType.valueByLetter(rs.getString("trigger_type").charAt(0)));
                to.setChannels(convertArrayToList(rs.getArray("channels")));
                return to;
            }

            private List<ChannelTO> convertArrayToList(Array array) throws SQLException {
                return PGArray.read(array, new Converter<PGRow, ChannelTO>() {
                    @Override
                    public ChannelTO item(PGRow row) {
                        Long id = row.getLong(0);
                        String name = row.getString(1);
                        Character status = row.getCharacter(2);
                        Character qaStatus = row.getCharacter(3);
                        Integer displayStatus = row.getInteger(4);
                        String channelType = row.getString(5);
                        return new ChannelTO(id, name, status, qaStatus, displayStatus.longValue(), channelType);
                    }
                });
            }
        });
        return new DetachedList<TriggerQATO>(triggerTOList, triggerTOList.size());
    }

    @Override
    @Restrict(restriction = "TriggerQA.view")
    @Validate(validation = "TriggerQA.get", parameters = "#selector")
    public Result<TriggerQATO> get(final TriggerQASelector selector) {
        DetachedList<TriggerQATO> resultList = search(new TriggerQASearchParameters(
            selector.getPaging().getFirst(),
            selector.getPaging().getCount(),
            null,
            determineFilter(selector.getCampaignId(), selector.getCcgId(), selector.getChannelId()),
            "",
            selector.getTriggerStatus() == null ? null : selector.getTriggerStatus().getLetter(),
            null,
            null,
            null,
            selector.getChannelId(),
            determineCountryCode(selector.getCampaignId(), selector.getCcgId(), selector.getChannelId()),
            null,
            selector.getCampaignId(),
            selector.getCcgId(),
            null,
            TriggerQASortType.ATOZ
                ));

        return new Result<TriggerQATO>(resultList, selector.getPaging());
    }

    private List<String> getQaStatuses(TriggerQASearchParameters params) {
        List<String> statuses = new ArrayList<String>();
        Character ch = params.getSearchStatuses();
        if (ch == null) {
            statuses.add("A");
            statuses.add("D");
            statuses.add("H");
        } else if (ch == 'E') {
            statuses.add("A");
            statuses.add("D");
        } else {
            statuses.add(ch.toString());
        }
        return statuses;
    }

    private List<Long> getRolesIds(TriggerQASearchParameters params) {
        List<Long> returnList = new ArrayList<Long>();
        if (params.getAccountRoles() != null) {
            for (AccountRole role : params.getAccountRoles()) {
                returnList.add((long) role.getId());
            }
        }
        return returnList;
    }

    private List<Long> getDisplayStatuses(TriggerQASearchParameters params) {
        List<Long> returnList = new ArrayList<Long>();
        if (params.getDisplayStatuses() != null) {
            for (DisplayStatus status : params.getDisplayStatuses()) {
                returnList.add(status.getId());
            }
        }
        return returnList;
    }

    private List<String> getVisibilities(TriggerQASearchParameters params) {
        List<String> returnList = new ArrayList<String>();
        if (params.getVisibility() != null) {
            returnList.add(params.getVisibility().getName());
        }
        return returnList;
    }

    private String getFilterBy(TriggerQASearchParameters params) {
        switch (params.getFilter()) {
        case ALL:
            return "A";
        case CCG:
            return "G";
        case CHANNEL:
            return "C";
        default:
            throw new IllegalArgumentException("Unknown TriggerQASearchFilter");
        }
    }

    private Long getAccountId(TriggerQASearchParameters params) {
        Long res = null;
        if (params.getAccountId() != null && params.getAccountId() > 0) {
            res = params.getAccountId();
        }
        return res;
    }

    @Override
    @Restrict(restriction = "TriggerQA.update")
    public void update(List<TriggerQATO> triggers) throws UpdateException {
        triggerService.updateTriggers(triggers);
    }

    @Override
    @Restrict(restriction = "TriggerQA.update")
    @Validate(validation = "TriggerQA.perform", parameters = "#triggerOperations")
    public OperationsResult perform(Operations<TriggerQATO> triggerOperations) throws UpdateException {
        List<TriggerQATO> triggersToUpdate = new ArrayList<>(triggerOperations.getOperations().size());
        for (Operation<TriggerQATO> triggerOperation : triggerOperations.getOperations()) {
            triggersToUpdate.add(triggerOperation.getEntity());
        }
        update(triggersToUpdate);
        return new OperationsResult(new ArrayList<>(EntityUtils.getEntityIds(triggersToUpdate)));
    }

    private TriggerQASearchFilter determineFilter(Long campaignId, Long ccgId, Long channelId) {
        if (campaignId != null) {
            return TriggerQASearchFilter.CCG;
        }
        if (ccgId != null) {
            return TriggerQASearchFilter.CCG;
        }
        if (channelId != null) {
            return TriggerQASearchFilter.CHANNEL;
        }
        return null;
    }

    private String determineCountryCode(Long campaignId, Long ccgId, Long channelId) {
        Long id = null;
        try {
            if (campaignId != null) {
                id = campaignId;
                return campaignService.find(campaignId).getAccount().getCountry().getCountryCode();
            } else if (ccgId != null) {
                id = ccgId;
                return campaignCreativeGroupService.find(ccgId).getCountry().getCountryCode();
            } else {
                id = channelId;
                return searchChannelService.find(channelId).getCountry().getCountryCode();
            }
        } catch (EntityNotFoundException e) {
            throw ConstraintViolationException
                .newBuilder("errors.entity.notFound")
                .withParameters(id)
                .build();
        }
    }
}
