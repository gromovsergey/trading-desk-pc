package com.foros;

import com.foros.config.MockConfigService;
import com.foros.model.ApprovableEntity;
import com.foros.model.DisplayStatusEntity;
import com.foros.model.DisplayStatusEntityBase;
import com.foros.model.Identifiable;
import com.foros.model.StatusEntityBase;
import com.foros.model.security.User;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.MockPrincipal;
import com.foros.security.principal.SecurityPrincipal;
import com.foros.service.mock.AdvertisingFinanceServiceMock;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.ServiceLocator;
import com.foros.session.ServiceLocatorMock;
import com.foros.session.fileman.FileUtils;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.DisplayStatusService;
import com.foros.test.CurrentUserRule;
import com.foros.util.FlagsUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        SpringUnitilsAdaptorTestExecutionListener.class
})
@ContextConfiguration(
        locations = {
                Contexts.DEFAULT_ROOT_PROPERTIES_LOCATION,
                Contexts.DEFAULT_ROOT_CONTEXT_LOCATION,
                Contexts.DEFAULT_TEST_CONTEXT_LOCATION
        })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public abstract class AbstractServiceBeanIntegrationTest extends Assert implements ApplicationContextAware {
    public static Map<String, String> DEFAULT_SYSTEM_PROPS = new HashMap<>();

    @Rule
    public CurrentUserRule currentUserRule = new CurrentUserRule();

    @Autowired
    private MockConfigService configService;

    static {
        DEFAULT_SYSTEM_PROPS.put("dataRoot", FileUtils.trimPathName(System.getProperty("user.dir"), false));
        DEFAULT_SYSTEM_PROPS.put("creativesFolder", "");
        DEFAULT_SYSTEM_PROPS.put("dataUrl", "");
        DEFAULT_SYSTEM_PROPS.put("insecureDataUrl", "");
        DEFAULT_SYSTEM_PROPS.put("previewFolder", "");
        DEFAULT_SYSTEM_PROPS.put("tagsFolder", "");
        DEFAULT_SYSTEM_PROPS.put("reportsFolder", "");
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

    public static final SecurityPrincipal DEFAULT_ADMIN_PRINCIPAL = new MockPrincipal(
            "test@ocslab.com",
            1L,
            1L,
            2L,
            0L
    );

    public static final SecurityPrincipal ADVERTISER_PRINCIPAL = new MockPrincipal(
            "advertiser@ocslab.com",
            2L,
            2L,
            4L,
            4L
    );

    public static final SecurityPrincipal INTERNAL_USER_ACCOUNT_PRINCIPAL = new MockPrincipal(
            "INTERNAL_USER_ACCOUNT@ocslab.com",
            1L,
            1L,
            2L,
            0L
    );

    @Autowired
    private AdvertisingFinanceServiceMock advertisingFinanceServiceMock;

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager entityManager;

    @Autowired
    protected AuditService mockAuditService;

    @Autowired
    private UserService userService;

    @Autowired
    protected LoggingJdbcTemplate jdbcTemplate;

    @Autowired
    protected PermissionService permissionService;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.applicationContext = context;
    }

    @BeforeClass
    public static void beforeClass() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

    @Before
    public void setUp() throws Exception {
        for (Map.Entry<String, String> entry : DEFAULT_SYSTEM_PROPS.entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }

        currentUserRule.setPrincipal(DEFAULT_ADMIN_PRINCIPAL);
        ServiceLocatorMock.getInstance().setBeanFactory(applicationContext);

        configService.clear();
    }

    @Before
    public final void initPermissionService() {
        EasyMock.reset(permissionService);

        EasyMock.expect(permissionService.isGranted(EasyMock.anyString(), EasyMock.anyString())).andReturn(true).anyTimes();

        permissionService.removePolicyCache();
        EasyMock.expectLastCall().anyTimes();

        permissionService.removePolicyCache(EasyMock.anyObject(Long.class));
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(permissionService);
    }

    @Before
    @After
    public final void cleanup() {
        advertisingFinanceServiceMock.reset();
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected Identifiable findById(List<? extends Identifiable> entityList, Long id) {
        if (entityList == null || id == null) {
            return null;
        }

        for (Identifiable other : entityList) {
            if (id.equals(other.getId())) {
                return other;
            }
        }

        return null;
    }

    protected void commitChanges() {
        getEntityManager().flush();
    }

    protected void clearContext() {
        getEntityManager().clear();
    }

    protected void commitChangesAndClearContext() {
        commitChanges();
        clearContext();
    }

    protected void persist(Object entry) {
        getEntityManager().persist(entry);
        getEntityManager().flush();
    }

    protected void verifyBulkUpdateDisplayStatus(Object entity) {
        Long javaDisplayStatus = ((DisplayStatusEntityBase) entity).getDisplayStatusId();
        String msg = message((DisplayStatusEntityBase) entity);
        Assert.assertNotNull(msg, javaDisplayStatus);

        getEntityManager().merge(entity);
        getEntityManager().flush();

        DisplayStatusService displayStatusService = ServiceLocator.getInstance().lookup(DisplayStatusService.class);
        displayStatusService.update((Identifiable) entity);

        getEntityManager().refresh(entity);
        Assert.assertNotNull(msg, ((DisplayStatusEntityBase) entity).getDisplayStatus());
        Assert.assertEquals(msg, javaDisplayStatus, ((DisplayStatusEntityBase) entity).getDisplayStatusId());
    }

    protected void setDeletedObjectsVisible(boolean flag) {
        User user = userService.getMyUser();
        user.setFlags(FlagsUtil.set(user.getFlags(), User.IS_DELETED_OBJECTS_VISIBLE, flag));
        userService.update(user);
        commitChanges();
    }

    private static String message(DisplayStatusEntity entity) {
        String msg = "Entity entity" + entity;
        if (entity instanceof StatusEntityBase) {
            msg += " status: " + ((StatusEntityBase) entity).getStatus();
        }
        if (entity instanceof StatusEntityBase) {
            msg += " status: " + ((StatusEntityBase) entity).getStatus();
        }
        if (entity instanceof ApprovableEntity) {
            msg += " qa status: " + ((ApprovableEntity) entity).getQaStatus();
        }
        return msg;
    }
}
