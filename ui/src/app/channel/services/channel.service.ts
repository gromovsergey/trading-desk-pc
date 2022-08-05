import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService} from '../../common/services/common.service';
import {DynamicLocalizationModel} from '../models/dynamic-localization.model';
import {appConstants} from '../../common/common.const';


@Injectable({providedIn: 'root'})
export class ChannelService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getChannels(accountId: number): Promise<any> {
    return this.httpGet(this.api.channel.accountChannels, {
      accountId
    });
  }

  statusChange(channelId: any, name: string, flightId?: number, lineItemId?: number): Promise<any> {

    const params: any = {
      channelId,
      name
    };

    if (flightId !== undefined) {
      params.flightId = flightId.toString();
    }
    if (lineItemId !== undefined) {
      params.lineItemId = lineItemId.toString();
    }
    return this.httpPut(this.api.channel.status, {}, params);
  }

  getBehavioralById(channelId: number): Promise<any> {
    return this.httpGet(this.api.channel.getBehavioral, {
      channelId: channelId.toString()
    });
  }

  getExpressionById(channelId: number): Promise<any> {
    return this.httpGet(this.api.channel.getExpression, {
      channelId: channelId.toString()
    });
  }

  createBehavioral(channel: any): Promise<any> {
    return this.httpPost(this.api.channel.createBehavioral, channel);
  }

  createExpression(channel: any): Promise<any> {
    return this.httpPost(this.api.channel.createExpression, channel);
  }

  updateBehavioral(channel: any): Promise<any> {
    return this.httpPut(this.api.channel.updateBehavioral, channel);
  }

  updateExpression(channel: any): Promise<any> {
    return this.httpPut(this.api.channel.updateExpression, channel);
  }

  getAccountChannels(accountId: number, name?: string): Promise<any> {
    const params = {
      accountId: accountId.toString()
    };
    if (name) {
      params['name'] = name;
    }
    return this.httpGet(this.api.channel.accountChannels, params);
  }

  getExpressionChannels(accountId: number, countryCode: string, name?: string): Promise<any> {
    return this.httpGet(this.api.channel.expressionChannels, {
      accountId,
      countryCode,
      name
    });
  }

  getExternalChannels(accountId: number, channelIds: Array<number>): Promise<any> {
    const params = new HttpParams({
      fromObject: {
        extAccountId: accountId.toString()
      }
    });
    channelIds.forEach(id => {
      params.append('channelIds[]', id.toString());
    });

    return this.httpGet(this.api.channel.externalChannels, params);
  }

  getAllChannels(name: string, accountId: any, type: string, visibility: string): Promise<any> {
    return this.httpGet(this.api.channel.allChannels, {
      name,
      type,
      visibility,
      accountId
    });
  }

  channelsSearch(accountId: number, channels: Array<{ name: string; accountName: string }>): Promise<any> {
    return this.httpPost(this.api.channel.channelSearch, channels, {
      accountId: accountId.toString()
    });
  }

  channelRubricNodesSearch(accountId: number, source: string): Promise<any> {
    return this.httpGet(this.api.channel.channelRubricNodesSearch, {
      accountId: accountId.toString(),
      source,
      country: appConstants.country,
      language: appConstants.language
    });
  }

  channelNodesSearch(parentId: number): Promise<any> {
    return this.httpGet(this.api.channel.channelNodesSearch, {
      parentId: parentId.toString(),
      language: appConstants.language
    });
  }

  getChannelStats(type: string, channelId: number): Promise<any> {
    return this.httpGet(type === 'behavioral' ? this.api.channel.stats.behavioral : this.api.channel.stats.expression, {
      channelId: channelId.toString()
    });
  }

  getDynamicLocalizations(channelId: number): Promise<any> {
    return this.httpGet(this.api.channel.dynamicLocalizations, {
      channelId: channelId.toString()
    });
  }

  updateDynamicLocalizations(channelId: number,
                             localizations: Array<DynamicLocalizationModel>): Promise<any> {
    return this.httpPut(this.api.channel.dynamicLocalizations,
      localizations,
      {
        channelId: channelId.toString()
      }
    );
  }

  deleteDynamicLocalizations(channelId: number,
                             localizations: Array<DynamicLocalizationModel>): Promise<any> {
    return this.httpDelete(this.api.channel.dynamicLocalizations,
      {
        channelId: channelId.toString(),
        localizations: JSON.stringify(localizations)
      }
    );
  }

  getChannelOwners(): Promise<Array<any>> {
    return this.httpGet(this.api.channel.channelOwners, {});
  }

  getInternalAccounts(): Promise<Array<any>> {
    return this.httpGet(this.api.channel.internalAccounts, {});
  }

  getAccountById(accountId: number): Promise<AccountModel> {
    return this.httpGet(this.api.channel.getAccount, {
      accountId: accountId.toString()
    });
  }
}
