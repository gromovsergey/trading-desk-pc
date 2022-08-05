import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService, RequestIdFilter} from '../common/common.service';


@Injectable()
export class ChartService extends CommonService{

    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    @RequestIdFilter
    public getSeries(params: any): Promise<any>{
        return this.httpGet(this.api.chart.data, params);
    }
}