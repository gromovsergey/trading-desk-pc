package app.programmatic.ui.reporting.tools;

import static app.programmatic.ui.common.permission.dao.model.PermissionAction.VIEW_SYSTEM_FINANCE;
import static app.programmatic.ui.common.permission.dao.model.PermissionType.ADVERTISING_ACCOUNT;
import static app.programmatic.ui.reporting.model.ReportColumn.*;

import org.springframework.jdbc.core.JdbcOperations;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.common.permission.service.PermissionService;
import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.model.ReportColumn;
import app.programmatic.ui.reporting.model.ReportParameters;
import app.programmatic.ui.user.dao.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class ColumnAvailabilityHelper {
    private static final String VIDEO_CREATIVE_SIZE_NAME_RU = "Видео (VAST)";
    private static final String VIDEO_CREATIVE_SIZE_NAME_EN = "Video (VAST)";

    private AuthorizationService authorizationService;
    private PermissionService permissionService;
    private AccountService accountService;
    private JdbcOperations jdbcOperations;

    @Autowired
    public ColumnAvailabilityHelper(AuthorizationService authorizationService,
                                    PermissionService permissionService,
                                    AccountService accountService,
                                    JdbcOperations jdbcOperations) {
        this.authorizationService = authorizationService;
        this.permissionService = permissionService;
        this.accountService = accountService;
        this.jdbcOperations = jdbcOperations;
    }

    public EnumSet<ReportColumn> getUnacceptableColumns(ReportParameters parameters) {
        EnumSet<ReportColumn> unacceptableColumns = EnumSet.noneOf(ReportColumn.class);

        if (parameters.getReport() == Report.ADVERTISER || parameters.getReport() == Report.DOMAINS) {
            boolean isGranted = permissionService.isGranted(ADVERTISING_ACCOUNT, VIEW_SYSTEM_FINANCE);
            if (isGranted) {
                User currentUser = authorizationService.getAuthUser();
                switch (currentUser.getUserRole().getAccountRole()) {
                    case INTERNAL:
                        break;
                    case ADVERTISER:
                    case AGENCY:
                        AdvertisingAccount account = accountService.findAdvertising(currentUser.getAccountId());
                        if (!account.isSelfServiceFlag()) {
                            unacceptableColumns.addAll(EnumSet.of(PUB_AMOUNT, MARGINALITY, COST));
                        }
                        break;
                    default:
                        break;
                }
            } else {
                unacceptableColumns.addAll(EnumSet.of(COST, PUB_AMOUNT, MARGINALITY, AGENCY_MARGIN));
            }

            if (!hasVideoCreatives(parameters.getAccountId())) {
                unacceptableColumns.addAll(EnumSet.of(
                        START,
                        FIRST_QUARTILE,
                        MIDPOINT,
                        THIRD_QUARTILE,
                        COMPLETE,
                        COMPLETION_RATE,
                        SKIP,
                        PAUSE,
                        VIEW_RATE,
                        MUTE,
                        UNMUTE,
                        RESUME,
                        FULLSCREEN,
                        ERROR
                ));
            }
        }

        return unacceptableColumns;
    }

    private boolean hasVideoCreatives(Long accountId) {
        return jdbcOperations.queryForObject(
                "select exists(select 1 " +
                        "from creative c " +
                        "  inner join creativesize cs using(size_id) " +
                        "where c.status != 'D' and c.account_id = ? and (cs.name = ? or cs.name = ?) limit 1)",
                new Object[]{
                        accountId,
                        VIDEO_CREATIVE_SIZE_NAME_RU,
                        VIDEO_CREATIVE_SIZE_NAME_EN
                },
                Boolean.class
        );
    }
}
