import { Injectable } from '@angular/core';
import { Http }       from '@angular/http';
import { Router }     from '@angular/router';

import { CommonService } from '../common/common.service';
import { CommentModel }  from '../agency/comment.model';


@Injectable()
export class AgentReportService extends CommonService {

    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public getTotalStats(): Promise<any>{
        return this.httpGet(this.api.agentReport.totalStats);
    }

    public getMonthlyStats(year: number, month: number): Promise<any>{
        return this.httpGet(this.api.agentReport.monthlyStats, {
            year: year,
            month: month
        });
    }

    public downloadMonthlyStatsFile(year: number, month: number): Promise<any>{
        return this.httpDownload('GET', this.api.agentReport.monthlyStatsFile, {}, {
            year: year,
            month: month
        });
    }

    public saveMonthlyStats(year: number, month: number, stat: any): Promise<any>{
        return this.httpPut(this.api.agentReport.monthlyStats, stat, {
            year: year,
            month: month
        });
    }

    public closeMonthlyStats(year: number, month: number, stat: any): Promise<any>{
        return this.httpPut(this.api.agentReport.closeMonthlyStats, stat, {
            year: year,
            month: month
        });
    }
}