import {Triggers} from './trigger.model';
import {BehavioralParameters} from './behavioral-parameters.model';
import {AdvertiserModel} from '../../advertiser/models';

export class BehavioralChannel {
  id: number;
  name: string;
  displayStatus: string;
  country: string;
  account: AdvertiserModel;
  visibility: string;
  pageKeywords: Triggers;
  searchKeywords: Triggers;
  urls: Triggers;
  urlKeywords: Triggers;
  behavioralParameters: BehavioralParameters[];

  constructor() {
    this.account = new AdvertiserModel();
    this.pageKeywords = new Triggers();
    this.searchKeywords = new Triggers();
    this.urls = new Triggers();
    this.urlKeywords = new Triggers();
    this.behavioralParameters = [];
  }
}
