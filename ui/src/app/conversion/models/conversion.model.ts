import {AdvertiserModel} from '../../advertiser/models';

export class ConversionModel {
    public id: number;
    public account: AdvertiserModel;
    public status: string;
    public name: string;
    public conversionCategory: string;
    public url: string;
    public impWindow: number;
    public clickWindow: number;

    constructor() {
        this.account = new AdvertiserModel();
    }
}
