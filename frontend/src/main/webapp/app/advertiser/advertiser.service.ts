import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Router} from '@angular/router';

import {CommonService} from '../common/common.service';
import {AdvertiserModel} from './advertiser.model';
import {AdvertiserSessionModel} from './advertiser_session.model';
import {CreativeUpload} from "./creative_upload.model";

@Injectable()
export class AdvertiserService extends CommonService {

    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public getListByAgencyId(id: number): Promise<Array<any>>{
        return this.httpGet(this.api.agency.advertisers, {
            agencyId: +id
        });
    }

    public getById(id: number): Promise<AdvertiserModel>{
        let promise = this.httpGet(this.api.advertiser.get, {
            accountId: +id
        });
        promise.then(advertiser => {
            new AdvertiserSessionModel().data   = advertiser;
        });

        return promise;
    }

    public getAvailableBudget(id: number): Promise<any>{
        return this.httpGet(this.api.advertiser.availableBudget, {
            accountId: +id
        });
    }

    public getProperties(accountId: number): Promise<any>{
        return this.httpGet(this.api.advertiser.properties, {
            accountId: +accountId
        });
    }

    public getSiteList(accountId: number): Promise<any>{
        return this.httpGet(this.api.advertiser.site, {
            accountId: +accountId
        });
    }

    public getTemplates(accountId: number): Promise<any>{
        return this.httpGet(this.api.advertiser.templates, {
            accountId: +accountId
        });
    }

    public getSizes(templateId: number, accountId: number): Promise<any>{
        return this.httpGet(this.api.advertiser.sizes, {
            templateId: +templateId,
            accountId: +accountId
        });
    }

    public getImageTemplate(accountId: number): Promise<any>{
        return this.httpGet(this.api.advertiser.imageTemplate, {
            accountId: +accountId
        });
    }

    public uploadCreatives(creativeUpload: CreativeUpload): Promise<any>{
        return this.httpPost(this.api.advertiser.uploadCreatives, creativeUpload);
    }

    public create(advertiser: AdvertiserModel): Promise<any>{
        return this.httpPost(this.api.advertiser.add, advertiser);
    }

    public update(advertiser: AdvertiserModel): Promise<any>{
        return this.httpPut(this.api.advertiser.update, advertiser);
    }

    public updateStatus(accountId: number, operation: string): Promise<any>{
        return this.httpPut(this.api.advertiser.updateStatus, {}, {
            accountId: accountId,
            operation: operation
        });
    }
}