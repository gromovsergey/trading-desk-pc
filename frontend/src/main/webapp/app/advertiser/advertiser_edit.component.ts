import { Component, Input, OnDestroy, OnInit }  from '@angular/core';
import { FormsModule }                          from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { Subscription }                         from 'rxjs/Rx';

import { LoadingComponent }   from '../shared/loading.component';
import { PageComponent }      from '../shared/page.component';
import { IconComponent }      from '../shared/icon.component';
import { AgencySessionModel } from '../agency/agency_session.model';

import { AdvertiserService }  from './advertiser.service';
import { AdvertiserModel }    from './advertiser.model';

@Component({
    selector: 'ui-advertiser-edit',
    templateUrl: 'edit.html'
})

export class AdvertiserEditComponent extends PageComponent implements OnInit, OnDestroy{

    public backUrl:string;
    private mode: string;

    private routerSubscription: Subscription;

    public title:string;
    public wait: boolean       = true;
    private waitSubmit: boolean = false;
    private agencySessionModel: AgencySessionModel   = new AgencySessionModel();
    private errors: any  = {};
    private advertiser:AdvertiserModel  = new AdvertiserModel();
    public canEditCommission: boolean;

    constructor(private advertiserService: AdvertiserService,
                private route: ActivatedRoute,
                private router: Router){
        super();
        if (this.mode) {
            this.initTitle();
        }
    }

    ngOnInit() {
        this.routerSubscription = this.route.url.subscribe(params => {
            if (params[0].path === 'add') {
                this.mode = 'add';
                this.initTitle();
                this.backUrl = `/agency/${this.agencySessionModel.id}/advertisers`;

                // set default commission
                this.advertiser.commission = 0;

                this.advertiserService.isAllowedLocal0('account.viewAdvertisingFinance')
                    .then(res => {
                        this.canEditCommission = res;
                        this.wait = false;
                    })

            } else {
                this.mode = 'edit';
                this.initTitle();
                this.advertiserService.getById(+params[0].path)
                    .then(advertiser => {
                        this.advertiser = advertiser;
                        this.backUrl = `/advertiser/${this.advertiser.id}/account`;

                        this.advertiserService.isAllowedLocal(this.advertiser.id, 'account.viewAdvertisingFinance')
                            .then(res => {
                                this.canEditCommission = res && this.advertiser.financialFieldsFlag && this.advertiser.selfServiceFlag;
                                this.wait = false;
                            })
                    });
            }
        });
    }

    private initTitle() {
        if (this.mode === 'add') {
            this.title = '_L10N_(button.add)' + ' ' + '_L10N_(advertiserAccount.advertiser.genitive)';
        } else {
            this.title = '_L10N_(button.edit)' + ' ' + '_L10N_(advertiserAccount.advertiser.genitive)';
        }
    }

    ngOnDestroy(){
        if (this.routerSubscription){
            this.routerSubscription.unsubscribe();
        }
    }

    private submitForm(){
        this.waitSubmit = true;
        this.advertiser.agencyId = this.agencySessionModel.id;

        let promise;
        if (this.mode === 'add') {
            promise = this.advertiserService.create(this.advertiser);
        } else {
            promise = this.advertiserService.update(this.advertiser);
        }

        promise
            .then(id => {
                this.waitSubmit = false;
                this.router.navigateByUrl(this.backUrl);
            })
            .catch(e => {
                if (e.status    === 412){
                    this.errors = e.json();
                    this.waitSubmit = false;

                    setImmediate(()=>{
                        let errorEl = window.document.querySelector('.has-error');
                        if (errorEl !== null){
                            let bounds  = errorEl.getBoundingClientRect();
                            window.scrollTo(0, window.scrollY+bounds.top-70);
                        }
                    });
                }
            });
    }
}
