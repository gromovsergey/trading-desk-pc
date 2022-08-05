import { Component, Input, Output, EventEmitter } from '@angular/core';

import { IconComponent }          from './icon.component';
import { DisplayStatusDirective } from './display_status.directive';

@Component({
    selector: 'ui-display-status-toggle',
    template: `<button (click)="changeStatus($event)" class="btn btn-toggle">
              <ui-icon [displayStatus]="status" [fliph]="direction" [icon]="'toggle-on'"></ui-icon>
              </button>`
})

export class DisplayStatusToggleComponent {

    @Input() status: string;
    @Input() statusObject: any;

    @Output() onStatusChange: EventEmitter<any>  = new EventEmitter();

    public direction: boolean;
    private activeStatus: Array<string> = ['LIVE', 'NOT_LIVE', 'LIVE_NEED_ATT'];


    ngOnChanges(){
        this.direction  = this.activeStatus.includes(this.status.split('|')[0]);
    }


    public changeStatus(e: any){
        e.preventDefault();

        let currentStatus   = this.statusObject.displayStatus.split('|')[0];

        this.statusObject.statusChangeOperation         = this.activeStatus.includes(currentStatus) ? 'INACTIVATE': 'ACTIVATE';
        this.statusObject.displayStatus = this.status   = this.activeStatus.includes(currentStatus) ? 'INACTIVE': 'LIVE';

        this.onStatusChange.emit(this.statusObject);
    }
}
