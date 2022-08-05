package com.foros.reporting.tools.olap.query.saiku;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.phorm.oix.saiku.SaikuApiClient;
import com.phorm.oix.saiku.SaikuFactory;
import com.phorm.oix.saiku.SaikuStatement;
import com.phorm.oix.saiku.meta.SaikuCube;
import com.phorm.oix.saiku.meta.SaikuSchema;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

@Startup
@Singleton(name = "SaikuStatementProvider")
@TransactionManagement(TransactionManagementType.BEAN)
public class SaikuStatementProviderImpl implements SaikuStatementProvider {

    @EJB
    private ConfigService configService;

    private SaikuFactory factory;

    private SaikuSchema schema;
    private String schemaName;
    private SaikuApiClient saikuApiClient;

    @PostConstruct
    public void initialize() {
        String url = configService.get(ConfigParameters.SAIKU_API_URL);
        String user = configService.get(ConfigParameters.SAIKU_API_USER);
        String password = configService.get(ConfigParameters.SAIKU_API_PASSWORD);

        this.schemaName = configService.get(ConfigParameters.SAIKU_DEFAULT_SCHEMA);

        this.saikuApiClient = new SaikuApiClient(url, user, password);
        this.factory = new SaikuFactory(saikuApiClient);
    }

    @Override
    public synchronized SaikuStatement createStatement(String cubeName) {
        if (schema == null) {
            schema = factory.getSchema(schemaName, false);
        }

        SaikuCube cube = schema.getSaikuCubes().get(cubeName);

        if (cube == null) {
            schema = null;
            throw new IllegalStateException("Cube with name " + cubeName + " not found in schema " + schemaName + ". Try to reload schema.");
        }

        String uuid = saikuApiClient.queryCreateQuery(
                cube.getSchema().getCatalog().getDatabase().getName(),
                cube.getSchema().getCatalog().getName(),
                cube.getSchema().getName(),
                cube.getName()
        );

        return new SaikuStatement(cube, uuid, saikuApiClient);
    }

    @Override
    public synchronized void refresh() {
        schema = factory.getSchema(schemaName, true);
    }

}
