import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Router} from '@angular/router';

import {CommonService} from '../common/common.service';
import {DomainsReportParameters, ReportMeta, ReportService} from "../report/report";

@Injectable()
export class DomainsReportService extends CommonService implements ReportService {

    constructor(public router: Router, public http: Http) {
        super(router, http);
    }

    public getReportMeta(parameters: DomainsReportParameters): Promise<ReportMeta> {
        return this.httpPut(this.api.report.meta.domains, parameters);
    }

    public generateReport(parameters: DomainsReportParameters): Promise<any> {
        return this.httpPut(this.api.report.run.domains, parameters, {format: 'JSON'});
    }

    public downloadReport(parameters: DomainsReportParameters, format: string): Promise<any> {
        return this.httpDownload('PUT', this.api.report.run.domains, parameters, {format: format});
    }
}