import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Router} from '@angular/router';

import {CommonService} from '../common/common.service';
import {IdName, ReferrerReportParameters, ReportMeta, ReportService} from "./report";

@Injectable()
export class ReferrerReportService extends CommonService implements ReportService {

    constructor(public router: Router, public http: Http) {
        super(router, http);
    }

    public getReportMeta(parameters: ReferrerReportParameters): Promise<ReportMeta> {
        return this.httpPut(this.api.report.meta.referrer, parameters);
    }

    public generateReport(parameters: ReferrerReportParameters): Promise<any> {
        return this.httpPut(this.api.report.run.referrer, parameters, {format: 'JSON'});
    }

    public downloadReport(parameters: ReferrerReportParameters, format: string): Promise<any> {
        return this.httpDownload('PUT', this.api.report.run.referrer, parameters, {format: format});
    }

    public getPublisherAccount(accountId: number): Promise<IdName> {
        return this.httpGet(this.api.publisher.get, {
            accountId: +accountId
        });
    }

    public getPublisherAccounts(): Promise<Array<any>> {
        return this.httpGet(this.api.publisher.listForReferrerReport, {});
    }

    public getSites(accountId: number): Promise<Array<any>> {
        return this.httpGet(this.api.site.list, {
            accountId: accountId
        });
    }

    public getTags(siteId: number): Promise<Array<any>> {
        return this.httpGet(this.api.site.tagsList, {
            siteId: siteId
        });
    }
}