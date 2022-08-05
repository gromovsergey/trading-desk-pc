import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService} from '../../common/services/common.service';
import {Observable} from 'rxjs';

@Injectable()
export class AgentReportService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getTotalStats(): Observable<any> {
    return this.http.get(`${this.host}${this.api.agentReport.totalStats}`);
  }

  getMonthlyStats(year: number, month: number): Promise<any> {
    return this.httpGet(this.api.agentReport.monthlyStats, {
      year, month
    });
  }

  downloadMonthlyStatsFile(year: number, month: number): Promise<any> {
    return this.httpDownload('GET', this.api.agentReport.monthlyStatsFile, {}, {
      year, month
    });
  }

  saveMonthlyStats(year: number, month: number, stat: any): Promise<any> {
    return this.httpPut(this.api.agentReport.monthlyStats, stat, {
      year, month
    });
  }

  closeMonthlyStats(year: number, month: number, stat: any): Promise<any> {
    return this.httpPut(this.api.agentReport.closeMonthlyStats, stat, {
      year, month
    });
  }
}
