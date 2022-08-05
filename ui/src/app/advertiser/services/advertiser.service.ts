import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {API} from '../../const';
import {CommonService} from '../../common/services/common.service';
import {AdvertiserModel, AdvertiserSessionModel, CreativeUpload} from '../models';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';

@Injectable()
export class AdvertiserService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getListByAgencyId(agencyId: number): Promise<any> {
    return this.httpGet(API.agency.advertisers, {agencyId});
  }

  getById(accountId: number): Observable<any> {
    return this.http.get(`${this.host}${API.advertiser.get}`, {
      params: {accountId: accountId.toString()}
    }).pipe(tap(advertiser => {
      new AdvertiserSessionModel().data = advertiser as AdvertiserModel;
    }));
  }

  getAvailableBudget(accountId: number): Promise<any> {
    return this.httpGet(API.advertiser.availableBudget, {accountId});
  }

  getProperties(accountId: number): Promise<any> {
    // @todo missing API.advertiser.properties
    return this.httpGet(API.advertiser.get, {accountId});
  }

  getSiteList$(accountId: number): Observable<IdName[]> {
    return this.http.get<IdName[]>(`${this.host}${API.advertiser.site}`, {params: {accountId: accountId.toString()}});
  }

  getSiteList(accountId: number): Promise<any> {
    return this.httpGet(API.advertiser.site, {accountId});
  }

  getTemplates(accountId: number): Promise<any> {
    return this.httpGet(API.advertiser.templates, {accountId});
  }

  getSizes(templateId: number, accountId: number): Promise<any> {
    return this.httpGet(API.advertiser.sizes, {
      accountId,
      templateId
    });
  }

  getImageTemplate(accountId: number): Promise<any> {
    return this.httpGet(API.advertiser.imageTemplate, {accountId});
  }

  uploadCreatives(creativeUpload: CreativeUpload): Promise<any> {
    return this.httpPost(API.advertiser.uploadCreatives, creativeUpload);
  }

  create(advertiser: AdvertiserModel): Promise<any> {
    return this.httpPost(API.advertiser.add, advertiser);
  }

  update(advertiser: AdvertiserModel): Promise<any> {
    return this.httpPut(API.advertiser.update, advertiser);
  }

  updateStatus(accountId: number, operation: string): Promise<any> {
    return this.httpPut(API.advertiser.updateStatus, {}, {accountId, operation});
  }

  isAllowedLocal(entityId: any, name: string): Promise<boolean> {
    return this.httpGet(API.restrictionLocal, {name, entityId});
  }
}
