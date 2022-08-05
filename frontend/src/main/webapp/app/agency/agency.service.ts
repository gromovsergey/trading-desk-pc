import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService}      from '../common/common.service';
import {AgencyModel}        from './agency.model';
import {AgencySessionModel} from './agency_session.model';


@Injectable()
export class AgencyService extends CommonService{


    constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public getById(id: number): Promise<AgencyModel>{
        let promise = this.httpGet(this.api.agency.get, {
            accountId: +id
        });
        promise.then(agency => {
            new AgencySessionModel().data   = agency;
        });

        return promise;
    }

    public getAvailableBudget(id: number): Promise<any>{
        return this.httpGet(this.api.agency.availableBudget, {
            accountId: +id
        });
    }

    public search(options: {}): Promise<Array<any>>{
        return this.httpGet(this.api.agency.search, options);
    }

    public getProperties(accountId: number): Promise<any>{
        return this.httpGet(this.api.agency.properties, {
            accountId: +accountId
        });
    }

    public getAccountSearchParams(){
        return this.httpGet(this.api.agency.searchParams);
    }
}