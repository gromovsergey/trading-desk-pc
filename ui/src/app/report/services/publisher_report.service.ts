import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService} from '../../common/services/common.service';
import {Observable} from 'rxjs';

@Injectable()
export class PublisherReportService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  public getReportMeta(parameters: PublisherReportParameters): Observable<ReportMeta> {
    return this.http.put<ReportMeta>(`${this.host}${this.api.report.meta.publisher}`, parameters);
  }

  public generateReport(parameters: PublisherReportParameters): Promise<any> {
    return this.httpPut(this.api.report.run.publisher, parameters, {format: 'JSON'});
  }

  public downloadReport(parameters: PublisherReportParameters, format: string): Promise<any> {
    return this.httpDownload('PUT', this.api.report.run.publisher, parameters, {format});
  }

  public getPublisherAccount(accountId: number): Observable<Account> {
    return this.http.get<Account>(`${this.host}${this.api.publisher.get}`, {
      params: this.filterParams({accountId})
    });
  }

  getPublisherAccounts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.host}${this.api.publisher.list}`, {});
  }
}
