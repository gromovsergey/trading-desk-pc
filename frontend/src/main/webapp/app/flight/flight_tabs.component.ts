import { Component, OnChanges, Input } from '@angular/core';
import { RouterModule, Router }        from '@angular/router';

import { DropdownButtonComponent, DropdownButtonMenuItem } from '../shared/dropdown_button.component';

@Component({
    selector: 'ui-flight-tabs',
    templateUrl: 'tabs.html'
})

export class FlightTabsComponent implements OnChanges{

    @Input() lineItems: Array<any>;
    @Input() flightId: number;
    @Input() lineItemId: number;
    @Input() canCreate: boolean;


    constructor(private router: Router) {
    }

    ngOnChanges(e){
        if (this.lineItems !== undefined){
            this.lineItems.forEach(v => {
                if (this.canCreate){
                    v.menu  = [
                        new DropdownButtonMenuItem('_L10N_(button.edit)', {
                            onclick: ()=>{
                                this.router.navigateByUrl('/lineitem/'+v.id+'/edit');
                            }
                        }),
                    ];
                }
            })

        }
    }
}
