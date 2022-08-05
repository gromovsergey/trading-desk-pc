import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService} from './common.service';

@Injectable()
export class QuickSearchService extends CommonService {

    constructor(public router:Router, public http:Http) {
        super(router, http);
    }

    public search(searchText: string):Promise<Array<any>> {
        return this.httpGet(this.api.quick_search, {
            quick_search: searchText
        });
    }
}