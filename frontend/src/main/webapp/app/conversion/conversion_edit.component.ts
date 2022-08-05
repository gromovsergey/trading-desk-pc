import { Component, Input, OnDestroy, OnInit }  from '@angular/core';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule }                          from '@angular/forms';
import { Subscription }                         from 'rxjs/Rx';

import { PageComponent }            from '../shared/page.component';
import { IconComponent }            from '../shared/icon.component';
import { LoadingComponent }         from '../shared/loading.component';
import { AdvertiserSessionModel }   from '../advertiser/advertiser_session.model';
import { AdvertiserModel }          from '../advertiser/advertiser.model';
import { ConversionService }        from './conversion.service';
import { ConversionModel }          from './conversion.model';
import { ConversionContainerModel } from './conversion.container.model';

@Component({
    selector: 'ui-conversion-edit',
    templateUrl: 'edit.html'
})

export class ConversionEditComponent extends PageComponent implements OnInit, OnDestroy{

    public backUrl:string;
    private mode: string;

    private routerSubscription: Subscription;

    public title:string;
    public wait: boolean       = true;
    private waitSubmit: boolean = false;
    private advertiserSessionModel: AdvertiserSessionModel   = new AdvertiserSessionModel();
    private conversion: ConversionContainerModel;
    private errors: any  = {};

    constructor(private conversionService: ConversionService,
                private route: ActivatedRoute,
                private router: Router){
        super();
        if (this.mode) {
            this.initTitle();
        }
    }

    private initTitle(): void {
        if (this.mode === 'add') {
            this.title = '_L10N_(advertiserAccount.conversion.addConversion)';
        } else {
            this.title = '_L10N_(advertiserAccount.conversion.editConversion)';
        }
    }

    ngOnInit(){
        this.routerSubscription   = this.route.url.subscribe(params => {
            if (params[0].path === 'add') {
                this.mode = 'add';
                this.initTitle();
                this.backUrl = `/advertiser/${this.advertiserSessionModel.id}/conversions`;
                this.conversion = new ConversionContainerModel();
                this.conversion.conversion.impWindow = 30;
                this.conversion.conversion.clickWindow = 30;

                this.wait = false;
            } else {
                this.mode = 'edit';
                this.initTitle();
                this.conversionService.getById(+params[0].path)
                    .then(conversion => {
                        this.conversion = conversion;
                        this.backUrl = `/advertiser/${this.advertiserSessionModel.id}/conversions`;
                        this.wait = false;
                });
            }
        });
    }

    ngOnDestroy(){
        if (this.routerSubscription){
            this.routerSubscription.unsubscribe();
        }
    }

    private submitForm(){
        this.waitSubmit = true;
        let adv = new AdvertiserModel();
        adv.id = this.advertiserSessionModel.id;
        this.conversion.conversion.account = adv;

        let promise;
        if (this.mode === 'add') {
            promise = this.conversionService.create(this.conversion);
        } else {
            promise = this.conversionService.update(this.conversion);
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
