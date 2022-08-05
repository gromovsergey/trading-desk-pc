import { Component, Input, EventEmitter, Output } from '@angular/core';
import { RouterModule }                           from '@angular/router';

import { LoadingComponent }        from '../shared/loading.component';
import { OptionTransferComponent } from '../shared/option_transfer.component';
import { AdvertiserService }       from '../advertiser/advertiser.service';
import { AdvertiserSessionModel }  from '../advertiser/advertiser_session.model';

@Component({
    selector: 'ui-flight-edit-inventory',
    templateUrl: 'inventory_source.html'
})

export class FlightEditInventorysourceComponent {

    @Input() siteIds: Array<number>;
    @Output() onChange  = new EventEmitter();

    public radioVal: boolean   = false;
    public wait: boolean       = false;
    private sites: Array<any>;
    private sitesAvailable: Array<any> = [];
    private sitesSelected: Array<any> = [];
    private sort  = function (a, b) {
        if (a.name === b.name) return 0;
        return (a.name > b.name) ? 1 : -1;
    };

    constructor(private advertiserService: AdvertiserService){}

    ngOnChanges(){
        if (this.siteIds && this.siteIds.length){
            this.radioVal   = true;
            this.loadSites();
        } else {
            this.radioVal   = false;
        }
    }

    private loadSites(): Promise<any>{
        this.wait   = true;
        return this.advertiserService
            .getSiteList(new AdvertiserSessionModel().id)
            .then(list => {
                this.sites  = list;
                this.initOptiontransfer();
                this.wait   = false;
            });
    }

    public switchType(e: any){
        this.radioVal   = !this.radioVal;
        if (!this.radioVal){
            this.siteIds    = [];
            this.onChange.emit(this.siteIds);
            this.initOptiontransfer();
        } else if (!this.sites) {
            this.loadSites();
        }
    }

    private onSiteListChange(e: any){
        if (e.length === this.sites.length) e = [];
        this.onChange.emit(e);
    }

    private initOptiontransfer(){
        this.sitesAvailable = [];
        this.sitesSelected  = [];

        this.sites.forEach(v => {
            if (this.siteIds.indexOf(v.siteId) !== -1){
                this.sitesSelected.push({
                    id: v.siteId,
                    name: v.name
                });
            } else {
                this.sitesAvailable.push({
                    id: v.siteId,
                    name: v.name
                });
            }
        });
    }
}
