import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService} from '../../common/services/common.service';

@Injectable()
export class SegmentsReportService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getReportMeta(parameters: SegmentsReportParameters): Promise<ReportMeta> {
    return this.httpPut(this.api.report.meta.segments, parameters);
  }

  generateReport(parameters: SegmentsReportParameters): Promise<any> {
    return this.httpPut(this.api.report.run.segments, parameters, {format: 'JSON'});
  }

  downloadReport(parameters: SegmentsReportParameters, format: string): Promise<any> {
    return this.httpDownload('PUT', this.api.report.run.segments, parameters, {format});
  }
}
