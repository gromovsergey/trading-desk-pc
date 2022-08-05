import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {AgencyService} from '../agency/agency.service';

import {FileService} from "../shared/file.service";
import {AdvertiserComponent} from "./advertiser.component";
import {AdvertiserReportParametersModel, ReportMetaModel} from "../report/report.model";
import {AdvertiserReportParameters} from "../report/report";
import {AdvertiserService} from "./advertiser.service";
import {AdvertiserReportService} from "./advertiser_report.service";
import {FlightService} from "../flight/flight.service";

@Component({
    selector: 'ui-advertiser-report',
    templateUrl: 'advertiser_report.html'
})

export class AdvertiserReportComponent extends AdvertiserComponent implements OnInit {

    public title: string;
    protected titlePrefix: string;

    private meta: ReportMetaModel;
    private reportParameters: AdvertiserReportParameters;

    public radioVal: boolean = false;
    private flights: Array<any>;
    private flightsAvailable: Array<any> = [];
    private flightsSelected: Array<any> = [];

    private sort = function (a, b) {
        if (a.name === b.name) return 0;
        return (a.name > b.name) ? 1 : -1;
    };

    constructor(private reportService: AdvertiserReportService,
                protected advertiserService: AdvertiserService,
                protected flightService: FlightService,
                protected agencyService: AgencyService,
                protected fileService: FileService,
                protected route: ActivatedRoute) {

        super(advertiserService, agencyService, fileService, route);
    }

    protected initResources(): void {
        this.title = '_L10N_(report.report)';
        this.titlePrefix = '_L10N_(report.report)' + ': ';
    }

    ngOnInit() {
        this.onInit();
        this.promise = this.promise.then(() => {
            this.reportParameters = new AdvertiserReportParametersModel();
            this.reportParameters.accountId = this.advertiser.id;
            this.wait = true;
            return this.reportService.getReportMeta(this.reportParameters);
        }).then(meta => {
            this.meta = meta;
            this.wait = false;
        });
    }

    private loadFlights(): Promise<any> {
        return this.flightService.getListByAdvertiserId(this.advertiser.id)
            .then(list => {
                this.flights = list;
                this.initOptiontransfer();
            });
    }

    public switchType(e: any) {
        this.radioVal = !this.radioVal;
        if (!this.radioVal) {
            this.reportParameters.flightIds = null;
        } else {
            this.reportParameters.flightIds = [];
            if (!this.flights) {
                this.loadFlights();
            } else {
                this.initOptiontransfer();
            }
        }
    }

    private initOptiontransfer() {
        this.flightsAvailable = [];
        this.flightsSelected = [];

        this.flights.forEach(v => {
            if (this.reportParameters.flightIds.indexOf(v.id) !== -1) {
                this.flightsSelected.push({
                    id: v.id,
                    name: v.name
                });
            } else {
                this.flightsAvailable.push({
                    id: v.id,
                    name: v.name
                });
            }
        });
    }

    private filghtIdsChange(e: any) {
        let ids = [];
        for (let v of e) {
            ids.push(v.id);
        }
        this.reportParameters.flightIds = ids;
    }
}
