import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService} from '../../common/services/common.service';
import {API} from '../../const';
import {Observable} from "rxjs";

@Injectable()
export class LineItemService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getListByFlightId(flightId: number, startDate: string, endDate: string): Promise<any> {
    return this.httpGet(API.lineItem.list, {
      flightId,
      startDate,
      endDate
    });
  }

  getById(lineItemId: number): Promise<any> {
    return this.httpGet(API.lineItem.get, {lineItemId});
  }

  getById$(lineItemId: number): Observable<any> {
    return this.httpGet$(API.lineItem.get, {lineItemId});
  }

  update(lineItem: any): Promise<any> {
    return this.httpPut(API.lineItem.update, lineItem);
  }

  save(lineItem: any): Promise<any> {
    return this.httpPost(API.lineItem.add, lineItem);
  }

  // @RequestIdFilter
  getStatsById(lineItemId: number): Promise<any> {
    return this.httpGet(API.lineItem.stats, {lineItemId});
  }

  copy(lineItemId: number): Promise<any> {
    return this.httpPost(API.lineItem.copy, {}, {lineItemId});
  }

  changeStatus(lineItemIds: any, name: string): Promise<any> {
    return this.httpPut(API.lineItem.status, {}, {
      lineItemIds,
      name,
    });
  }

  public changeSite$(lineItemIds: number[], siteIds: number[], editMode: string, name: string): Observable<void> {
    return this.httpPut$(API.lineItem.status, {siteIds: siteIds}, {
      lineItemIds,
      editMode,
      name,
    });
  }

  public changeGeo$(lineItemIds: number[], cityId, editMode: string, name: string): Observable<void> {
    return this.httpPut$(API.lineItem.status, cityId, {
      lineItemIds,
      editMode,
      name,
    });
  }

  public changeDevise$(lineItemIds: number[], deviceId, editMode: string, name: string): Observable<void> {
    return this.httpPut$(API.lineItem.status, deviceId, {
      lineItemIds,
      editMode,
      name,
    });
  }

  public changeRates$(lineItemIds: number[], rates, name: string): Observable<void> {
    return this.httpPut$(API.lineItem.status, rates, {
      lineItemIds,
      name,
    });
  }

  // @RequestIdFilter
  getSiteList(lineItemId: number): Promise<any> {
    return this.httpGet(API.lineItem.site, {lineItemId});
  }

  linkSites(lineItemId: number, siteIds: Array<number>): Promise<any> {
    return this.httpPut(API.lineItem.linkSites, siteIds, {lineItemId});
  }

  linkChannels(lineItemId: number, channelIds: Array<number>, linkSpecialChannelFlag: boolean): Promise<any> {
    return this.httpPut(API.lineItem.linkChannels,
        {channelIds, linkSpecialChannelFlag},
        {lineItemId}
    );
  }

  // @RequestIdFilter
  getLinkedChannels(lineItemId: number): Promise<any> {
    return this.httpGet(API.lineItem.channelsStat, {lineItemId});
  }

  // @RequestIdFilter
  getCreativeList(lineItemId: number): Promise<any> {
    return this.httpGet(API.lineItem.creativeList, {lineItemId});
  }

  linkCreatives(lineItemId: number, creativeIds: Array<number>): Promise<any> {
    return this.httpPut(API.lineItem.creativeLink, creativeIds, {lineItemId});
  }

  linkStatusChange(lineItemId: any, creativeId: any, name: string): Promise<any> {
    return this.httpPut(API.creative.linkStatus, {}, {
      lineItemId,
      creativeId,
      name
    });
  }
}
