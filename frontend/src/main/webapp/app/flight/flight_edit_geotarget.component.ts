import {Component, Input, Output, EventEmitter, OnChanges, ElementRef, ViewChild, OnInit} from '@angular/core';
import { RouterModule }                                                             from '@angular/router';
import { FormsModule }                                                              from '@angular/forms';

import { LoadingComponent }       from '../shared/loading.component';
import { IconComponent }          from '../shared/icon.component';
import { PopupComponent }         from '../shared/popup.component';
import { GeoService }             from '../common/geo.service';
import { CountryCode, GeoPlace, GeoAddress }  from '../common/geotarget';
import { L10nDistanceUnits, L10nCountries } from '../common/L10n.const';
import { AdvertiserSessionModel } from '../advertiser/advertiser_session.model';
import { FlightService }          from './flight.service';
import { FlightGeoAddress }       from './flight.model';


@Component({
    selector: 'ui-flight-edit-geo',
    providers: [
        GeoService
    ],
    templateUrl: 'geotarget.html'
})

export class FlightEditGeotargetComponent implements OnInit{

    @Input() geoChannelIds;
    @Input() excludedGeoChannelIds;
    @Input() flightId;

    @Output() onGeotargetChange: EventEmitter<any> = new EventEmitter();
    @Output() onExcludedGeotargetChange: EventEmitter<any> = new EventEmitter();
    @Output() onGeoAdrressChange: EventEmitter<any> = new EventEmitter();
    @Output() onExcludedGeoAddressChange: EventEmitter<any> = new EventEmitter();
    @ViewChild('textInput') textInputEl: ElementRef;

    public radioVal: boolean = true;
    public waitGeoData: boolean = true;
    public waitExcludedGeoData: boolean = true;
    private waitSearch: boolean;
    private waitAddress: boolean = false;
    public countryCode: string;
    public locations: Array<any> = [];
    public excludedLocations: Array<any> = [];
    public locationsError: string = null;
    public excludedLocationsError: string = null;
    private autocomplete: Array<GeoPlace> = null;
    public addresses: Array<GeoAddress> = [];
    public excludedAddresses: Array<GeoAddress> = [];
    private geoCode: string = '';
    public canChangeAddresses: boolean = true;
    private foundAddresses: Array<GeoAddress>;
    private searchAddress: boolean = false;
    public selectedAddress: GeoAddress = new FlightGeoAddress();
    private inputTimer;
    private keypDelay:number    = 500;
    public popupVisible:boolean = false;
    public popupOptions;
    public radiusError: string;

    public L10nDistanceUnits = L10nDistanceUnits;
    public L10nCountries = L10nCountries;

    constructor(private flightService: FlightService,
                private geoService: GeoService){
        this.countryCode = new AdvertiserSessionModel().countryCode;

        if (this.countryCode != 'RU') {
            this.canChangeAddresses = false;
        }

        this.popupOptions = {
            title:      '_L10N_(flight.blockName.addLocation)',
            btnTitle:   '_L10N_(button.add)',
            btnIcon:    'link',
            btnIconDisabled: false
        };
    }

    ngOnInit(){
        this.loadChannelsList(true);
        this.loadExcludedChannelsList(true);
    }

