import { Component, Input, OnDestroy, OnInit }  from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription }                         from 'rxjs/Rx';

import { PageComponent }                      from '../shared/page.component';
import {jQuery, dateFormatShort, moment} from '../common/common.const';
import { L10nTimeZones }                          from '../common/L10n.const';
import { AdvertiserService }                  from '../advertiser/advertiser.service';
import { AdvertiserSessionModel }             from '../advertiser/advertiser_session.model';
import { LineItemService }                    from '../lineitem/lineitem.service';
import { FlightService }                      from './flight.service';
import { FlightModel, FrequencyCaps }         from './flight.model';
import { Flight }                             from './flight';


@Component({
    selector: 'ui-flight-edit',
    templateUrl: 'edit.html'
})

export class FlightEditComponent extends PageComponent implements OnInit, OnDestroy{

    @Input() isLineItem: boolean = false;

    public title:string;

    private routerSubscription: Subscription;
    private mode: string;
    public wait: boolean       = true;
    private waitSubmit: boolean = false;
    private flight:Flight       = new FlightModel();
    private flightDefaults:Flight   = new FlightModel();
    public spentBudget: number = 0;
    private backUrl: string;
    private advertiserSession: AdvertiserSessionModel   = new AdvertiserSessionModel();
    private flightSessionId: number;
    private resetableFields: Array<string>  = null;

    private errors: any  = {};
    private strings;

    public L10nTimeZones = L10nTimeZones;

    constructor(private flightService: FlightService,
                private lineItemService: LineItemService,
                private advertiserService: AdvertiserService,
                private route: ActivatedRoute,
                private router: Router){
        super();

        this.strings = {
            lineitem: {
                add_title:  '_L10N_(lineItem.button.add)',
                edit_title:  '_L10N_(lineItem.button.edit)',
                edit_back_url: (id: number) => {
                    return `/lineitem/${id}`;
                },
                add_back_url: (id: number) => {
                    return `/flight/${id}`;
                },
            },
            flight: {
                add_title:  '_L10N_(flight.button.add)',
                edit_title:  '_L10N_(flight.button.edit)',
                edit_back_url: (id: number) => {
                    return `/flight/${id}`;
                },
                add_back_url: (id: number) => {
                    return `/advertiser/${id}/flights`;
                },
            },
        };
    }

    ngOnInit(){
        this.routerSubscription   = this.route.url.subscribe(params => {
            if (params.length > 1 && params[1].path === 'lineitem') {
                this.isLineItem = true;
            }

            let strings     = this.isLineItem ? this.strings.lineitem : this.strings.flight,
                service     = this.isLineItem ? this.lineItemService : this.flightService;

            if (!this.isLineItem && params[0].path === 'add'
                    || this.isLineItem &&  params.length > 2 && params[2].path === 'add') {
                this.flightSessionId = +params[0];
                this.mode = 'add';
                this.title = strings.add_title;
                this.backUrl = strings.add_back_url(this.isLineItem ? this.flightSessionId : this.advertiserSession.id);
                this.resetableFields = [];

                if (this.isLineItem){
                    this.flightService.getById(this.flightSessionId)
                        .then(flightDefaults => {
                            delete flightDefaults.id;
                            delete flightDefaults.accountId;
                            delete flightDefaults.name;
                            delete flightDefaults.version;
                            delete flightDefaults.version2;
                            delete flightDefaults['whiteListId'];
                            delete flightDefaults['blackListId'];
                            if (flightDefaults.frequencyCap){
                                delete flightDefaults.frequencyCap.id;
                                delete flightDefaults.frequencyCap.version;
                            } else {
                                flightDefaults.frequencyCap = new FrequencyCaps();
                            }
                            this.flight = Object.assign({}, flightDefaults);
                            this.wait   = false;
                        });
                } else {
                    this.wait       = false;
                }
            } else {
                this.mode   = 'edit';
                this.title  = strings.edit_title;

                let entityId = +params[0].path;
                let promise = Promise.all([
                    service.getById(entityId),
                    service.getStatsById(entityId)
                ]);

                promise.then(res => {

                    let entity;

                    if (this.isLineItem){
                        entity  = res[0].lineItemsView.pop();
                        this.flightDefaults = res[0].flightView;
                        this.resetableFields    = entity.resetAwareProps;
                    } else {
                        entity  = res[0];
                    }

                    this.spentBudget = res[1].spentBudget;

                    if (entity.frequencyCap === null) {
                        entity.frequencyCap = new FrequencyCaps();
                    }

                    this.flight     = entity;
                    this.backUrl    = strings.edit_back_url(entity.id);

                    if (new AdvertiserSessionModel().hasData()){
                        this.wait       = false;
                    } else {
                        this.advertiserService.getById(this.flight.accountId)
                            .then(advertiser => {
                                new AdvertiserSessionModel().data   = advertiser;
                                this.wait   = false;
                            });
                    }
                });
            }
        });
    }

