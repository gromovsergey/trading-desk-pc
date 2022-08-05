import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Router} from '@angular/router';

import {CommonService} from '../common/common.service';
import {SegmentsReportParameters, ReportMeta, ReportService, LineItemIdNameModel} from "../report/report";

@Injectable()
export class SegmentsReportService extends CommonService implements ReportService {

    constructor(public router: Router, public http: Http) {
        super(router, http);
    }

    public getReportMeta(parameters: SegmentsReportParameters): Promise<ReportMeta> {
        return this.httpPut(this.api.report.meta.segments, parameters);
    }

    public generateReport(parameters: SegmentsReportParameters): Promise<any> {
        return this.httpPut(this.api.report.run.segments, parameters, {format: 'JSON'});
    }

    public downloadReport(parameters: SegmentsReportParameters, format: string): Promise<any> {
        return this.httpDownload('PUT', this.api.report.run.segments, parameters, {format: format});
    }

    public getListByAdvertiserId(id: number): Promise<Array<LineItemIdNameModel>>{
        return this.httpGet(this.api.advertiser.lineItemsIdNameList, {
            accountId: +id
        });
    }
}
