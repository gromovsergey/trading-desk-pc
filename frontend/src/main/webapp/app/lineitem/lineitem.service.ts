import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService, RequestIdFilter} from '../common/common.service';
import {FlightModel}   from '../flight/flight.model';


@Injectable()
export class LineItemService extends CommonService{

    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public getListByFlightId(flightId: number, startDate: string, endDate: string): Promise<Array<any>>{
        return this.httpGet(this.api.lineItem.list, {
            flightId: +flightId,
            startDate: startDate,
            endDate: endDate
        });
    }

    public getById(lineItemId: number): Promise<any>{
        return this.httpGet(this.api.lineItem.get, {
            lineItemId: lineItemId
        });
    }

    public update(lineItem: FlightModel): Promise<any>{
        return this.httpPut(this.api.lineItem.update, lineItem);
    }

    public save(lineItem: FlightModel): Promise<any>{
        return this.httpPost(this.api.lineItem.add, lineItem);
    }

    @RequestIdFilter
    public getStatsById(lineItemId: number): Promise<any>{
        return this.httpGet(this.api.lineItem.stats, {
            lineItemId: lineItemId
        });
    }

    public changeStatus(lineItemIds: any, name: string): Promise<any>{
        return this.httpPut(this.api.lineItem.status, {}, {
            lineItemIds: lineItemIds,
            name: name
        });
    }

    @RequestIdFilter
    public getSiteList(lineItemId: number): Promise<any>{
        return this.httpGet(this.api.lineItem.site, {
            lineItemId: lineItemId
        });
    }

    public linkSites(lineItemId: number, siteIds: Array<number>): Promise<Array<any>>{
        return this.httpPut(this.api.lineItem.linkSites, siteIds, {
            lineItemId: lineItemId
        });
    }

    public linkChannels(lineItemId: number, channelIds: Array<number>, linkSpecialChannelFlag: boolean): Promise<Array<any>>{
        return this.httpPut(this.api.lineItem.linkChannels,
            { channelIds: channelIds, linkSpecialChannelFlag: linkSpecialChannelFlag },
            { lineItemId: lineItemId }
        );
    }

    @RequestIdFilter
    public getLinkedChannels(lineItemId: number): Promise<any>{
        return this.httpGet(this.api.lineItem.channelsStat, {
            lineItemId: lineItemId
        });
    }

    @RequestIdFilter
    public getCreativeList(lineItemId: number): Promise<Array<any>>{
        return this.httpGet(this.api.lineItem.creativeList, {
            lineItemId: lineItemId
        });
    }

    public linkCreatives(lineItemId: number, creativeIds: Array<number>): Promise<Array<any>>{
        return this.httpPut(this.api.lineItem.creativeLink, creativeIds, {
            lineItemId: lineItemId
        });
    }

    public linkStatusChange(lineItemId: number, creativeId: number, status: string): Promise<any>{
        return this.httpPut(this.api.creative.linkStatus, {}, {
            lineItemId: lineItemId,
            creativeId: creativeId,
            name: status
        });
    }
}