import { Injectable } from '@angular/core';
import { Http }       from '@angular/http';
import { Router }     from '@angular/router';

import { CommonService } from '../common/common.service';
import { CommentModel }  from './comment.model';


@Injectable()
export class DashboardService extends CommonService {

    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public getDashboardStats(): Promise<any>{
        return this.httpGet(this.api.dashboard.dashboardStats);
    }
}