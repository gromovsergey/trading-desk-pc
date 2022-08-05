import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService} from '../../common/services/common.service';

@Injectable()
export class DomainsReportService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getReportMeta(parameters: DomainsReportParameters): Promise<ReportMeta> {
    return this.httpPut(this.api.report.meta.domains, parameters);
  }

  generateReport(parameters: DomainsReportParameters): Promise<any> {
    return this.httpPut(this.api.report.run.domains, parameters, {format: 'JSON'});
  }

  downloadReport(parameters: DomainsReportParameters, format: string): Promise<any> {
    return this.httpDownload('PUT', this.api.report.run.domains, parameters, {format});
  }
}
