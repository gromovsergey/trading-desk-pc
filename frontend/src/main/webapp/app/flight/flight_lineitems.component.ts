import {Component, OnChanges, Input, Output, EventEmitter} from '@angular/core';

import {DropdownButtonMenuItem} from '../shared/dropdown_button.component';
import {AdvertiserSessionModel} from '../advertiser/advertiser_session.model';
import {LineItemService} from '../lineitem/lineitem.service';
import {DateRange} from "../shared/date_range";
import {DateRangeModel} from "../shared/date_range.model";

@Component({
    selector: 'ui-flight-lineitems',
    templateUrl: 'lineitems.html'
})

export class FlightLineItemsComponent implements OnChanges{

    @Input() flightId: number;
    @Input() canCreate: boolean;
    @Output() onLoad: EventEmitter<any>  = new EventEmitter();
    @Output() onStatusChange = new EventEmitter();

    public wait: boolean = true;
    private canEditLineItems: boolean = false;
    public lineItems: Array<any>;
    private bulkMenu    = [];
    private accountCurrency = new AdvertiserSessionModel().currencyCode;

    private dateRange: DateRange = new DateRangeModel();

    constructor(private lineItemService: LineItemService){
        this.bulkMenu.push(new DropdownButtonMenuItem('_L10N_(button.activate)', {onclick: this.bulkStatusChange.bind(this, 'ACTIVATE', 0)}));
        this.bulkMenu.push(new DropdownButtonMenuItem('_L10N_(button.deactivate)', {onclick: this.bulkStatusChange.bind(this, 'INACTIVATE', 1)}));
        this.bulkMenu.push(new DropdownButtonMenuItem('_L10N_(button.delete)', {onclick: this.bulkStatusChange.bind(this, 'DELETE', 2)}));
    }

    ngOnChanges() {
        this.wait = true;
        Promise.all([
            this.lineItemService.getListByFlightId(this.flightId, this.dateRange.dateStart, this.dateRange.dateEnd),
            this.lineItemService.isAllowedLocal(this.flightId, 'flight.update')
        ]).then(res => {
            let list = res[0];
            list.forEach(li => {
                li.checked = false;
            });

            this.lineItems = list;
            this.onLoad.emit(this.lineItems);

            this.canEditLineItems = Boolean(res[1]);

            this.wait = false;
        });
    }

    public reloadLineItems(e: any) {
        this.lineItemService.getListByFlightId(this.flightId, this.dateRange.dateStart, this.dateRange.dateEnd)
            .then(list => {
                list.forEach((item, index) => {
                    item.checked = this.lineItems[index].checked;
                });
                this.lineItems = list;
            });
    }

    private toggleCheckedAll(e) {
        this.lineItems.forEach(v => {
            if (this.canEditLineItems) {
                v.checked = e.target.checked;
            }
        });
    }

    private changeStatus(lineItem: any){
        this.lineItemService
            .changeStatus(lineItem.id, lineItem.statusChangeOperation)
            .then(newStatus => {
                lineItem.displayStatus = newStatus[0];
                this.onStatusChange.emit(null);
            });
    }

    private bulkStatusChange(status: string, index: number){
        this.bulkMenu[index].deactivate();

        let ids:Array<number> = this.lineItems.filter(v => {return v.checked}).map(v => {return v.id});

        if (ids.length){

            if (status === 'DELETE' && ids.length+1 === this.lineItems.length && !confirm('All Flight values will be overwritten by the last Line Item values.\nContinue?')){
                return;
            }

            this.wait   = true;
            this.lineItemService.changeStatus(ids, status)
                .then(statusList => {
                    ids.forEach((id,i) => {
                        this.lineItems.find(f => {
                            return f.id === id;
                        }).displayStatus = statusList[i];
                    });
                    this.lineItems  = this.lineItems.filter(v => {
                        return v.displayStatus !== 'DELETED';
                    });
                    this.wait   = false;
                    this.onStatusChange.emit(this.lineItems);
                });
        }
    }
}
