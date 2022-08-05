import {Component, Input, Output, EventEmitter, OnInit, OnDestroy} from '@angular/core';
import {GeoService} from '../../../../common/services/geo.service';
import {L10nDistanceUnits, L10nCountries} from '../../../../common/L10n.const';
import {FlightService} from '../../../services/flight.service';
import {FlightGeoAddress} from '../../../models/flight.model';
import {AdvertiserSessionModel} from '../../../../advertiser/models';
import {L10nStatic} from '../../../../shared/static/l10n.static';
import {Subject} from 'rxjs';
import {debounceTime, filter, switchMap, takeUntil, tap} from 'rxjs/operators';

@Component({
  selector: 'ui-flight-geo',
  templateUrl: 'flight-geotarget.component.html',
  styleUrls: ['./flight-geotarget.component.scss']
})
export class FlightGeotargetComponent implements OnInit, OnDestroy {

  _ids_geoChannelIds
  _id_excludedGeoChannelIds;

  @Input()
  set geoChannelIds(ids: number[]) {
    this._ids_geoChannelIds = ids;
    this.init()
  }
  @Input()
  set excludedGeoChannelIds(ids: number[]) {
    this._id_excludedGeoChannelIds = ids;
    this.init()
  }
  // @Input() geoChannelIds;
  // @Input() excludedGeoChannelIds;
  @Input() flightId;

  @Output() geotargetChange: EventEmitter<any> = new EventEmitter();
  @Output() excludedGeotargetChange: EventEmitter<any> = new EventEmitter();
  @Output() geoAddressChange: EventEmitter<any> = new EventEmitter();
  @Output() excludedGeoAddressChange: EventEmitter<any> = new EventEmitter();

  radioVal = true;
  waitGeoData = true;
  waitExcludedGeoData = true;
  waitSearch: boolean;
  waitAddress = false;
  countryCode: string;
  locations: any[] = [];
  excludedLocations: any[] = [];
  locationsError: string = null;
  excludedLocationsError: string = null;
  autocomplete: GeoPlace[] = null;
  addresses: GeoAddress[] = [];
  excludedAddresses: GeoAddress[] = [];
  geoCode = '';
  canChangeAddresses = true;
  foundAddresses: GeoAddress[];
  searchAddress = false;
  selectedAddress: GeoAddress = new FlightGeoAddress();
  popupVisible = false;
  popupOptions;
  radiusError: string;
  destroy$ = new Subject();
  geoSearch$ = new Subject();
  readonly L10nDistanceUnits = L10nDistanceUnits;
  readonly L10nCountries = L10nCountries;

  constructor(private flightService: FlightService,
              private geoService: GeoService) {
    this.countryCode = new AdvertiserSessionModel().countryCode;

    if (this.countryCode !== 'RU') {
      this.canChangeAddresses = false;
    }

    this.popupOptions = {
      title: L10nStatic.translate('flight.blockName.addLocation'),
      btnTitle: L10nStatic.translate('button.add'),
      btnIcon: 'link',
      btnIconDisabled: false
    };
  }

  ngOnInit(): void {
    this.init()
  }
  init(){
    this.loadChannelsList(true);
    this.loadExcludedChannelsList(true);

    this.geoSearch$.pipe(
      takeUntil(this.destroy$),
      debounceTime(500),
      tap(() => {
        this.autocomplete = null;
      }),
      filter(text => !!text),
      tap(() => {
        this.waitSearch = true;
      }),
      switchMap((text: string) => this.geoService.searchLocation(text))
    ).subscribe(res => {
      this.autocomplete = res;
      this.waitSearch = false;
    });
  }
  ngOnDestroy(): void {
    this.destroy$.next(null);
    this.destroy$.unsubscribe();
  }

  loadChannelsList(loadAddresses: boolean): void {
    if (this._ids_geoChannelIds && this._ids_geoChannelIds.length !== 0) {
      this.waitGeoData = true;
      this.geoService.getLocations(this._ids_geoChannelIds)
        .then(list => {
          this.locations = list;

          if (loadAddresses && this.canChangeAddresses) {
            this.geoService.getAddresses(this._ids_geoChannelIds)
              .then(list2 => {
                this.addresses = list2;

                if (this.locations.length + this.addresses.length < this._ids_geoChannelIds.length) {
                  this.locationsError = L10nStatic.translate('flight.blockName.locations.error');
                } else {
                  this.locationsError = null;
                }

                this.waitGeoData = false;
              });
          } else {
            if (this.locations.length + this.addresses.length < this._ids_geoChannelIds.length) {
              this.locationsError = L10nStatic.translate('flight.blockName.locations.error');
            } else {
              this.locationsError = null;
            }
            this.waitGeoData = false;
          }
        });
    } else {
      this.locations = [];
      this.waitGeoData = false;
    }
  }

  loadExcludedChannelsList(loadAddresses: boolean): void {
    if (this._id_excludedGeoChannelIds && this._id_excludedGeoChannelIds.length !== 0) {
      this.waitExcludedGeoData = true;
      this.geoService.getLocations(this._id_excludedGeoChannelIds)
        .then(list => {
          this.excludedLocations = list;

          if (loadAddresses && this.canChangeAddresses) {
            this.geoService.getAddresses(this._id_excludedGeoChannelIds)
              .then(list2 => {
                this.excludedAddresses = list2;

                if (this.excludedLocations.length + this.excludedAddresses.length < this._id_excludedGeoChannelIds.length) {
                  this.excludedLocationsError = L10nStatic.translate('flight.blockName.locations.error');
                } else {
                  this.excludedLocationsError = null;
                }

                this.waitExcludedGeoData = false;
              });
          } else {
            if (this.excludedLocations.length + this.excludedAddresses.length < this._id_excludedGeoChannelIds.length) {
              this.excludedLocationsError = L10nStatic.translate('flight.blockName.locations.error');
            } else {
              this.excludedLocationsError = null;
            }
            this.waitExcludedGeoData = false;
          }
        });
    } else {
      this.excludedLocations = [];
      this.waitExcludedGeoData = false;
    }
  }

