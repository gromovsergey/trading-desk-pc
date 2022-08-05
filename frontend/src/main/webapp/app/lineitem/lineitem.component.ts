import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { Subscription }                 from 'rxjs/Rx';

import { PageComponent }            from '../shared/page.component';
import { LoadingComponent }         from '../shared/loading.component';
import { IconComponent }            from '../shared/icon.component';
import { PanelComponent }           from '../shared/panel.component';
import { DisplayStatusDirective }   from '../shared/display_status.directive';
import { FlightService }            from '../flight/flight.service';
import { FlightChannelsComponent }  from '../flight/flight_channels.component';
import { FlightCreativesComponent } from '../flight/flight_creatives.component';
import { FlightSitesComponent }     from '../flight/flight_sites.component';
import { FlightModel }              from '../flight/flight.model';
import { AgencyService }            from '../agency/agency.service';
import { AgencyModel }              from '../agency/agency.model';
import { AdvertiserModel }          from '../advertiser/advertiser.model';
import { AdvertiserService }        from '../advertiser/advertiser.service';
import { FlightTabsComponent }      from '../flight/flight_tabs.component';
import { FlightLineItemsComponent } from '../flight/flight_lineitems.component';
import { FlightSummaryComponent }   from '../flight/flight_summary.component';
import { ChartComponent }           from '../chart/chart.component';
import { LineItemService }          from './lineitem.service';


@Component({
    selector: 'ui-lineitem',
    templateUrl: 'index.html'
})

export class LineItemComponent extends PageComponent implements OnInit, OnDestroy{

    public title:string;
    private titlePrefix:string;
    private routerSubscription:Subscription;
    private lineItem;
    private lineItems: Array<any>;
    private flight:FlightModel;
    private agency:AgencyModel;
    private advertiser:AdvertiserModel;
    public wait: boolean     = true;
    private canCreateLineItem: boolean;
    private canEditLineItem: boolean;


    constructor(private lineItemService:LineItemService,
                private flightService:FlightService,
                private advertiserService: AdvertiserService,
                private agencyService: AgencyService,
                private route: ActivatedRoute){
        super();

        this.title = '_L10N_(lineItem.object)';
        this.titlePrefix = this.title + ': ';
    }

    ngOnInit(){
        this.routerSubscription = this.route.params.subscribe(params => {
            this.lineItemService
                .getById(+params['id'])
                .then(res  => {
                    this.lineItem   = res.lineItemsView.pop();
                    this.title      = this.titlePrefix + this.lineItem.name;
                    return Promise.all([
                        this.flightService.getById(this.lineItem.flightId),
                        this.lineItemService.isAllowedLocal(this.lineItem.flightId, 'flight.createLineItem'),
                        this.lineItemService.isAllowedLocal(this.lineItem.id, 'flight.updateLineItem'),
                    ]);
                })
                .then(res => {
                    this.flight = res[0];
                    this.canCreateLineItem  = Boolean(res[1]);
                    this.canEditLineItem    = Boolean(res[2]);

                    return this.advertiserService.getById(this.flight.accountId);
                })
                .then(advertiser    => {
                    this.advertiser = advertiser;

                    if (this.advertiser.agencyId > 0) {
                        return this.agencyService.getById(this.advertiser.agencyId);
                    } else {
                        return Promise.resolve(null); // in case of direct adv agency is null
                    }
                })
                .then(agency    => {
                    this.agency = agency;
                    this.wait = false;
                });
        });
    }

    ngOnDestroy(){
        if (this.routerSubscription){
            this.routerSubscription.unsubscribe();
        }
    }

    private lineItemsOnLoad(e){
        this.lineItems  = e;
    }

    public onStatusChange(): void {
        this.wait = true;
        Promise.all([
            this.lineItemService.getById(this.lineItem.id),
            this.flightService.getById(this.flight.id)
        ])
        .then(res  => {
            this.lineItem = res[0].lineItemsView.pop();
            this.flight = res[1];
            this.wait = false;
        });
    }
}
