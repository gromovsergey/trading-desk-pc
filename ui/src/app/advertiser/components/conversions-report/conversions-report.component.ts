import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AgencyService} from '../../../agency/services/agency.service';
import {FileService} from '../../../shared/services/file.service';
import {ConversionsReportParametersModel, ReportMetaModel} from '../../../report/models/report.model';
import {AdvertiserService} from '../../services/advertiser.service';
import {FlightService} from '../../../flight/services/flight.service';
import {ConversionsReportService} from '../../services/conversions_report.service';
import {LineItemService} from '../../../lineitem/services/lineitem.service';
import {ConversionService} from '../../../conversion/services/conversion.service';
import {AdvertiserComponent} from '../advertiser/advertiser.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'ui-conversions-report',
  templateUrl: './conversions-report.component.html',
  styleUrls: ['./conversions-report.component.scss']
})
export class ConversionsReportComponent extends AdvertiserComponent implements OnInit {

  title: string;
  titlePrefix: string;
  meta: ReportMetaModel;
  reportParameters: ConversionsReportParameters;
  flightId: number;
  flights: any[];
  lineItemRadio = false;
  lineItems: any[];
  lineItemsAvailable = [];
  lineItemsSelected = [];
  conversionRadio = false;
  conversions: any[];
  conversionsAvailable = [];
  conversionsSelected = [];

  constructor(public reportService: ConversionsReportService,
              protected advertiserService: AdvertiserService,
              protected flightService: FlightService,
              protected lineItemService: LineItemService,
              protected conversionService: ConversionService,
              protected agencyService: AgencyService,
              protected fileService: FileService,
              protected route: ActivatedRoute,
              protected dialog: MatDialog) {

    super(advertiserService, agencyService, fileService, route, dialog);
  }

  ngOnInit(): void {
    this.wait = true;
    super.ngOnInit();

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

  onFlightChange(flightId: number): void {
    this.lineItems = null;
    this.resetLineItems();

    this.reportParameters.flightIds = [];
    if (flightId > 0) {
      this.reportParameters.flightIds[0] = flightId;
      this.flightId = flightId;
      this.initLineItems();
    } else {
      this.reportParameters.flightIds = null;
      this.flightId = null;
    }
  }

  switchLineItemType(): void {
    this.lineItemRadio = !this.lineItemRadio;
    if (!this.lineItemRadio) {
      this.resetLineItems();
    } else {
      this.initLineItems();
    }
  }

  initLineItems(): any {
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

  initLineItemsOptiontransfer(): void {
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

  resetLineItems(): void {
    this.reportParameters.lineItemIds = null;
    this.lineItemsAvailable = [];
    this.lineItemsSelected = [];
  }

  onLineItemsChange(e: any): void {
    const ids = [];
    for (const v of e) {
      ids.push(v.id);
    }
    this.reportParameters.lineItemIds = ids;
  }

  switchConversionType(): any {
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

  initConversionsOptiontransfer(): void {
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

  onConversionsChange(e: any): void {
    const ids = [];
    for (const v of e) {
      ids.push(v.id);
    }
    this.reportParameters.conversionIds = ids;
  }

  sort(a, b): number {
    if (a.name === b.name) {
      return 0;
    }
    return (a.name > b.name) ? 1 : -1;
  }
}

