package com.foros.action.admin.permissions;

import com.foros.session.NamedTO;
import com.foros.session.ServiceLocator;
import com.foros.session.birt.BirtReportService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BirtReportsIdsProvider implements ParametersProvider {

    @Override
    public List<Parameter> parameters() {
        BirtReportService service = ServiceLocator.getInstance().lookup(BirtReportService.class);

        ArrayList<Parameter> result = new ArrayList<Parameter>();
        for (NamedTO report : service.getAllReportNames()) {
            result.add(new Parameter(report.getId().toString(), report.getName()));
        }

        Collections.sort(result);

        return result;
    }

}
