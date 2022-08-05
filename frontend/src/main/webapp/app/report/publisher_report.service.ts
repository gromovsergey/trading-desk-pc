import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Router} from '@angular/router';

import {CommonService} from '../common/common.service';
import {DomainsReportParameters, IdName, PublisherReportParameters, ReportMeta, ReportService} from "./report";

@Injectable()
export class PublisherReportService extends CommonService implements ReportService {

    constructor(public router: Router, public http: Http) {
        super(router, http);
    }

    public getReportMeta(parameters: PublisherReportParameters): Promise<ReportMeta> {
        return this.httpPut(this.api.report.meta.publisher, parameters);
    }

    public generateReport(parameters: PublisherReportParameters): Promise<any> {
        return this.httpPut(this.api.report.run.publisher, parameters, {format: 'JSON'});
    }

    public downloadReport(parameters: PublisherReportParameters, format: string): Promise<any> {
        return this.httpDownload('PUT', this.api.report.run.publisher, parameters, {format: format});
    }

    public getPublisherAccount(accountId: number): Promise<IdName> {
        return this.httpGet(this.api.publisher.get, {
            accountId: +accountId
        });
    }

    public getPublisherAccounts(): Promise<Array<any>> {
        return this.httpGet(this.api.publisher.list, {});
    }
}