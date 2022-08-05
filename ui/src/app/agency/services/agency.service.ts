import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {AgencySessionModel} from '../models/agency-session.model';
import {API} from '../../const';
import {CommonService} from '../../common/services/common.service';


@Injectable()
export class AgencyService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getById(accountId: number): Promise<any> {
    const promise = this.httpGet(API.agency.get, {accountId: accountId.toString()});

    promise.then(agency => {
      new AgencySessionModel().data = agency as AgencyModel;
    });

    return promise;
  }

  getAvailableBudget(accountId: number): Promise<any> {
    return this.httpGet(API.agency.availableBudget, {accountId: accountId.toString()});
  }

  search(options: any): Promise<any> {
    return this.httpGet(API.agency.search, options);
  }

  getProperties(accountId: number): Promise<any> {
    return this.httpGet(API.agency.get, { // @todo missing API.agency.properties
      accountId: accountId.toString()
    });
  }

  getAccountSearchParams(): Promise<AgencySearchParams> {
    return this.httpGet(API.agency.searchParams);
  }

  isAllowedLocal(entityId: any, name: string): Promise<boolean> {
    return this.httpGet(API.restrictionLocal, {
      name,
      entityId
    });
  }

  getDashboardStats(): Promise<any> {
    return this.httpGet(API.dashboard.dashboardStats);
  }
}
