import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService, RequestIdFilter} from '../../common/services/common.service';
import {API} from '../../const';
import {Observable, of} from 'rxjs';
import * as http from "http";

interface FlightResponse {
  accountId: number;
  addresses: Array<any>;
  bidStrategy: string;
  blackList: string;
  blackListId: number | number[];
  budget: number | number[];
  channelIds: number | number[];
  clicksDailyLimit: number;
  clicksPacing: string;
  clicksTotalLimit: number;
  conversionIds: number | number[];
  creativeIds: number | number[];
  dailyBudget: any;
  dateEnd: any
  dateStart: string;
  deliveryPacing: string;
  deviceChannelIds: number | number[];
  displayStatus: string;
  emptyProps: string | string[];
  excludedAddresses: any[];
  excludedGeoChannelIds: any[];
  frequencyCap: any;
  geoChannelIds: number | number[];
  id: number | number[];
  impressionsDailyLimit: any;
  impressionsPacing: string;
  impressionsTotalLimit: any;
  ioId: number | number[];
  minCtrGoal: number;
  name: string;
  rateType: string;
  rateValue: number | number[];
  schedules: any[];
  siteIds: number | number[];
  specialChannelLinked: boolean;
  version: number;
  version2: number;
  whiteList: string;
  whiteListId: any;
}

export interface IFlightStatus {
  budget: number;
  clicks: number;
  ctr: number;
  displayStatus: string;
  ecpm: number;
  id: number;
  impressions: number;
  name: string;
  postClickConv: number;
  postImpConv: number;
  requests: number;
  spentBudget: number;
  totalCost: number;
}

@Injectable()
export class FlightService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getStatListByAdvertiserId(accountId: number, startDate: string, endDate: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.host}${API.advertiser.flights}`, {
      params: this.filterParams({
        accountId: accountId.toString(),
        startDate,
        endDate
      })
    });
  }

  getListByAdvertiserId(accountId: number): Promise<any> {
    return this.httpGet(API.advertiser.flightsList, {accountId});
  }

  getById(flightId: number): Promise<any> {
    return this.httpGet(API.flight.get, {flightId});
  }

  getById$(flightId: number): Observable<FlightResponse> {
    return this.httpGet$(API.flight.get, {flightId});
  }

  // @RequestIdFilter
  getStatsById(flightId: number): Promise<any> {
    return this.httpGet(API.flight.stats, {flightId});
  }

  getStatsById$(flightId: number): Observable<IFlightStatus> {
    return this.httpGet$(API.flight.stats, {flightId});
  }

  update(flight: any): Promise<any> {
    return this.httpPut(API.flight.update, flight);
  }

  update$(flight: any): Observable<void> {
    return this.httpPut$(API.flight.update, flight);
  }

  save$(flight: any, params?: HttpParams | any): Observable<void> {
    return this.http.post<void>(this.host + API.flight.add, flight, {params: this.filterParams(params)});
  }

  save(flight: any): Promise<any> {
    return this.httpPost(API.flight.add, flight);
  }

  copy(flightId: number): Promise<any> {
    return this.httpPost(API.flight.copy, {}, {flightId});
  }

  changeStatus(flightId: any, name: string): Promise<any> {
    return this.httpPut(API.flight.status, {}, {
      flightId,
      name
    });
  }

  // @RequestIdFilter
  getSiteList(flightId: number): Promise<any> {
    return this.httpGet(API.flight.site, {flightId});
  }

  getSiteList$(flightId: number): Observable<any> {
    return this.httpGet$(API.flight.site, {flightId});
  }

  getDeviceTree(id: number, type?: string): Promise<any> {
    const params: any = {};
    switch (type) {
      case 'lineitem':
        params.lineItemId = id;
        break;
      case 'flight':
        params.flightId = id;
        break;
      default:
        params.accountId = id;
    }
    return this.httpGet(API.flight.device, params);
  }

  getAttachments(flightId: number): Promise<any> {
    return this.httpGet(API.flight.attachments, {flightId});
  }

  downloadAttachments(flightId: number, name: string): Promise<any> {
    return this.httpDownload('GET', API.flight.attachmentDownload, {}, {
      flightId,
      name
    });
  }

  deleteAttachments(flightId: number, name: string): Promise<any> {
    return this.httpDelete(API.flight.attachmentDelete, {
      flightId,
      name
    });
  }

  linkChannels(flightId: number, channelIds: Array<number>, linkSpecialChannelFlag: number): Promise<any> {
    return this.httpPut(API.flight.linkChannels,
        {channelIds, linkSpecialChannelFlag},
        {flightId});
  }

  // @RequestIdFilter
  getLinkedChannels(flightId: number): Promise<any> {
    return this.httpGet(API.flight.channelsStat, {flightId});
  }

  linkSites(flightId: number, siteIds: Array<number>): Promise<any> {
    return this.httpPut(API.flight.linkSites, siteIds, {flightId});
  }

  linkSites$(flightId: number, siteIds: Array<number>): Observable<void> {
    console.log("flightId", flightId)
    console.log("siteIds", siteIds)
    return of();
    //return this.httpPut$(API.flight.linkSites, siteIds, {flightId});
  }

  // @RequestIdFilter
  getCreativeList(flightId: number): Promise<any> {
    return this.httpGet(API.flight.creativeList, {flightId});
  }

  linkCreatives(flightId: number, creativeIds: Array<number>): Promise<any> {
    return this.httpPut(API.flight.creativeLink, creativeIds, {flightId});
  }

  linkStatusChange(flightId: number, creativeId: number, name: string): Promise<any> {
    return this.httpPut(API.creative.linkStatus, {}, {
      flightId,
      creativeId,
      name
    });
  }

  setPartAll(flightId: number, flightPart: string, accountId: number): Observable<void> {
    return this.httpPost$(API.flight.applyToStrategies, {
      flightId,
      flightPart,
      accountId
    }, {});
  }
}
