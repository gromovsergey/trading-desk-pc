import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService, RequestIdFilter} from '../common/common.service';
import {FlightModel}        from './flight.model';


@Injectable()
export class FlightService extends CommonService {

    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public getStatListByAdvertiserId(id: number, startDate: string, endDate: string): Promise<Array<{}>>{
        return this.httpGet(this.api.advertiser.flights, {
            accountId: +id,
            startDate: startDate,
            endDate: endDate
        });
    }

    public getListByAdvertiserId(id: number): Promise<Array<{}>>{
        return this.httpGet(this.api.advertiser.flightsList, {
            accountId: +id
        });
    }

    public getById(id: number): Promise<any>{
        let promise = this.httpGet(this.api.flight.get, {
            flightId: +id
        });

        return promise;
    }

    @RequestIdFilter
    public getStatsById(id: number): Promise<{}>{
        return this.httpGet(this.api.flight.stats, {
            flightId: +id
        });
    }

    public update(flight: FlightModel): Promise<any>{
        return this.httpPut(this.api.flight.update, flight);
    }

    public save(flight: FlightModel): Promise<any>{
        return this.httpPost(this.api.flight.add, flight);
    }

    public changeStatus(id: any, name: string): Promise<any>{
        return this.httpPut(this.api.flight.status, {}, {
            flightId: +id,
            name: name
        });
    }

    public getGeotargetPlaces(flightId: number): Promise<any>{
        return this.httpGet(this.api.flight.geotarget, {
            flightId: +flightId
        });
    }

    @RequestIdFilter
    public getSiteList(flightId: number): Promise<any>{
        return this.httpGet(this.api.flight.site, {
            flightId: flightId
        });
    }

    public getDeviceTree(id: number, type?: string){
        let params = {};
        switch (type){
            case 'lineitem':
                params['lineItemId']    = id;
                break;
            case 'flight':
                params['flightId']    = id;
                break;
            default:
                params['accountId']    = id;
        }
        return this.httpGet(this.api.flight.device, params);
    }

    public getAttachments(flightId: number): Promise<any>{
        return this.httpGet(this.api.flight.attachments, {
            flightId: flightId
        });
    }

    public downloadAttachments(flightId: number, name: string): Promise<any>{
        return this.httpDownload('GET', this.api.flight.attachmentDownload, {}, {
            flightId: flightId,
            name: name
        });
    }

    public deleteAttachments(flightId: number, name: string): Promise<any>{
        return this.httpDelete(this.api.flight.attachmentDelete, {}, {
            flightId: flightId,
            name: name
        });
    }

    public linkChannels(flightId: number, channelIds: Array<number>, linkSpecialChannelFlag: number): Promise<Array<any>>{
        return this.httpPut(this.api.flight.linkChannels,
            { channelIds: channelIds, linkSpecialChannelFlag: linkSpecialChannelFlag },
            { flightId: flightId }
        );
    }

    @RequestIdFilter
    public getLinkedChannels(flightId: number): Promise<any>{
        return this.httpGet(this.api.flight.channelsStat, {
            flightId: flightId
        });
    }

    public linkSites(flightId: number, siteIds: Array<number>): Promise<Array<any>>{
        return this.httpPut(this.api.flight.linkSites, siteIds, {
            flightId: flightId
        });
    }

    @RequestIdFilter
    public getCreativeList(flightId: number): Promise<Array<any>>{
        return this.httpGet(this.api.flight.creativeList, {
            flightId: flightId
        });
    }

    public linkCreatives(flightId: number, creativeIds: Array<number>): Promise<Array<any>>{
        return this.httpPut(this.api.flight.creativeLink, creativeIds, {
            flightId: flightId
        });
    }

    public linkStatusChange(flightId: number, creativeId: number, status: string): Promise<any>{
        return this.httpPut(this.api.creative.linkStatus, {}, {
            flightId: flightId,
            creativeId: creativeId,
            name: status
        });
    }
}