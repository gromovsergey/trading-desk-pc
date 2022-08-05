import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AgencyService} from '../../../agency/services/agency.service';
import {FileService} from '../../../shared/services/file.service';
import {AdvertiserComponent} from '../advertiser/advertiser.component';
import {AdvertiserReportParametersModel, ReportMetaModel} from '../../../report/models/report.model';
import {AdvertiserService} from '../../services/advertiser.service';
import {AdvertiserReportService} from '../../services/advertiser_report.service';
import {FlightService} from '../../../flight/services/flight.service';
import {ArrayHelperStatic} from '../../../shared/static/array-helper.static';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'ui-advertiser-report',
  templateUrl: './advertiser-report.component.html',
  styleUrls: ['./advertiser-report.component.scss']
})
export class AdvertiserReportComponent extends AdvertiserComponent implements OnInit {

  title: string;
  titlePrefix: string;
  meta: ReportMetaModel;
  reportParameters: AdvertiserReportParameters;
  radioVal = false;
  flights: any[];
  flightsAvailable = [];
  flightsSelected = [];
  sort = ArrayHelperStatic.sortByKey.bind(this, 'name');

  constructor(public reportService: AdvertiserReportService,
              protected advertiserService: AdvertiserService,
              protected flightService: FlightService,
              protected agencyService: AgencyService,
              protected fileService: FileService,
              protected route: ActivatedRoute,
              protected dialog: MatDialog) {

    super(advertiserService, agencyService, fileService, route, dialog);
  }

  ngOnInit(): void {
    super.ngOnInit();
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

  loadFlights(): Promise<any> {
    return this.flightService.getListByAdvertiserId(this.advertiser.id)
      .then(list => {
        this.flights = list;
        this.initOptiontransfer();
      });
  }

  switchType(): void {
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

  initOptiontransfer(): void {
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

  filghtIdsChange(e: any): void {
    const ids = [];
    for (const v of e) {
      ids.push(v.id);
    }
    this.reportParameters.flightIds = ids;
  }
}
