package com.foros.action.reporting;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.reporting.tools.CancelQueryService;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

public class CancellablePageReportingAction extends BaseActionSupport {

    @Autowired
    private CancelQueryService cancelQueryService;

    private String cancellationToken;

    @ReadOnly
    public String showCancellableView() {
        cancellationToken = UUID.randomUUID().toString();
        return SUCCESS;
    }

    public String cancel() {
        cancelQueryService.cancel(cancellationToken);
        return null;
    }

    public String cancelAsync() {
        cancelQueryService.cancelAsync(cancellationToken);
        return null;
    }

    public String getCancellationToken() {
        return cancellationToken;
    }

    public void setCancellationToken(String cancellationToken) {
        this.cancellationToken = cancellationToken;
    }
}
