import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {L10nTimeZones} from '../../../common/L10n.const';
import {AdvertiserService} from '../../../advertiser/services/advertiser.service';
import {LineItemService} from '../../../lineitem/services/lineitem.service';
import {FlightService} from '../../services/flight.service';
import {FlightModel, FrequencyCaps} from '../../models/flight.model';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {ErrorHelperStatic} from '../../../shared/static/error-helper.static';
import {ISite} from "../../../advertiser/models/site";


@Component({
  selector: 'ui-flight-edit',
  templateUrl: 'flight-edit.component.html',
  styleUrls: ['./flight-edit.component.scss']
})
export class FlightEditComponent implements OnInit, OnDestroy {

  @Input() isLineItem = false;

  routerSubscription: Subscription;
  mode: string;
  wait = true;
  waitSubmit = false;
  flight = new FlightModel();
  flightDefaults: Flight = new FlightModel();
  spentBudget = 0;
  backUrl: string;
  advertiserSession = new AdvertiserSessionModel();
  flightSessionId: number;
  resetableFields: string[] = null;
  errors: any = {};
  strings;
  L10nTimeZones = L10nTimeZones;

  constructor(private flightService: FlightService,
              private lineItemService: LineItemService,
              private advertiserService: AdvertiserService,
              private route: ActivatedRoute,
              private router: Router) {


    this.strings = {
      lineitem: {
        add_title: L10nStatic.translate('lineItem.button.add'),
        edit_title: L10nStatic.translate('lineItem.button.edit'),
        edit_back_url: (id: number) => `/lineitem/${id}`,
        add_back_url: (id: number) => `/flight/${id}`,
      },
      flight: {
        add_title: L10nStatic.translate('flight.button.add'),
        edit_title: L10nStatic.translate('flight.button.edit'),
        edit_back_url: (id: number) => `/flight/${id}`,
        add_back_url: (id: number) => `/advertiser/${id}/flights`,
      },
    };
  }

