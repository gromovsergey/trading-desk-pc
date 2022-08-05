import { Component, OnChanges, Input, ViewChild, ElementRef, Output, EventEmitter } from '@angular/core';
import { RouterModule }                                                             from '@angular/router';

import { LoadingComponent }                                from '../shared/loading.component';
import { IconComponent }                                   from '../shared/icon.component';
import { DropdownButtonComponent, DropdownButtonMenuItem } from '../shared/dropdown_button.component';
import { PopupComponent }                                  from '../shared/popup.component';
import { DisplayStatusToggleComponent }                    from '../shared/display_status_toggle.component';
import { DisplayStatusDirective }                          from '../shared/display_status.directive';
import { AdvertiserSessionModel }                          from '../advertiser/advertiser_session.model';
import { CreativeService }                                 from '../creative/creative.service';
import { CreativePreview }                                 from '../creative/creative_preview.component';
import { LineItemService }                                 from '../lineitem/lineitem.service';
import { FlightService }                                   from './flight.service';


@Component({
    selector: 'ui-flight-creatives',
    templateUrl: 'creatives.html'
})

export class FlightCreativesComponent implements OnChanges{

    @Input() flightId: number;
    @Input() lineItemId: number;
    @Input() short: boolean;
    @Input() readonly: boolean  = false;

    @Output() onStatusChange    = new EventEmitter();

    @ViewChild('checkAll')      checkAllInput: ElementRef;
    @ViewChild('checkAllLink')  checkAllLinkInput: ElementRef;

    public wait: boolean   = true;
    public waitPopup: boolean;
    public canUpdate: boolean;
    private advertiser: AdvertiserSessionModel  = new AdvertiserSessionModel();
    public creatives: Array<any> = [];
    public creativePreview;
    public creativesLink: Array<any> = [];
    private bulkMenu    = [];
    public popupVisible: boolean   = false;
    public popupOptions;

    private service;
    private entityId: number;

    constructor(private flightService: FlightService,
                private lineItemService: LineItemService,
                private creativeService: CreativeService){
        this.popupOptions = {
            title:      '_L10N_(flight.button.linkCreatives)',
            btnTitle:   '_L10N_(button.link)',
            btnIcon:    'link',
            btnIconDisabled:    false
        };

        this.bulkMenu.push(
            new DropdownButtonMenuItem('_L10N_(button.unlink)', {onclick: this.deleteCreatives.bind(this)})
        );
    }

    ngOnChanges(){
        this.service    = this.flightId ? this.flightService : this.lineItemService;
        this.entityId   = this.flightId ? this.flightId : this.lineItemId;
        this.loadCreativeList();
    }

    private loadCreativeList(){
        if (this.entityId) {
            Promise.all([
                this.service.getCreativeList(this.entityId),
                this.flightId ?
                    this.service.isAllowedLocal(this.entityId, 'flight.updateFlightCreatives') :
                    this.service.isAllowedLocal(this.entityId, 'flight.updateLineItemCreatives')
            ]).then(res => {
                let list = res[0];
                this.canUpdate = res[1];

                if (!list || !list.length) {
                    this.creatives = [];
                    this.wait = false;
                    return;
                }

                list.forEach((v, i) => {
                    v.checked = false;
                });
                this.creatives = list;
                this.wait = false;

                setImmediate(() => {
                    this.onTableChange();
                });
            });
        } else {
            this.wait = false;
        }
    }

    private toggleCheckedAll(e?: any, type?: string){
        let creatives       = type === 'popup' ? this.creativesLink: this.creatives;

        creatives.forEach(v => {
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
        this.loadLinkCreativesList();
    }

    private loadLinkCreativesList(){
        this.popupOptions.btnIconDisabled   = true;
        this.waitPopup  = true;

        this.creativeService.getListByAdvertiserId(this.advertiser.id)
            .then(creativesLink    => {
                if (this.creatives.length && creativesLink.length){
                    creativesLink.map(v => {
                        v.checked   = this.creatives.find(c => {
                            return (c.creativeId === v.id);
                        }) !== undefined;
                    });
                }
                this.creativesLink  = creativesLink;

                this.popupOptions.btnIconDisabled   = this.creativesLink.length === 0;
                this.waitPopup  = false;

                setImmediate(() => {
                    this.onTableChange(null, 'popup');
                });
            });
    }

    private onTableChange(e?: any, type?:string){
        let chekedAll   = true,
            sites       = type === 'popup' ? this.creativesLink : this.creatives;

        if (sites.length === 0) return;

        let chbx        = type === 'popup' ?
            this.checkAllLinkInput && this.checkAllLinkInput.nativeElement :
            this.checkAllInput && this.checkAllInput.nativeElement;
        if (!chbx) return;

        sites.forEach(v => {
            chekedAll   = chekedAll && v.checked;
        });
        chbx.checked  = chekedAll;
    }

    public popupHide(e: any){
        this.popupVisible   = false;
    }

    public popupSave(e: any){
        this.popupOptions.btnIconDisabled    = true;
        let ids: Array<number>  = [];

        this.creativesLink.forEach(v => {
            if (v.checked) {
                ids.push(v.id);
            }
        });

        this.wait   = true;
        this.popupVisible   = false;
        this.service.linkCreatives(this.entityId, ids)
            .then(newStatus => {
                this.onStatusChange.emit(newStatus);
                this.loadCreativeList();
                this.popupOptions.btnIconDisabled    = false;
            });
    }

    private popupToggleCheckedAll(e){
        this.creativesLink.forEach((v,i)=>{
            v.checked   = e.target.checked;
        });
    }

    private changeStatus(creative: any){
        this.service
            .linkStatusChange(this.entityId, creative.creativeId, creative.statusChangeOperation)
            .then(newStatus => {
                creative.displayStatus = newStatus.creativeLinkDispayStatus.split('|')[0];
                this.onStatusChange.emit(newStatus);
            });
    }

    private deleteCreatives(e?: any, creativeId?: number){

        let ids: Array<number>  = [];

        if (creativeId === undefined){
            this.creatives.forEach(v => {
                if (!v.checked){
                    ids.push(v.creativeId);
                }
            });
        } else {
            this.creatives.forEach(v => {
                if (v.creativeId !== creativeId && v.displayStatus !== 'DELETED'){
                    ids.push(v.creativeId);
                }
            });
        }

        if (ids.length === this.creatives.length){
            // nothing to delete
            return;
        }

        this.wait   = true;
        this.service.linkCreatives(this.entityId, ids)
            .then(newStatus => {
                this.onStatusChange.emit(newStatus);
                this.loadCreativeList();

                setImmediate(() => {
                    this.onTableChange();
                });
            });
    }

    private preview(e: any, creative: any) {
        e.preventDefault();
        this.creativePreview        = creative;
    }

    public onPreviewClose(e?: any) {
        this.creativePreview        = null;
    }
}
