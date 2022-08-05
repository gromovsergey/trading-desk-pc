import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService} from '../common/common.service';
import {AccountModel} from "./account.model";
import {DynamicLocalizationModel} from "./dynamic_localization.model";
import {appConstants} from '../common/common.const';


@Injectable()
export class ChannelService extends CommonService {

    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public getChannels(accountId: number): Promise<any>{
        return this.httpGet(this.api.channel.accountChannels, {
            accountId: accountId
        });
    }

    public statusChange(channelId: number, status: string, flightId?: number, lineItemId?: number): Promise<any>{
        let params  = {
            channelId: channelId,
            name: status
        };
        if (flightId !== undefined){
            params['flightId']    = flightId;
        }
        if (lineItemId !== undefined){
            params['lineItemId']    = lineItemId;
        }
        return this.httpPut(this.api.channel.status, {}, params);
    }

    public getBehavioralById(id: number): Promise<any>{
        let promise = this.httpGet(this.api.channel.getBehavioral, {
            channelId: +id
        });
        return promise;
    }

    public getExpressionById(id: number): Promise<any>{
        let promise = this.httpGet(this.api.channel.getExpression, {
            channelId: +id
        });
        return promise;
    }

    public createBehavioral(channel: any): Promise<any>{
        return this.httpPost(this.api.channel.createBehavioral, channel);
    }

    public createExpression(channel: any): Promise<any>{
        return this.httpPost(this.api.channel.createExpression, channel);
    }

    public updateBehavioral(channel: any): Promise<any>{
        return this.httpPut(this.api.channel.updateBehavioral, channel);
    }

    public updateExpression(channel: any): Promise<any>{
        return this.httpPut(this.api.channel.updateExpression, channel);
    }

    public getAccountChannels(accountId: number, name?: string): Promise<any>{
        let params  = {
            accountId: accountId
        };

        if (name){
            params['name']  = name;
        }
        return this.httpGet(this.api.channel.accountChannels, params);
    }

    public getExpressionChannels(accountId: number, countryCode: string, name?: string): Promise<any>{
        return this.httpGet(this.api.channel.expressionChannels, {
            accountId: accountId,
            countryCode: countryCode,
            name: name
        });
    }

    public getExternalChannels(accountId: number, channelIds: Array<number>): Promise<any>{
        return this.httpGet(this.api.channel.externalChannels, {
            channelIds: channelIds,
            extAccountId: accountId
        });
    }

    public getAllChannels(name: string, accountId: number, type: string, visibility: string): Promise<any>{
        let params  = {
            name: name,
            accountId: accountId,
            type: type,
            visibility: visibility
        };

        return this.httpGet(this.api.channel.allChannels, params);
    }

    public channelsSearch(accountId: number, channels: Array<{name: string, accountName: string}>): Promise<any>{
        return this.httpPost(this.api.channel.channelSearch, channels, {
            accountId: accountId
        });
    }

    public channelRubricNodesSearch(accountId: number, source: string): Promise<any>{
        return this.httpGet(this.api.channel.channelRubricNodesSearch, {
            accountId: accountId,
            source: source,
            country: appConstants.country,
            language: appConstants.language
        });
    }

    public channelNodesSearch(parentId: number): Promise<any>{
        return this.httpGet(this.api.channel.channelNodesSearch, {
            parentId: parentId,
            language: appConstants.language
        });
    }

    public getChannelStats(type: string, channelId: number): Promise<any>{
        return this.httpGet(type === 'behavioral' ? this.api.channel.stats.behavioral : this.api.channel.stats.expression, {
            channelId: channelId
        });
    }

    public getDynamicLocalizations(channelId: number): Promise<any>{
        return this.httpGet(this.api.channel.dynamicLocalizations, {
            channelId: channelId
        });
    }

    public updateDynamicLocalizations(channelId: number,
                                      localizations: Array<DynamicLocalizationModel>): Promise<any>{
        return this.httpPut(this.api.channel.dynamicLocalizations,
            localizations,
            { channelId: channelId }
        );
    }

    public deleteDynamicLocalizations(channelId: number,
                                      localizations: Array<DynamicLocalizationModel>): Promise<any>{
        return this.httpDelete(this.api.channel.dynamicLocalizations,
            localizations,
            { channelId: channelId }
        );
    }

    public getChannelOwners(): Promise<Array<any>> {
        return this.httpGet(this.api.channel.channelOwners, {});
    }

    public getInternalAccounts(): Promise<Array<any>> {
        return this.httpGet(this.api.channel.internalAccounts, {});
    }

    public getAccountById(id: number): Promise<AccountModel> {
        return this.httpGet(this.api.channel.getAccount, {
            accountId: +id
        });
    }
}