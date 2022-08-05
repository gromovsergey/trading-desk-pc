package app.programmatic.ui.quick_search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import app.programmatic.ui.account.dao.model.AccountDisplayStatus;
import app.programmatic.ui.campaign.dao.model.CampaignDisplayStatus;
import app.programmatic.ui.common.datasource.DataSourceService;
import app.programmatic.ui.quick_search.model.QuickSearchResult;
import app.programmatic.ui.quick_search.model.QuickSearchResultItem;
import app.programmatic.ui.quick_search.model.Type;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;

@Service
public class QuickSearchServiceImpl implements QuickSearchService{
    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private DataSourceService dsService;

    @Override
    public QuickSearchResult search(String text) {
        return dsService.executeWithAuth(jdbcOperations, () -> searchWithDatastore(text));
    }

    private QuickSearchResult searchWithDatastore(String text) {
        Array objectTypes = jdbcOperations.execute((Connection con) -> con.createArrayOf("varchar", new String[] {"agency", "advertiser", "flight"}));
        QuickSearchResult quickSearchResult = new QuickSearchResult();
        jdbcOperations.query(
                "select * from entityqueries.quick_search(?::varchar[], ?::varchar)",
                new Object[]{objectTypes, text},
                (ResultSet rs) -> {
                    QuickSearchResultItem result = new QuickSearchResultItem();
                    result.setId(rs.getLong("id"));
                    result.setName(rs.getString("name"));

                    Type type = Type.valueOf(rs.getString("type_name"));
                    result.setType(type);

                    Long displayStatusId = rs.getLong("display_status_id");
                    switch (type) {
                        case Advertiser: case Agency:
                            result.setDisplayStatus(AccountDisplayStatus.valueOf(displayStatusId.intValue()).getMajorStatus());
                            break;
                        case Flight:
                            result.setDisplayStatus(CampaignDisplayStatus.valueOf(displayStatusId.intValue()).getMajorStatus());
                            break;
                    }
                    quickSearchResult.add(result);
                }
        );
        return quickSearchResult;
    }

}
