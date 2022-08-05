import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService} from '../../common/services/common.service';

@Injectable()
export class ConversionsReportService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getReportMeta(parameters: ConversionsReportParameters): Promise<ReportMeta> {
    return this.httpPut(this.api.report.meta.conversions, parameters);
  }

  generateReport(parameters: ConversionsReportParameters): Promise<any> {
    return this.httpPut(this.api.report.run.conversions, parameters, {format: 'JSON'});
  }

  downloadReport(parameters: ConversionsReportParameters, format: string): Promise<any> {
    return this.httpDownload('PUT', this.api.report.run.conversions, parameters, {format});
  }
}
