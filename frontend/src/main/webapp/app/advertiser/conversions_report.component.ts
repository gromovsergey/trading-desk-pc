import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {AgencyService} from '../agency/agency.service';

import {FileService} from "../shared/file.service";
import {AdvertiserComponent} from "./advertiser.component";
import {ConversionsReportParametersModel, ReportMetaModel} from "../report/report.model";
import {ConversionsReportParameters} from "../report/report";
import {AdvertiserService} from "./advertiser.service";
import {FlightService} from "../flight/flight.service";
import {ConversionsReportService} from "./conversions_report.service";
import {LineItemService} from "../lineitem/lineitem.service";
import {ConversionService} from "../conversion/conversion.service";

@Component({
    selector: 'ui-conversions-report',
    templateUrl: 'conversions_report.html'
})

export class ConversionsReportComponent extends AdvertiserComponent implements OnInit {

    public title: string;
    protected titlePrefix: string;

    public meta: ReportMetaModel;
    public reportParameters: ConversionsReportParameters;

    public flightId: number;
    public flights: Array<any>;

    public lineItemRadio: boolean = false;
    public lineItems: Array<any>;
    public lineItemsAvailable: Array<any> = [];
    public lineItemsSelected: Array<any> = [];

    public conversionRadio: boolean = false;
    public conversions: Array<any>;
    public conversionsAvailable: Array<any> = [];
    public conversionsSelected: Array<any> = [];

    private sort = function (a, b) {
        if (a.name === b.name) return 0;
        return (a.name > b.name) ? 1 : -1;
    };

    constructor(private reportService: ConversionsReportService,
                protected advertiserService: AdvertiserService,
                protected flightService: FlightService,
                protected lineItemService: LineItemService,
                protected conversionService: ConversionService,
                protected agencyService: AgencyService,
                protected fileService: FileService,
                protected route: ActivatedRoute) {

        super(advertiserService, agencyService, fileService, route);
    }

    protected initResources(): void {
        this.title = '_L10N_(report.conversionsReport)';
        this.titlePrefix = '_L10N_(report.conversionsReport)' + ': ';
    }

    ngOnInit() {
        this.wait = true;
        this.onInit();

        this.promise = this.promise.then(() => {
            this.reportParameters = new ConversionsReportParametersModel();
            this.reportParameters.accountId = this.advertiser.id;
            this.wait = true;

            return Promise.all([
                this.reportService.getReportMeta(this.reportParameters),
                this.flightService.getListByAdvertiserId(this.advertiser.id)
            ]).then(res => {
                this.meta = res[0];
                this.flights = res[1];
                this.wait = false;
            });
        });
    }

    public onFlightChange(event: any) {
        this.lineItems = null;
        this.resetLineItems();

        this.reportParameters.flightIds = [];
        if (event.target.value > 0) {
            this.reportParameters.flightIds[0] = event.target.value;
            this.flightId = event.target.value;
            this.initLineItems();
        } else {
            this.reportParameters.flightIds = null;
            this.flightId = null;
        }
    }

    public switchLineItemType(e: any) {
        this.lineItemRadio = !this.lineItemRadio;
        if (!this.lineItemRadio) {
            this.resetLineItems();
        } else {
            this.initLineItems();
        }
    }

    private initLineItems() {
        this.reportParameters.lineItemIds = [];
        if (!this.lineItems) {
            return this.lineItemService.getListByFlightId(this.flightId, null, null)
                .then(list => {
                    this.lineItems = list;
                    this.initLineItemsOptiontransfer();
                });
        } else {
            this.initLineItemsOptiontransfer();
        }
    }

    private initLineItemsOptiontransfer() {
        this.lineItemsAvailable = [];
        this.lineItemsSelected = [];

        this.lineItems.forEach(v => {
            if (this.reportParameters.lineItemIds.indexOf(v.id) !== -1) {
                this.lineItemsSelected.push({
                    id: v.id,
                    name: v.name
                });
            } else {
                this.lineItemsAvailable.push({
                    id: v.id,
                    name: v.name
                });
            }
        });
    }

    private resetLineItems() {
        this.reportParameters.lineItemIds = null;
        this.lineItemsAvailable = [];
        this.lineItemsSelected = [];
    }

    public onLineItemsChange(e: any) {
        let ids = [];
        for (let v of e) {
            ids.push(v.id);
        }
        this.reportParameters.lineItemIds = ids;
    }

    public switchConversionType(e: any) {
        this.conversionRadio = !this.conversionRadio;
        if (!this.conversionRadio) {
            this.reportParameters.conversionIds = null;
            this.conversionsAvailable = [];
            this.conversionsSelected = [];
        } else {
            this.reportParameters.conversionIds = [];
            if (!this.conversions) {
                return this.conversionService.getListByAdvertiserId(this.advertiser.id)
                    .then(list => {
                        this.conversions = list;
                        this.initConversionsOptiontransfer();
                    });
            } else {
                this.initConversionsOptiontransfer();
            }
        }
    }

    private initConversionsOptiontransfer() {
        this.conversionsAvailable = [];
        this.conversionsSelected = [];

        this.conversions.forEach(v => {
            if (this.reportParameters.conversionIds.indexOf(v.id) !== -1) {
                this.conversionsSelected.push({
                    id: v.conversion.id,
                    name: v.conversion.name
                });
            } else {
                this.conversionsAvailable.push({
                    id: v.conversion.id,
                    name: v.conversion.name
                });
            }
        });
    }

    public onConversionsChange(e: any) {
        let ids = [];
        for (let v of e) {
            ids.push(v.id);
        }
        this.reportParameters.conversionIds = ids;
    }
}

