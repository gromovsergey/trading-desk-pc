import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Router} from '@angular/router';

import {CommonService} from '../common/common.service';
import {AudienceResearch} from "./audienceresearch.model";
import {AudienceResearchStat} from "./audienceresearchstat.model";

@Injectable()
export class AudienceResearchService extends CommonService {

    constructor(public router: Router, public http: Http) {
        super(router, http);
    }

    public getAudienceResearches(): Promise<any> {
        return this.httpGet(this.api.audienceResearch.list, {});
    }

    public getById(id: number): Promise<any> {
        return this.httpGet(this.api.audienceResearch.get, {
            audienceResearchId: id
        });
    }

    public getStat(audienceResearchId: number, channelId: number): Promise<AudienceResearchStat> {
        return this.httpGet(this.api.audienceResearch.stat, {
            audienceResearchId: audienceResearchId,
            channelId: channelId
        });
    }

    public getChannels(text: string, type: string, internalOnly: boolean): Promise<any> {
        return this.httpGet(this.api.audienceResearch.channels, {
            text: text,
            type: type,
            internalOnly: internalOnly
        });
    }

    public getAdvertisers(): Promise<any> {
        return this.httpGet(this.api.audienceResearch.advertisers, {});
    }

    public create(audienceResearch: AudienceResearch): Promise<any> {
        return this.httpPost(this.api.audienceResearch.create, audienceResearch);
    }

    public update(audienceResearch: AudienceResearch): Promise<any> {
        return this.httpPut(this.api.audienceResearch.update, audienceResearch);
    }

    public updateYesterdayComment(comment: string, id: number): Promise<any> {
        return this.httpPut(this.api.audienceResearch.yesterdayComment, null, {
            comment: comment,
            id: id
        });
    }

    public updateTotalComment(comment: string, id: number): Promise<any> {
        return this.httpPut(this.api.audienceResearch.totalComment, null, {
            comment: comment,
            id: id
        });
    }

    public delete(id: number): Promise<any> {
        return this.httpDelete(this.api.audienceResearch.delete, {}, {
            audienceResearchId: id
        });
    }

    public getDynamicLocalizations(channelId: number): Promise<Array<any>>{
        return this.httpGet(this.api.channel.dynamicLocalizations, {
            channelId: channelId
        });
    }
}