  ngOnInit(): void {
    this.routerSubscription = this.route.url.subscribe(params => {
      if (params.length > 1 && params[1].path === 'lineitem') {
        this.isLineItem = true;
      }

      const strings = this.isLineItem ? this.strings.lineitem : this.strings.flight;
      const service = this.isLineItem ? this.lineItemService : this.flightService;

      if (!this.isLineItem && params[0].path === 'add'
        || this.isLineItem && params.length > 2 && params[2].path === 'add') {
        this.flightSessionId = +params[0];
        this.mode = 'add';
        this.backUrl = strings.add_back_url(this.isLineItem ? this.flightSessionId : this.advertiserSession.id);
        this.resetableFields = [];

        if (this.isLineItem) {
          this.flightService.getById(this.flightSessionId)
            .then(flightDefaults => {
              delete flightDefaults.id;
              delete flightDefaults.accountId;
              delete flightDefaults.name;
              delete flightDefaults.version;
              delete flightDefaults.version2;
              delete flightDefaults.whiteListId;
              delete flightDefaults.blackListId;
              if (flightDefaults.frequencyCap) {
                delete flightDefaults.frequencyCap.id;
                delete flightDefaults.frequencyCap.version;
              } else {
                flightDefaults.frequencyCap = new FrequencyCaps();
              }
              this.flight = Object.assign({}, flightDefaults);
              this.wait = false;
            });
        } else {
          this.wait = false;
        }
      } else {
        this.mode = 'edit';

        const entityId = +params[0].path;
        const promise = Promise.all([
          service.getById(entityId),
          service.getStatsById(entityId)
        ]);

        promise.then(res => {

          let entity;

          if (this.isLineItem) {
            entity = res[0].lineItemsView.pop();
            this.flightDefaults = res[0].flightView;
            this.resetableFields = entity.resetAwareProps;
          } else {
            entity = res[0];
          }
          this.spentBudget = res[1].spentBudget;

          if (entity.frequencyCap === null) {
            entity.frequencyCap = new FrequencyCaps();
          }

          this.flight = entity;
          this.backUrl = strings.edit_back_url(entity.id);

          if (new AdvertiserSessionModel().hasData()) {
            this.wait = false;
          } else {
            this.advertiserService.getById(this.flight.accountId).toPromise()
              .then(advertiser => {
                new AdvertiserSessionModel().data = advertiser;
                this.wait = false;
              });
          }
        });
      }
    });
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  submitForm(): void {
    let promise;
    const entity = Object.assign({}, this.flight);
    const service = this.isLineItem ? this.lineItemService : this.flightService;

    this.waitSubmit = true;

    entity.emptyProps = [];

    Object.keys(this.flight).forEach(v => {
      if (!['resetAwareProps', 'propsWithFlightValues'].includes(v)) {
        if (this.flight[v] === '' || this.flight[v] === null || (Array.isArray(this.flight[v]) && this.flight[v].length === 0)) {
          entity.emptyProps.push(v);
        }
      }
    });


    if (this.mode === 'add') {
      if (this.isLineItem) {
        entity.flightId = this.flightSessionId;
      } else {
        entity.accountId = this.advertiserSession.id;
      }

      promise = service.save(entity);
    } else {
      promise = service.update(entity);
    }

    promise
      .then(id => {
        if (this.mode === 'add') {
          const strings = this.isLineItem ? this.strings.lineitem : this.strings.flight;
          this.backUrl = strings.edit_back_url(id);
        }

        this.waitSubmit = false;
        this.router.navigateByUrl(this.backUrl);
      })
      .catch(err => {

        if (err.status === 412) {
          this.errors = ErrorHelperStatic.matchErrors(err);
          this.waitSubmit = false;
        }
      });
  }

  geoChange(e: any): void {
    this.flight.geoChannelIds = e;
  }

  excludedGeoChange(e: any): void {
    this.flight.excludedGeoChannelIds = e;
  }

  geoAddressesChange(e: any): void {
    this.flight.addresses = e;
  }

  excludedGeoAddressesChange(e: any): void {
    this.flight.excludedAddresses = e;
  }

  onDScheduleChange(e: any): void {
    this.flight.schedules = e;
  }

  convTrackingChange(e: any): void {
    this.flight.conversionIds = e;
  }

  siteIdsChange(list: IdName[]): void {
    this.flight.siteIds = list.map(item => item.id);
  }

  devicesChange(e: any): void {
    this.flight.deviceChannelIds = e;
  }

  resetField(e: any, fieldName: string): void {
    if (e) {
      e.preventDefault();
      e.stopPropagation();
    }

    if (!this.isLineItem) {
      return;
    }

    if (this.flight.propsWithFlightValues === undefined) {
      this.flight.propsWithFlightValues = [];
    }

    const fieldNames: Array<string> = [fieldName];
    switch (fieldName) {
      case 'dateEnd':
        if (this.flightDefaults.dateEnd === null && this.flight.deliveryPacing === 'D') {
          fieldNames.push('deliveryPacing', 'dailyBudget');
        }
        break;
      case 'deliveryPacing':
        fieldNames.push('dailyBudget');
        if (this.flightDefaults.deliveryPacing === 'D' && this.flight.dateEnd === null) {
          fieldNames.push('dateEnd');
        }
        break;
      case 'rateType':
        fieldNames.push('rateValue');
        break;
      case 'geoChannelIds':
        fieldNames.push('geoChannelIds', 'excludedGeoChannelIds');
        break;
    }

    const obj: any = {};
    fieldNames.forEach(f => {
      obj[f] = this.flightDefaults[f];
      this.flight.propsWithFlightValues.push(f);
    });
    if (fieldName === 'frequencyCap' && obj.frequencyCap !== null) {
      delete obj.frequencyCap.id;
    }
    this.flight = Object.assign({}, this.flight, obj);
    this.resetableFields = this.resetableFields.filter(f => !fieldNames.includes(f));
  }
}
