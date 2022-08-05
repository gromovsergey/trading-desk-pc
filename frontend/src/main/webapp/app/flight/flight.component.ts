import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute }               from '@angular/router';
import { Subscription }                 from 'rxjs/Rx';

import { PageComponent }            from '../shared/page.component';
import { AdvertiserService }        from '../advertiser/advertiser.service';
import { AdvertiserModel }          from '../advertiser/advertiser.model';
import { AgencyService }            from '../agency/agency.service';
import { AgencyModel }              from '../agency/agency.model';
import { FlightModel }              from './flight.model';
import { FlightService }            from './flight.service';
import {LineItemService} from "../lineitem/lineitem.service";

@Component({
    selector: 'ui-flight',
    templateUrl: 'index.html'
})

export class FlightComponent extends PageComponent implements OnInit, OnDestroy{

    public title:string;

    private titlePrefix: string;
    private flight:FlightModel;
    private agency:AgencyModel;
    private advertiser:AdvertiserModel;
    private routerSubscription:Subscription;
    public wait: boolean   = true;
    public specialChannelId: number = null;
    public waitSpecialChannelLoad: boolean = false;
    private lineItems: Array<any>;
    private scanIo: boolean = false;
    private canEditFlight: boolean;
    private canChangeStatusFlight: boolean;
    private canCreateLineItem: boolean;

    constructor(private flightService: FlightService,
                private lineItemService: LineItemService,
                private advertiserService: AdvertiserService,
                private agencyService: AgencyService,
                private route: ActivatedRoute){
        super();

        this.title = '_L10N_(flight.object)';
        this.titlePrefix = this.title + ': ';
    }

    ngOnInit(){
        this.routerSubscription = this.route.params.subscribe(params => {
            this.loadFlightData(+params['id']);
        });
    }

    ngOnDestroy(){
        if (this.routerSubscription){
            this.routerSubscription.unsubscribe();
        }
    }

    private loadFlightData(flightId: number){
        this.flightService.getById(flightId)
            .then(flight => {
                this.flight = flight;
                this.title = this.titlePrefix + flight.name;

                return Promise.all([
                    this.advertiserService.getById(this.flight.accountId),
                    this.flightService.isAllowedLocal(flightId, 'flight.update'),
                    this.flightService.isAllowedLocal(flightId, 'flight.changeStatus'),
                    this.flightService.isAllowedLocal(flightId, 'flight.createLineItem'),
                ]);
            })
            .then(res => {
                this.advertiser             = res[0];
                this.canEditFlight          = Boolean(res[1]);
                this.canChangeStatusFlight  = Boolean(res[2]);
                this.canCreateLineItem      = Boolean(res[3]);

                if (this.advertiser.agencyId){
                    this.agencyService.getById(this.advertiser.agencyId).then(agency => {
                        this.agency = agency;
                        this.wait = false;
                    });
                } else {
                    this.wait   = false;
                }
            });
    }

    public lineItemsOnLoad(e){
        this.lineItems = e;
        this.refreshSpecialChannelInfo();
    }

    private refreshSpecialChannelInfo(): void {
        if (this.lineItems.length == 1) {
            this.waitSpecialChannelLoad = true;
            this.lineItemService.getById(this.lineItems[0].id)
            .then(res => {
                this.specialChannelId = res.lineItemsView[0].specialChannelId;
                this.waitSpecialChannelLoad = false;
            });
        }
    }

    public refreshStatus(lineItems?: any): void {
        this.wait = true;
        if (lineItems){
            this.lineItems = lineItems;
            this.refreshSpecialChannelInfo();
        }
        Promise.all([
                this.flightService.getById(this.flight.id),
                this.flightService.isAllowedLocal(this.flight.id, 'flight.changeStatus')
            ])
            .then(res => {
                this.flight = res[0];
                this.canChangeStatusFlight = Boolean(res[1]);

                this.wait = false;
            });
    }

    private showScanIo(e: any){
        this.scanIo = true;
    }

    private onScanIoClose(e: any){
        this.scanIo = false;
    }
}
