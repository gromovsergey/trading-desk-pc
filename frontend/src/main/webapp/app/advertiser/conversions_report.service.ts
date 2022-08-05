import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Router} from '@angular/router';

import {CommonService} from '../common/common.service';
import {ConversionsReportParameters, ReportMeta, ReportService} from "../report/report";

@Injectable()
export class ConversionsReportService extends CommonService implements ReportService {

    constructor(public router: Router, public http: Http) {
        super(router, http);
    }

    public getReportMeta(parameters: ConversionsReportParameters): Promise<ReportMeta> {
        return this.httpPut(this.api.report.meta.conversions, parameters);
    }

    public generateReport(parameters: ConversionsReportParameters): Promise<any> {
        return this.httpPut(this.api.report.run.conversions, parameters, {format: 'JSON'});
    }

    public downloadReport(parameters: ConversionsReportParameters, format: string): Promise<any> {
        return this.httpDownload('PUT', this.api.report.run.conversions, parameters, {format: format});
    }
}