  deleteLocation(channel: any): void {
    if (channel.id) {
      this._ids_geoChannelIds = this._ids_geoChannelIds.filter(id => +id !== +channel.id);

      this.loadChannelsList(false);
      this.geotargetChange.emit(this._ids_geoChannelIds);
    }
  }

  excludeLocation(channel: any): void {
    this.deleteLocation(channel);

    if (channel.id) {
      if (!this._id_excludedGeoChannelIds.includes(channel.id)) {
        this._id_excludedGeoChannelIds.push(channel.id);
        this.loadExcludedChannelsList(false);
        this.excludedGeotargetChange.emit(this._id_excludedGeoChannelIds);
      }
    }
  }

  deleteAddress(address: any): void {
    if (address.id) {
      this._ids_geoChannelIds = this._ids_geoChannelIds.filter(id => +id !== +address.id);
      this.geotargetChange.emit(this._ids_geoChannelIds);
    }

    this.addresses = this.addresses.filter(a => a !== address);

    this.geoAddressChange.emit(this.addresses);
  }

  excludeAddress(address: any): void {
    this.deleteAddress(address);

    if (address.id) {
      if (!this._id_excludedGeoChannelIds.includes(address.id)) {
        this._id_excludedGeoChannelIds.push(address.id);
        this.excludedGeotargetChange.emit(this._id_excludedGeoChannelIds);
      }
    }

    this.excludedAddresses.push(address);
    this.excludedGeoAddressChange.emit(this.excludedAddresses);
  }

  deleteExcludedLocation(channel: any): void {
    if (channel.id) {
      this._id_excludedGeoChannelIds = this._id_excludedGeoChannelIds.filter(id => +id !== +channel.id);

      this.loadExcludedChannelsList(false);
      this.excludedGeotargetChange.emit(this._id_excludedGeoChannelIds);
    }
  }

  addLocation(channel: any): void {
    this.deleteExcludedLocation(channel);

    if (channel.id) {
      if (!this._ids_geoChannelIds.includes(channel.id)) {
        this._ids_geoChannelIds.push(channel.id);
        this.loadChannelsList(false);
        this.geotargetChange.emit(this._ids_geoChannelIds);
      }
    }
  }

  deleteExcludedAddress(address: any): void {
    if (address.id) {
      this._id_excludedGeoChannelIds = this._id_excludedGeoChannelIds.filter(id => +id !== +address.id);
      this.excludedGeotargetChange.emit(this._id_excludedGeoChannelIds);
    }

    this.excludedAddresses = this.excludedAddresses.filter(a => a !== address);

    this.excludedGeoAddressChange.emit(this.excludedAddresses);
  }

  addAddress(address: any): void {
    this.deleteExcludedAddress(address);

    if (address.id) {
      if (!this._ids_geoChannelIds.includes(address.id)) {
        this._ids_geoChannelIds.push(address.id);
        this.geotargetChange.emit(this._ids_geoChannelIds);
      }
    }

    this.addresses.push(address);
    this.geoAddressChange.emit(this.addresses);
  }

  findAddress(e: Event): void {
    e.preventDefault();
    e.stopPropagation();

    this.waitAddress = true;
    this.clearAddresses();
    this.geoService.searchAddress(this.geoCode)
      .then(list => {
        this.foundAddresses = list;
        this.searchAddress = true;
        this.waitAddress = false;
      });
  }

  checkAddress(address: GeoAddress): boolean {
    return this.addresses.includes(address);
  }

  clearAddresses(): void {
    this.foundAddresses = [];
    this.searchAddress = false;
  }

  showPopup(address: GeoAddress): void {
    this.selectedAddress = address;
    this.popupVisible = true;
  }

  popupHide(): void {
    this.popupVisible = false;
  }

  popupSave(): void {
    this.radiusError = this.checkRadius();
    if (this.radiusError !== '') {
      return;
    }

    this.addresses.push(this.selectedAddress);
    this.geoAddressChange.emit(this.addresses);

    this.popupVisible = false;
  }

  checkRadius(): string {
    const radius = this.selectedAddress.radius;

    if (!/^-?\d+$/.test(radius.toString()) || radius % 1 > 0) {
      return L10nStatic.translate('flight.error.address.radius.fraction');
    }

    const radiusUnits = this.selectedAddress.radiusUnits;
    switch (radiusUnits) {
      case 'm':
        if (radius < 50 || radius > 50000) {
          return this.getRadiusRangeMessage(50, 50000);
        }
        break;
      case 'km':
        if (radius < 1 || radius > 50) {
          return this.getRadiusRangeMessage(1, 50);
        }
        break;
    }
    return '';
  }

  getRadiusRangeMessage(val1: number, val2: number): string {
    return L10nStatic.translate('flight.error.address.radius.range') + ' ' +
      L10nStatic.translate('flight.error.address.radius.range.from') + ' ' + val1 + ' ' +
      L10nStatic.translate('flight.error.address.radius.range.to') + ' ' + val2;
  }

  addChannel(channel: GeoPlace): void {
    if (!this._ids_geoChannelIds.includes(channel.id)) {
      this._ids_geoChannelIds.push(channel.id);
      this.loadChannelsList(false);
      this.geotargetChange.emit(this._ids_geoChannelIds);
    }
  }

  displayByName(value: any): string {
    return value && value.name ? value.name : '';
  }
}
