import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService}            from '../common/common.service';
import {ConversionModel}          from './conversion.model';
import {ConversionContainerModel} from './conversion.container.model';

@Injectable()
export class ConversionService extends CommonService{

    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public getListByAdvertiserId(id: number): Promise<Array<ConversionContainerModel>>{
        return this.httpGet(this.api.advertiser.conversions, {
            accountId: +id
        }).then(list => {
            list.forEach(v => {
                v._id   = v.conversion.id;
            });
            return list;
        });
    }

    public getById(id: number): Promise<ConversionContainerModel>{
        return this.httpGet(this.api.conversion.get, {
            conversionId: +id
        });
    }

    public create(conversion: ConversionContainerModel): Promise<any>{
        return this.httpPost(this.api.conversion.create, conversion);
    }

    public update(conversion: ConversionContainerModel): Promise<any>{
        return this.httpPut(this.api.conversion.update, conversion);
    }

    public updateStatus(conversionId: number, operation: string): Promise<any>{
        return this.httpPut(this.api.conversion.updateStatus, {}, {
            operation: operation,
            conversionId: conversionId
        });
    }

    public getPixelCode(id: number): Promise<any>{
        return this.httpGet(this.api.conversion.pixelCode, {
            conversionId: +id
        });
    }
}