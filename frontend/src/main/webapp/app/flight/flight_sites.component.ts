import { Component, OnChanges, Input, ViewChild, ElementRef } from '@angular/core';

import { LoadingComponent }       from '../shared/loading.component';
import { IconComponent }          from '../shared/icon.component';
import { DropdownButtonComponent, DropdownButtonMenuItem } from '../shared/dropdown_button.component';
import { DisplayStatusDirective } from '../shared/display_status.directive';
import { PopupComponent }         from '../shared/popup.component';
import { AdvertiserService }      from '../advertiser/advertiser.service';
import { AdvertiserSessionModel } from '../advertiser/advertiser_session.model';
import { LineItemService }        from '../lineitem/lineitem.service';
import { FlightService }          from './flight.service';

@Component({
    selector: 'ui-flight-sites',
    templateUrl: 'sites.html'
})

export class FlightSitesComponent implements OnChanges{

    @Input() flightId: number;
    @Input() lineItemId: number;
    @Input() short: boolean;
    @Input() readonly: boolean  = false;

    @ViewChild('checkAll')      checkAllInput: ElementRef;
    @ViewChild('checkAllLink')  checkAllLinkInput: ElementRef;

    public wait: boolean       = true;
    public waitPopup: boolean  = true;
    private advertiser: AdvertiserSessionModel  = new AdvertiserSessionModel();
    public sites: Array<any>;
    public sitesLink: Array<any>;
    private siteTableLimited: boolean   = false;
    private siteTableLimit: number      = 10;
    private bulkMenu    = [];
    public popupVisible: boolean   = false;
    public popupOptions;
    private errors  = {
        unchekedAll: false,
        popupUnchekedAll: false
    };

    private service;
    private entityId: number;

    constructor(private flightService: FlightService,
                private lineItemService: LineItemService,
                private advertiserService: AdvertiserService){
        this.popupOptions = {
            title:      '_L10N_(flight.button.linkSources)',
            btnTitle:   '_L10N_(button.link)',
            btnIcon:    'link',
            btnIconDisabled: false
        };

        this.bulkMenu.push(
            new DropdownButtonMenuItem('_L10N_(button.unlink)', {onclick: this.deleteSites.bind(this)})
        );
    }

    ngOnChanges(){
        this.service    = this.flightId ? this.flightService : this.lineItemService;
        this.entityId   = this.flightId ? this.flightId : this.lineItemId;
        this.loadSitesList();
    }

    private loadSitesList(){
        if (this.entityId){
            this.wait = true;
            this.service
                .getSiteList(this.entityId)
                .then(list => {
                    list.forEach((v, i)=> {
                        v.checked = false;
                    });

                    this.siteTableLimited   = (list.length > this.siteTableLimit);

                    this.sites = list;
                    this.wait = false;
                });
        }
    }

    private toggleCheckedAll(e?: any, type?: string){
        let sites       = type === 'popup' ? this.sitesLink : this.sites;

        sites.forEach(v => {
            v.checked   = e.target.checked;
        });
    }

    private chbxCancelBubling(e: any){
        e.stopPropagation();
    }

    private showPopup(e: any){
        e.preventDefault();
        e.stopPropagation();

        this.popupVisible   = true;
        this.loadLinkSitesList();
    }

    public popupHide(e: any){
        this.popupVisible   = false;
    }

    private showFullTable(e: any){
        this.siteTableLimited   = false;
    }

    private loadLinkSitesList(){
        this.popupOptions.btnIconDisabled   = true;
        this.waitPopup  = true;

        this.advertiserService
            .getSiteList(new AdvertiserSessionModel().id)
            .then(sitesLink  => {
                if (this.sites.length && sitesLink.length){
                    sitesLink.map(v => {
                        v.checked   = this.sites.find(c => {
                            return c.siteId === v.siteId;
                        }) !== undefined;
                    });
                }
                this.sitesLink  = sitesLink;

                this.popupOptions.btnIconDisabled   = this.sitesLink.length === 0;
                this.waitPopup  = false;

                setImmediate(() => {
                    this.onTableChange(null, 'popup');
                });
            });
    }

    public popupSave(e: any){
        this.clearErrors();
        this.popupOptions.btnIconDisabled    = true;

        let ids: Array<number>  = [];

        this.sitesLink.forEach(v => {
            if (v.checked) {
                ids.push(v.siteId);
            }
        });

        if (ids.length === 0){
            // all unchecked, show error
            this.errors.popupUnchekedAll        = true;
            this.popupOptions.btnIconDisabled   = false;
            return;
        }

        if (ids.length === this.sitesLink.length){
            // all checked, send empty array to link with all
            ids = [];
        }

        this.wait   = true;
        this.popupVisible   = false;
        this.service.linkSites(this.entityId, ids).then(v => {
            this.loadSitesList();
            this.popupOptions.btnIconDisabled   = false;
        });
    }

    private onTableChange(e?: any, type?:string){
        let chekedAll   = true,
            sites       = type === 'popup' ? this.sitesLink : this.sites,
            chbx        = type === 'popup' ? this.checkAllLinkInput.nativeElement : this.checkAllInput.nativeElement;

        sites.forEach(v => {
            chekedAll   = chekedAll && v.checked;
        });
        chbx.checked  = chekedAll;

        this.clearErrors();
    }

    private clearErrors(){
        this.errors.unchekedAll         = false;
        this.errors.popupUnchekedAll    = false;
    }

    private deleteSites(e?: any, siteId?: number){

        let ids: Array<number>  = [];

        if (siteId === undefined){
            this.sites.forEach(v => {
                if (!v.checked){
                    ids.push(v.siteId);
                }
            });
        } else {
            this.sites.forEach(v => {
                if (v.siteId !== siteId){
                    ids.push(v.siteId);
                }
            });
        }

        if (ids.length === this.sites.length){
            // nothing to delete
            return;
        }

        if (ids.length) {
            this.wait   = true;
            this.service.linkSites(this.entityId, ids).then(v => {
                this.loadSitesList();
            });
        } else {
            this.errors.unchekedAll = true;
        }
    }
}
