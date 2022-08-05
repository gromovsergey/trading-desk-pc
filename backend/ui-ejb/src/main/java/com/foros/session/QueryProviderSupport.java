package com.foros.session;

import com.foros.cache.generic.Cache;
import com.foros.cache.generic.CacheProviderService;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.reporting.rowsource.jdbc.ResultSetValueReaderRegistry;
import com.foros.reporting.tools.CancelQueryService;
import com.foros.reporting.tools.query.Query;
import com.foros.reporting.tools.query.QueryProvider;
import com.foros.reporting.tools.query.ResultSetExecutor;
import com.foros.reporting.tools.query.SpringQueryImpl;
import com.foros.reporting.tools.query.SpringTemplateQuery;
import com.foros.reporting.tools.query.cache.CachedQuery;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;

public abstract class QueryProviderSupport implements QueryProvider {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private CacheProviderService cacheProviderService;

    @EJB
    private ConfigService config;

    private Cache cache;

    protected JdbcTemplate jdbcTemplate;

    @EJB
    protected CancelQueryService cancelQueryService;

    protected abstract void doExecute(ResultSetExecutor executor, List<SqlParameterValue> parameters, ResultSetExtractor work);

    protected abstract ResultSetValueReaderRegistry getResultSetValueReaderRegistry();

    protected void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    private void init() {
        cache = cacheProviderService.getCache();
    }

    @Override
    public Query query(ResultSetExecutor executor) {
        return new SpringQueryImpl(this, executor, getResultSetValueReaderRegistry());
    }

    @Override
    public Query queryCallable(String procedure) {
        return new SpringTemplateQuery(this, procedure, createCallableTemplate(procedure), getResultSetValueReaderRegistry());
    }

    @Override
    public Query queryFunction(String function) {
        return new SpringTemplateQuery(this, function, createFunctionTemplate(function), getResultSetValueReaderRegistry());
    }

    @Override
    public CachedQuery cached(Query query, String regionName) {
        int exportMaxRows = config.get(ConfigParameters.EXPORT_REPORT_MAX_ROWS);
        return new CachedQuery(em, cache.getRegion(regionName), query, exportMaxRows + 1);
    }

    @Override
    public void execute(ResultSetExecutor executor, List<SqlParameterValue> parameters, ResultSetExtractor resultSetExtractor) {
        doExecute(executor, parameters, resultSetExtractor);
    }
}
