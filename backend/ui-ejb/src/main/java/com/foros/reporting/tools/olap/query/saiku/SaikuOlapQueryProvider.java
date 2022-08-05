package com.foros.reporting.tools.olap.query.saiku;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.reporting.ReportingException;
import com.foros.reporting.tools.olap.query.OlapQuery;
import com.foros.reporting.tools.olap.query.OlapQueryProvider;
import com.foros.reporting.tools.CancelQueryService;
import com.foros.reporting.tools.olap.query.StatCountingOlapQuery;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;

@Singleton(name = "OlapQueryProvider")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class SaikuOlapQueryProvider implements OlapQueryProvider {

    @EJB
    private SaikuStatementProvider connectionProvider;

    @EJB
    private CancelQueryService cancelQueryService;

    @EJB
    private ConfigService configService;

    @Override
    public OlapQuery query(String cubeName, Object context) {
        try {
            return new StatCountingOlapQuery(new SaikuOlapQuery(cancelQueryService, connectionProvider.createStatement(cubeName), context, calcExpirationTime()));
        } catch (Exception e) {
            throw new ReportingException(e);
        }
    }

    private long calcExpirationTime() {
        long timeout = configService.get(ConfigParameters.SAIKU_QUERY_TIMEOUT);
        return System.currentTimeMillis() + timeout;
    }
}
