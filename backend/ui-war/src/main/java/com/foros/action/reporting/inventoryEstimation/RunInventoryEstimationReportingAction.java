package com.foros.action.reporting.inventoryEstimation;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.inventoryEstimation.InventoryEstimationReportParameters;
import com.foros.session.reporting.inventoryEstimation.InventoryEstimationReportService;

import javax.ejb.EJB;

public class RunInventoryEstimationReportingAction extends RunReportingActionSupport<InventoryEstimationReportParameters> {

    @EJB
    private InventoryEstimationReportService reportsService;

    @Override
    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportsService, "inventoryEstimation");
    }
}

