import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';

import {CommonService} from './common.service';

@Injectable()
export class GeoService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  public getLocations(geoIds: Array<number>): Promise<any> {
    return this.httpGet(this.api.geo.getLocations, {
      language: 'ru',
      geoIds
    });
  }

  public getAddresses(geoIds: Array<number>): Promise<any> {
    return this.httpGet(this.api.geo.getAddresses, {
      geoIds
    });
  }

  public searchLocation(text: string): Promise<any> {
    return this.httpGet(this.api.geo.searchLocation, {
      country: 'RU',
      language: 'ru',
      text
    });
  }

  public searchAddress(geoCode: string): Promise<any> {
    return this.httpGet(this.api.geo.searchAddress, {
      geoCode
    });
  }
}
