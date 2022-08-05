import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService} from './common.service';

@Injectable()
export class GeoService extends CommonService{

    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public getLocations(geoIds: Array<number>): Promise<any>{
        return this.httpGet(this.api.geo.getLocations, {
            language: 'ru',
            geoIds: geoIds
        });
    }

    public getAddresses(geoIds: Array<number>): Promise<any>{
        return this.httpGet(this.api.geo.getAddresses, {
            geoIds: geoIds
        });
    }

    public searchLocation(text: string): Promise<any>{
        return this.httpGet(this.api.geo.searchLocation, {
            country: 'RU',
            language: 'ru',
            text: text
        });
    }

    public searchAddress(geoCode: string): Promise<any>{
        return this.httpGet(this.api.geo.searchAddress, {
            geoCode: geoCode
        });
    }
}