import { Component, Input, OnChanges, Output, EventEmitter } from '@angular/core';

import { DropdownButtonComponent, DropdownButtonMenuItem }   from './dropdown_button.component';

@Component({
    selector: 'ui-display-status-btn',
    template: `<div class="c-dstatbtn">
              <ui-dropdown-btn class="c-dstatbtn__dropdown" [menu]="menu" [status]="status" [title]="title"></ui-dropdown-btn>
              </div>`
})

export class DisplayStatusButtonComponent implements OnChanges {

    @Input() status: string;
    @Input() url: string;

    @Output() statusChange  = new EventEmitter();

    public menuActive;
    public menuInactive;
    public menuDeleted;

    private _status: Array<string>;
    public menu: Array<DropdownButtonMenuItem>;
    public title: string;
    private statusTypes;

    constructor() {
        this.menuActive = [
            new DropdownButtonMenuItem('_L10N_(status.button.deactivate)', {}, DEACTIVATE_OP),
            new DropdownButtonMenuItem('_L10N_(status.button.delete)', {}, DELETE_OP),
        ];
        this.menuInactive = [
            new DropdownButtonMenuItem('_L10N_(status.button.activate)', {}, ACTIVATE_OP),
            new DropdownButtonMenuItem('_L10N_(status.button.delete)', {}, DELETE_OP),
        ];
        this.menuDeleted = [
            new DropdownButtonMenuItem('_L10N_(status.button.restore)', {}, RESTORE_OP),
        ];

        this.statusTypes = {
            'LIVE': {title: '_L10N_(majorStatus.LIVE)', menu: this.menuActive},
            'LIVE_NEED_ATT': {title: '_L10N_(majorStatus.LIVE)', menu: this.menuActive},
            'DELETED': {title: '_L10N_(majorStatus.DELETED)', menu: this.menuDeleted},
            'NOT_LIVE': {title: '_L10N_(majorStatus.NOT_LIVE)', menu: this.menuInactive},
            'INACTIVE': {title: '_L10N_(majorStatus.INACTIVE)', menu: this.menuInactive}
        };
    }

    ngOnChanges(){
        this._status    = this.status.split('|');
        this._beforeChange();
    }


    private _beforeChange(){
        let type    = this._status[0];
        this.title  = this.statusTypes[type].title;
        this.menu   = this.statusTypes[type].menu;

        let self    = this;
        this.menu.forEach((v,i)=>{
            v.onclick   = function(){
                self.statusChangeEvent(v.operation);
                v.deactivate();
            }
        });
    }

    private statusChangeEvent(name: string){
        this.statusChange.emit(name);
    }
}

const ACTIVATE_OP:   string = 'ACTIVATE';
const DEACTIVATE_OP: string = 'DEACTIVATE';
const DELETE_OP:     string = 'DELETE';
const RESTORE_OP:    string = 'RESTORE';
