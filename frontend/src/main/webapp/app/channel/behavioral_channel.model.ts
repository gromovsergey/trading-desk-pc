import {AdvertiserModel}      from '../advertiser/advertiser.model';
import {Triggers}             from './trigger.model';
import {BehavioralParameters} from './behavioral_parameters.model';

export class BehavioralChannel {
    public id: number;
    public name: string;
    public displayStatus: string;
    public country: string;
    public account: AdvertiserModel;
    public visibility: string;
    public pageKeywords: Triggers;
    public searchKeywords: Triggers;
    public urls: Triggers;
    public urlKeywords: Triggers;
    public behavioralParameters: Array<BehavioralParameters>;

    constructor() {
        this.account = new AdvertiserModel();
        this.pageKeywords = new Triggers();
        this.searchKeywords = new Triggers();
        this.urls = new Triggers();
        this.urlKeywords = new Triggers();
        this.behavioralParameters = [];
    }
}