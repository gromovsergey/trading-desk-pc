import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { Subscription }                 from 'rxjs/Rx';

import { LoadingComponent }       from '../shared/loading.component';
import { IconComponent }          from '../shared/icon.component';
import { DisplayStatusDirective } from '../shared/display_status.directive';
import { AdvertiserComponent }    from '../advertiser/advertiser.component';
import { AdvertiserService }      from '../advertiser/advertiser.service';
import { AgencyService }          from '../agency/agency.service';
import { CreativeService }        from './creative.service';
import { Creative }               from './creative';
import {FileService} from "../shared/file.service";

@Component({
    selector: 'ui-creative',
    templateUrl: 'index.html'
})

export class CreativeComponent extends AdvertiserComponent implements OnInit, OnDestroy {

    public title: string;

    public creative: Creative;
    private creativeRouterSubscription:Subscription;


    constructor(protected advertiserService: AdvertiserService,
                protected agencyService: AgencyService,
                protected fileService: FileService,
                protected route: ActivatedRoute,
                private creativeService: CreativeService){
        super(advertiserService, agencyService, fileService, route);
    }

    protected initResources(): void {
        this.title = '_L10N_(creative.object)';
    }

    ngOnInit() {
        this.onInit();

        this.creativeRouterSubscription   = this.route.params.subscribe(params => {
            this.promise = this.creativeService
                .getCreative(+params['creativeId'])
                .then(creative => {
                    this.creative   = creative;
                    this.title      = 'Creative: '+creative.name;
                    this.wait       = false;
                });
        });
    }

    ngOnDestroy(){
        if (this.creativeRouterSubscription){
            this.creativeRouterSubscription.unsubscribe();
        }
    }
}
