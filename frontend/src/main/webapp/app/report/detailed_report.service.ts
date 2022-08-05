import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Router} from '@angular/router';

import {CommonService} from '../common/common.service';
import {DetailedReportParameters, IdName, ReportMeta, ReportService} from "./report";

@Injectable()
export class DetailedReportService extends CommonService implements ReportService {

    constructor(public router: Router, public http: Http) {
        super(router, http);
    }

    public getReportMeta(parameters: DetailedReportParameters): Promise<ReportMeta> {
        return this.httpPut(this.api.report.meta.detailed, parameters);
    }

    public generateReport(parameters: DetailedReportParameters): Promise<any> {
        return this.httpPut(this.api.report.run.detailed, parameters, {format: 'JSON'});
    }

    public downloadReport(parameters: DetailedReportParameters, format: string): Promise<any> {
        return this.httpDownload('PUT', this.api.report.run.detailed, parameters, {format: format});
    }

    public getPublisherAccount(accountId: number): Promise<IdName> {
        return this.httpGet(this.api.publisher.get, {
            accountId: +accountId
        });
    }

    public getPublisherAccounts(): Promise<Array<any>> {
        return this.httpGet(this.api.publisher.list, {});
    }

    public getAdvertiserAccounts(): Promise<Array<any>> {
        return this.httpGet(this.api.audienceResearch.advertisers, {});
    }
}