    private loadChannelsList(loadAddresses: boolean) {
        if (this.geoChannelIds && this.geoChannelIds.length !== 0) {
            this.waitGeoData = true;
            this.geoService.getLocations(this.geoChannelIds)
                .then(list => {
                    this.locations = list;

                    if (loadAddresses && this.canChangeAddresses) {
                        this.geoService.getAddresses(this.geoChannelIds)
                            .then(list => {
                                this.addresses = list;

                                if (this.locations.length + this.addresses.length < this.geoChannelIds.length) {
                                    this.locationsError = '_L10N_(flight.blockName.locations.error)';
                                } else {
                                    this.locationsError = null;
                                }

                                this.waitGeoData = false;
                            });
                    } else {
                        if (this.locations.length + this.addresses.length < this.geoChannelIds.length) {
                            this.locationsError = '_L10N_(flight.blockName.locations.error)';
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

    private loadExcludedChannelsList(loadAddresses: boolean){
        if (this.excludedGeoChannelIds && this.excludedGeoChannelIds.length !== 0){
            this.waitExcludedGeoData = true;
            this.geoService.getLocations(this.excludedGeoChannelIds)
                .then(list => {
                    this.excludedLocations = list;

                    if (loadAddresses && this.canChangeAddresses) {
                        this.geoService.getAddresses(this.excludedGeoChannelIds)
                            .then(list => {
                                this.excludedAddresses = list;

                                if (this.excludedLocations.length + this.excludedAddresses.length < this.excludedGeoChannelIds.length) {
                                    this.excludedLocationsError = '_L10N_(flight.blockName.locations.error)';
                                } else {
                                    this.excludedLocationsError = null;
                                }

                                this.waitExcludedGeoData = false;
                            });
                    } else {
                        if (this.excludedLocations.length + this.excludedAddresses.length < this.excludedGeoChannelIds.length) {
                            this.excludedLocationsError = '_L10N_(flight.blockName.locations.error)';
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

    private deleteLocation(e: any, channel: any){
        e.preventDefault();
        e.stopPropagation();

        if (channel.id){
            this.geoChannelIds = this.geoChannelIds.filter(id => {
                return +id !== +channel.id;
            });

            this.loadChannelsList(false);
            this.onGeotargetChange.emit(this.geoChannelIds);
        }
    }

    private excludeLocation(e: any, channel: any){
        this.deleteLocation(e, channel);

        if (channel.id){
            if (!this.excludedGeoChannelIds.includes(channel.id)){
                this.excludedGeoChannelIds.push(channel.id);
                this.loadExcludedChannelsList(false);
                this.onExcludedGeotargetChange.emit(this.excludedGeoChannelIds);
            }
        }
    }

    private deleteAddress(e: any, address: any){
        e.preventDefault();
        e.stopPropagation();

        if (address.id) {
            this.geoChannelIds = this.geoChannelIds.filter(id => {
                return +id !== +address.id;
            });
            this.onGeotargetChange.emit(this.geoChannelIds);
        }

        this.addresses = this.addresses.filter(a => {
            return a !== address;
        });

        this.onGeoAdrressChange.emit(this.addresses);
    }

    private excludeAddress(e: any, address: any){
        this.deleteAddress(e, address);

        if (address.id) {
            if (!this.excludedGeoChannelIds.includes(address.id)){
                this.excludedGeoChannelIds.push(address.id);
                this.onExcludedGeotargetChange.emit(this.excludedGeoChannelIds);
            }
        }

        this.excludedAddresses.push(address);
        this.onExcludedGeoAddressChange.emit(this.excludedAddresses);
    }

    private deleteExcludedLocation(e: any, channel: any){
        e.preventDefault();
        e.stopPropagation();

        if (channel.id){
            this.excludedGeoChannelIds = this.excludedGeoChannelIds.filter(id => {
                return +id !== +channel.id;
            });

            this.loadExcludedChannelsList(false);
            this.onExcludedGeotargetChange.emit(this.excludedGeoChannelIds);
        }
    }

    private addLocation(e: any, channel: any){
        this.deleteExcludedLocation(e, channel);

        if (channel.id){
            if (!this.geoChannelIds.includes(channel.id)){
                this.geoChannelIds.push(channel.id);
                this.loadChannelsList(false);
                this.onGeotargetChange.emit(this.geoChannelIds);
            }
        }
    }

    private deleteExcludedAddress(e: any, address: any){
        e.preventDefault();
        e.stopPropagation();

        if (address.id) {
            this.excludedGeoChannelIds = this.excludedGeoChannelIds.filter(id => {
                return +id !== +address.id;
            });
            this.onExcludedGeotargetChange.emit(this.excludedGeoChannelIds);
        }

        this.excludedAddresses = this.excludedAddresses.filter(a => {
            return a !== address;
        });

        this.onExcludedGeoAddressChange.emit(this.excludedAddresses);
    }

    private addAddress(e: any, address: any){
        this.deleteExcludedAddress(e, address);

        if (address.id) {
            if (!this.geoChannelIds.includes(address.id)){
                this.geoChannelIds.push(address.id);
                this.onGeotargetChange.emit(this.geoChannelIds);
            }
        }

        this.addresses.push(address);
        this.onGeoAdrressChange.emit(this.addresses);
    }

    private hideAutocomplete(e?: any) {
        this.autocomplete = null;
    }

    private textInputChange(e: any){
        if (this.waitSearch) return;
        if (this.inputTimer) clearTimeout(this.inputTimer);

        this.inputTimer = setTimeout(()=>{
            let text    = this.textInputEl.nativeElement.value;

            this.hideAutocomplete();

            if (text.length){
                this.waitSearch  = true;

                this.geoService.searchLocation(text)
                    .then(found => {
                        this.autocomplete   = found.filter( f => this.geoChannelIds == null || this.geoChannelIds.indexOf(f.id) == -1 );
                        this.waitSearch     = false;

                        setImmediate(() => {
                            this.textInputEl.nativeElement.focus();
                        });
                    });
            }
        }, this.keypDelay);
    }

    private switchType(e: any){
        this.radioVal   = !this.radioVal
    }

    private findAddress(e: any) {
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

    private checkAddress(address: GeoAddress): boolean {
        return this.addresses.includes(address);
    }

    private clearAddresses(): void {
        this.foundAddresses = [];
        this.searchAddress = false;
    }

    private showPopup(e: any, address: GeoAddress){
        e.preventDefault();
        e.stopPropagation();

        this.selectedAddress = address;
        this.popupVisible   = true;
    }

    public popupHide(e: any){
        this.popupVisible   = false;
    }

    public popupSave(e: any){
        this.radiusError = this.checkRadius();
        if (this.radiusError != '') {
            return;
        }

        this.addresses.push(this.selectedAddress);
        this.onGeoAdrressChange.emit(this.addresses);

        this.popupVisible = false;
    }

    private checkRadius(){
        let radius = this.selectedAddress.radius;

        if (!/^-?\d+$/.test(radius.toString()) || radius % 1 > 0) {
            return '_L10N_(flight.error.address.radius.fraction)';
        }

        let radiusUnits = this.selectedAddress.radiusUnits;
        switch(radiusUnits) {
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

    private getRadiusRangeMessage(val1: number, val2: number) {
        return '_L10N_(flight.error.address.radius.range)' + ' ' +
               '_L10N_(flight.error.address.radius.range.from)' + ' ' + val1 + ' ' +
               '_L10N_(flight.error.address.radius.range.to)' + ' ' + val2;
    }

    private addChannel(e: any, channel: GeoPlace){
        e.preventDefault();

        if (!this.geoChannelIds.includes(channel.id)){
            this.geoChannelIds.push(channel.id);
            this.loadChannelsList(false);
            this.onGeotargetChange.emit(this.geoChannelIds);
        }

        this.textInputEl.nativeElement.value    = '';
        this.textInputEl.nativeElement.focus();
        this.hideAutocomplete();
    }
}
