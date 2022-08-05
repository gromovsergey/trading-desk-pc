import { Component, OnInit }   from '@angular/core';
import { CurrencyPipe }        from '@angular/common';
import { ActivatedRoute }      from '@angular/router';

import { AgencyService }       from '../agency/agency.service';
import { FlightService }       from '../flight/flight.service';

import { AdvertiserService }   from './advertiser.service';
import { AdvertiserComponent } from './advertiser.component';
import {FileService}           from "../shared/file.service";
import {DateRange}             from "../shared/date_range";
import {DateRangeModel}        from "../shared/date_range.model";


@Component({
    selector: 'ui-advertiser-flights',
    templateUrl: 'flights.html'
})

export class AdvertiserFlightsComponent extends AdvertiserComponent implements OnInit{

    private flightList: Array<any>;
    private canCreateFlight: boolean;
    public _wait: boolean;

    private dateRange: DateRange = new DateRangeModel();

    protected titlePrefix: string;


    constructor(protected advertiserService: AdvertiserService,
                protected agencyService: AgencyService,
                protected fileService: FileService,
                protected route: ActivatedRoute,
                private flightService: FlightService){
        super(advertiserService, agencyService, fileService, route);
    }

    protected initResources(): void {
        this.titlePrefix = '_L10N_(advertiserAccount.accountFlights)' + ': ';
    }

    ngOnInit(){
        this._wait   = true;
        this.onInit();

        this.promise.then(data => {
            return this.flightService.isAllowedLocal(this.advertiser.id, 'flight.create');
        }).then(res => {
            this.canCreateFlight = res;
            this._wait = false;
        });
    }

    public loadFlights(e: any) {
        this.flightService.getStatListByAdvertiserId(this.advertiser.id, this.dateRange.dateStart, this.dateRange.dateEnd)
            .then(flightList => {
                this.flightList = flightList;
            });
    }
}
