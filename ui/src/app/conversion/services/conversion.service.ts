import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {ConversionContainerModel} from '../models/conversion-container.model';
import {API} from '../../const';
import {CommonService} from '../../common/services/common.service';
import {Observable} from "rxjs";

export interface IAdvertiserConversions {
  checked: boolean;
  conversion: {
    account: any;
    clickWindow: number;
    conversionCategory: string;
    id: number;
    impWindow: number;
    name: string;
    status: string;
    updated: number;
    url: number;
    value: number | number[];
  }
  displayStatus: string;
  pixelCode: number | number[];
  _id: number | number[];
}

@Injectable()
export class ConversionService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getListByAdvertiserId(accountId: number): Promise<any> {
    return this.httpGet(API.advertiser.conversions, {accountId})
      .then(list => list.map(item => {
        item._id = item.conversion.id;
        return item;
      }));
  }

  getListByAdvertiserId$(accountId: number): Observable<IAdvertiserConversions[]> {
    return this.httpGet$(API.advertiser.conversions, {accountId});
  }

  getById(conversionId: number): Promise<any> {
    return this.httpGet(API.conversion.get, {conversionId});
  }

  create(conversion: ConversionContainerModel): Promise<any> {
    return this.httpPost(API.conversion.create, conversion);
  }

  update(conversion: ConversionContainerModel): Promise<any> {
    return this.httpPut(API.conversion.update, conversion);
  }

  updateStatus(conversionId: number, operation: string): Promise<any> {
    return this.httpPut(API.conversion.updateStatus, {}, {
      operation,
      conversionId});
  }

  getPixelCode(conversionId: number): Promise<any> {
    return this.httpGet(API.conversion.pixelCode, {conversionId});
  }
}
