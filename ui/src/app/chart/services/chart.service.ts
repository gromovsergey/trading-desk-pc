import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService, RequestIdFilter} from '../../common/services/common.service';


@Injectable()
export class ChartService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  // @RequestIdFilter
  getSeries(params: any): Promise<any> {
    return this.httpGet(this.api.chart.data, params);
  }
}
