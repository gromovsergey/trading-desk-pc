package com.foros.service.mock;

import com.phorm.oix.service.AdServer.ChannelSearchSvcs.ChannelSearch;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.ChannelSearchPackage.ImplementationException;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.ChannelSearchResult;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.MatchInfo;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.WMatchInfo;
import com.foros.session.LoggingJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import org.springframework.jdbc.core.RowMapper;

/**
 * Mock implementation of ChannelSearch interface.
 */
@Stateless(name = "ChannelSearch")
@Remote(ChannelSearch.class)
public class ChannelSearchBean extends AbstractCorbaMock implements ChannelSearch {

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    public ChannelSearchResult[] search(String phrase) throws ImplementationException {
        return wsearch(phrase);
    }

    @Override
    public ChannelSearchResult[] wsearch(String phrase) throws ImplementationException {
        if ("exception".equals(phrase)) {
            throw new ImplementationException();
        }

        List<ChannelSearchResult> res = jdbcTemplate.query(
                "select trigger.get_channel_ids_by_trigger_pattern(?)",
                new Object[]{phrase},
                new RowMapper<ChannelSearchResult>() {
                    @Override
                    public ChannelSearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Long id = rs.getLong(1);
                        int reuse = 999;
                        return new ChannelSearchResult(id.intValue(), reuse);
                    }
                });
        return res.toArray(new ChannelSearchResult[res.size()]);
    }

    @Override
    public MatchInfo match(String url, String phrase, int channelsCount) throws ImplementationException {
        return null;
    }

    @Override
    public WMatchInfo wmatch(String url, String phrase, int channelsCount) throws ImplementationException {
        return null;
    }
}