    ngOnDestroy(){
        if (this.routerSubscription){
            this.routerSubscription.unsubscribe();
        }
    }

    private submitForm(){
        let promise,
            entity  = Object.assign({}, this.flight),
            service = this.isLineItem ? this.lineItemService : this.flightService;

        this.waitSubmit = true;

        entity.emptyProps  = [];

        Object.keys(this.flight).forEach(v => {
            if (!['resetAwareProps', 'propsWithFlightValues'].includes(v)){
                if (this.flight[v] === '' || this.flight[v] === null || (Array.isArray(this.flight[v]) && this.flight[v].length === 0)){
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
                    let strings     = this.isLineItem ? this.strings.lineitem : this.strings.flight;
                    this.backUrl    = strings.edit_back_url(id);
                }

                this.waitSubmit = false;
                this.router.navigateByUrl(this.backUrl);
            })
            .catch(e => {
                if (e.status    === 412){
                    this.errors = e.json();
                    this.waitSubmit = false;

                    setImmediate(()=>{
                        let errorEl = window.document.querySelector('.has-error');
                        if (errorEl !== null){
                            let bounds  = errorEl.getBoundingClientRect();
                            window.scrollTo(0, window.scrollY+bounds.top-70);
                        }
                    });
                }
            });
    }

    private geoChange(e: any) {
        this.flight.geoChannelIds = e;
    }

    private excludedGeoChange(e: any) {
        this.flight.excludedGeoChannelIds = e;
    }

    private geoAddressesChange(e: any) {
        this.flight.addresses = e;
    }

    private excludedGeoAddressesChange(e: any) {
        this.flight.excludedAddresses = e;
    }

    private onDScheduleChange(e: any){
        this.flight.schedules   = e;
    }

    private convTrackingChange(e: any){
        this.flight.conversionIds   = e;
    }

    private siteIdsChange(e: any){
        let ids    = [];
        for (let v of e){
            ids.push(v.id);
        }
        this.flight.siteIds = ids;
    }

    private devicesChange(e: any){
        this.flight.deviceChannelIds    = e;
    }

    private resetField(e: any, fieldName: string){
        if (e){
            e.preventDefault();
            e.stopPropagation();
        }

        if (!this.isLineItem){
            return;
        }

        if (this.flight['propsWithFlightValues'] === undefined){
            this.flight['propsWithFlightValues']   = [];
        }

        let fieldNames: Array<string> = [ fieldName ];
        switch (fieldName){
            case 'dateStart':
                setImmediate(() => {
                    jQuery('[name=dateStart]')
                        .data('DateTimePicker')
                        .format(dateFormatShort)
                        .date(moment(this.flightDefaults.dateStart, dateFormatShort));
                });
                break;
            case 'dateEnd':
                this.resetDateEndAdditional();
                if (this.flightDefaults.dateEnd === null && this.flight.deliveryPacing === 'D') {
                    fieldNames.push('deliveryPacing', 'dailyBudget');
                }
                break;
            case 'deliveryPacing':
                fieldNames.push('dailyBudget');
                if (this.flightDefaults.deliveryPacing === 'D' && this.flight.dateEnd === null) {
                    fieldNames.push('dateEnd');
                    this.resetDateEndAdditional();
                }
                break;
            case 'rateType':
                fieldNames.push('rateValue');
                break;
            case 'geoChannelIds':
                fieldNames.push('geoChannelIds', 'excludedGeoChannelIds');
                break;
        }

        let obj = {};
        fieldNames.forEach( f => {
            obj[f] = this.flightDefaults[f];
            this.flight['propsWithFlightValues'].push(f);
        });
        if (fieldName === 'frequencyCap' && obj['frequencyCap'] !== null) {
            delete obj['frequencyCap'].id;
            if (this.flight.frequencyCap) {
                obj['frequencyCap'].version = this.flight.frequencyCap.version;
            }
        }

        this.flight  = Object.assign({}, this.flight, obj);
        this.resetableFields = this.resetableFields.filter(f => { return !fieldNames.includes(f) });
    }

    private resetDateEndAdditional() {
        setImmediate(() => {
            jQuery('[name=dateEnd]')
                .data('DateTimePicker')
                .format(dateFormatShort)
                .date(moment(this.flightDefaults.dateEnd, dateFormatShort));
        });
    }
}
