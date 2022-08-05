import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService} from '../../common/services/common.service';
import {API} from '../../const';


@Injectable()
export class AgencyDashboardService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }


}
