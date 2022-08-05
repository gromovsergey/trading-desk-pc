import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { RouterModule }                                   from '@angular/router';

import { IconComponent }          from '../shared/icon.component';
import { LoadingComponent }       from '../shared/loading.component';
import { DisplayStatusDirective } from '../shared/display_status.directive';
import { AdvertiserSessionModel } from '../advertiser/advertiser_session.model';
import { ConversionService }      from '../conversion/conversion.service';


@Component({
    selector: 'ui-flight-edit-conversions',
    templateUrl: 'conversions.html'
})

export class FlightEditConversionsComponent implements OnInit {

    @Input() conversionIds: Array<number>;
    @Output() onChange  = new EventEmitter();

    public wait: boolean = false;
    public conversions: Array<any>;


    constructor(private conversionsService: ConversionService){}

    ngOnInit(){
        this.wait       = true;
        this.conversionsService.getListByAdvertiserId(new AdvertiserSessionModel().id)
            .then(list  => {
                this.conversionIds = list
                    .filter(v => { return this.conversionIds.includes(v.conversion.id) })
                    .map( v => { return v.conversion.id });
                this.onChange.emit(this.conversionIds);

                list.forEach(v => {
                    v['checked'] = (this.conversionIds.includes(v.conversion.id));
                });
                this.conversions    = list;
                this.wait           = false;
            });
    }

    private selectConversion(e:any, item: any){
        e.preventDefault();

        item.checked    = !item.checked;
        if (item.checked){
            this.conversionIds.push(item.conversion.id);
        } else {
            this.conversionIds  = this.conversionIds.filter(f => {return f !== item.conversion.id})
        }
        this.onChange.emit(this.conversionIds);
    }
}
