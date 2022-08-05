import {Component, OnInit}                    from '@angular/core';
import { CurrencyPipe }                       from '@angular/common';
import {RouterModule, Router, ActivatedRoute} from '@angular/router';
import {FormsModule}                          from '@angular/forms';

import { LoadingComponent }       from '../shared/loading.component';
import { PageComponent }          from '../shared/page.component';
import { IconComponent }          from '../shared/icon.component';
import { DisplayStatusDirective } from '../shared/display_status.directive';
import { AuthService }            from '../common/auth.service';
import { AdvertiserSessionModel } from '../advertiser/advertiser_session.model';
import { UserSessionModel }       from '../user/user_session.model';

import { AgencyService }      from './agency.service';
import { AgencySessionModel } from './agency_session.model';

@Component({
    selector: 'ui-agency-search',
    templateUrl: 'search.html'
})

export class AgencySearchComponent extends PageComponent implements OnInit{

    public title:string = 'Accounts';

    public filter = {
        name: '',
        displayStatuses: null,
        country: '',
        accountRole: null
    };
    private filterState: boolean = false;
    public agencyList: Array<Object>;
    public wait: boolean       = false;
    public waitParams: boolean = false;
    private searchParams: any;

    public constructor(private agencyService: AgencyService,
                       private router: Router,
                       private route: ActivatedRoute,
                       private authService: AuthService){
        super();
        let user    = new UserSessionModel();
        if (!user.isInternal()){
            this.authService.navigateDefault(user.role, user.accountId);
        }

        this.title = '_L10N_(accountSearch.accounts)';
    }

    ngOnInit(){
        new AgencySessionModel().clear();
        new AdvertiserSessionModel().clear();

        this.loadSearchParams();
    }

    private toggleFilter(e: any){
        e.stopPropagation();
        e.preventDefault();
        this.filterState = !this.filterState;
    }

    public search(){
        this.agencyList = null;
        this.wait = true;
        this.agencyService
            .search(this.getFilter())
            .then(agencyList => {
                this.agencyList = agencyList;
                this.wait = false;
            });
    }

    private loadSearchParams(){
        this.waitParams = true;
        return this.agencyService.getAccountSearchParams().then(params => {
            this.searchParams   = params;
            this.filter.accountRole = this.searchParams.accountRoles[0].roleId;
            this.filter.displayStatuses = this.searchParams.displayStatuses[0].type;
            this.waitParams     = false;
        });
    }

    private getFilter(){
        let _f  = Object.assign({}, this.filter);
        if (!_f.displayStatuses)    delete _f.displayStatuses;
        if (!_f.country)        delete _f.country;
        return _f;
    }
}
