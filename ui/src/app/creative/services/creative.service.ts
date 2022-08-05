import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {API} from '../../const';
import {CommonService} from '../../common/services/common.service';


@Injectable()
export class CreativeService extends CommonService {

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  getCreative(creativeId: number): Promise<any> {
    return this.httpGet(API.creative.get, {creativeId});
  }

  getListByAdvertiserId(accountId: number): Promise<any> {
    return this.httpGet(API.creative.advertiserList, {accountId});
  }

  getLinkListByLineItemId(lineItemId: number): Promise<any> {
    return this.httpGet(API.lineItem.list, {lineItemId});
  }

  creativeStatusChange(creativeId: number, name: string): Promise<any> {
    return this.httpPut(API.creative.status, null, {
      creativeId,
      name
    });
  }

  getPreviewUrl(creativeId: number): Promise<any> {
    return this.httpGet(API.creative.preview, {creativeId});
  }

  createCreative(creative: Creative): Promise<any> {
    return this.httpPost(API.creative.post, creative);
  }

  updateCreative(creative: Creative): Promise<any> {
    return this.httpPut(API.creative.post, creative);
  }

  getCategories(): Promise<any> {
    return this.httpGet(API.creative.categories);
  }

  getOptions(accountId: number, sizeId: number, templateId: number): Promise<any> {
    return this.httpGet(API.creative.options, {
      accountId,
      sizeId,
      templateId
    });
  }

  getContentCategories(accountId: number): Promise<any> {
    return this.httpGet(API.creative.contentCategories, {accountId});
  }

  getLivePreview(creative: Creative): Promise<any> {
    return this.httpPost(API.creative.livePreview, creative);
  }

  copy(creativeId: number) {
    return this.httpPost(API.creative.copy, null, {creativeId});
  }
}
