import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService} from '../common/common.service';
import {Creative}      from './creative';


@Injectable()
export class CreativeService extends CommonService{

    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public getCreative(creativeId: number): Promise<Creative>{
        return this.httpGet(this.api.creative.get, {
            creativeId: creativeId
        });
    }

    public getListByAdvertiserId(accountId: number): Promise<Array<any>>{
        return this.httpGet(this.api.creative.advertiserList, {
            accountId: accountId
        });
    }

    public getLinkListByLineItemId(lineItemId: number): Promise<Array<any>>{
        return this.httpGet(this.api.creative.link, {
            lineItemId: lineItemId
        });
    }

    public creativeStatusChange(creativeId: number, status: string): Promise<String>{
        return this.httpPut(this.api.creative.status, {}, {
            creativeId: creativeId,
            name: status
        });
    }

    public getPreviewUrl(creativeId: number): Promise<any>{
        return this.httpGet(this.api.creative.preview, {
            creativeId: creativeId
        });
    }

    public createCreative(creative: Creative): Promise<any>{
        return this.httpPost(this.api.creative.post, creative);
    }

    public updateCreative(creative: Creative): Promise<any>{
        return this.httpPut(this.api.creative.post, creative);
    }

    public getCategories(): Promise<any>{
        return this.httpGet(this.api.creative.categories);
    }

    public getOptions(accountId: number, sizeId: number, templateId: number): Promise<any>{
        return this.httpGet(this.api.creative.options, {
            accountId: accountId,
            sizeId: sizeId,
            templateId: templateId
        });
    }

    public getContentCategories(accountId: number): Promise<any>{
        return this.httpGet(this.api.creative.contentCategories, {
            accountId: accountId
        });
    }

    public getLivePreview(creative: Creative): Promise<any>{
        return this.httpPost(this.api.creative.livePreview, creative);
    }
}