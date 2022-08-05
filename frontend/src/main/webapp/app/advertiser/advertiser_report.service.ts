import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Router} from '@angular/router';

import {CommonService} from '../common/common.service';
import {AdvertiserReportParameters, ReportMeta, ReportService} from "../report/report";

@Injectable()
export class AdvertiserReportService extends CommonService implements ReportService {

    constructor(public router: Router, public http: Http) {
        super(router, http);
    }

    public getReportMeta(parameters: AdvertiserReportParameters): Promise<ReportMeta> {
        return this.httpPut(this.api.report.meta.advertiser, parameters);
    }

    public generateReport(parameters: AdvertiserReportParameters): Promise<any> {
        return this.httpPut(this.api.report.run.advertiser, parameters, {format: 'JSON'});
    }

    public downloadReport(parameters: AdvertiserReportParameters, format: string): Promise<any> {
        return this.httpDownload('PUT', this.api.report.run.advertiser, parameters, {format: format});
    }
}