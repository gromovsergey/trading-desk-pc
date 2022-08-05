import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService} from '../../common/services/common.service';

@Injectable()
export class AdvertiserReportService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getReportMeta(parameters: AdvertiserReportParameters): Promise<ReportMeta> {
    return this.httpPut(this.api.report.meta.advertiser, parameters);
  }

  generateReport(parameters: AdvertiserReportParameters): Promise<any> {
    return this.httpPut(this.api.report.run.advertiser, parameters, {format: 'JSON'});
  }

  downloadReport(parameters: AdvertiserReportParameters, format: string): Promise<any> {
    return this.httpDownload('PUT', this.api.report.run.advertiser, parameters, {format});
  }
}
