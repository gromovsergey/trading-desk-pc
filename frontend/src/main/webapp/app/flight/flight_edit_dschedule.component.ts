import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterModule }                           from '@angular/router';
import { FormsModule }                            from '@angular/forms';

import { IconComponent }  from '../shared/icon.component';
import { moment, jQuery } from '../common/common.const';
import { L10nWeekDays }       from '../common/L10n.const';

@Component({
    selector: 'ui-flight-edit-dschedule',
    templateUrl: 'delivery_schedule.html'
})

export class FlightEditDscheduleComponent {

    @Input() schedules: Array<string>;
    @Output() onScheduleChange  = new EventEmitter();

    public radio: number   = 0;
    private phases      = Array(24).fill(0).map((v,i) => {return i});
    private days;
    private isMDown: boolean        = false;
    private mode: boolean           = false;
    private slots: Array<number>    = [];
    private slot_size: number       = 30;
    private outVals: Array<string>;

    constructor(){
        this.days = [1,2,3,4,5,6,0].map(v => {
          return L10nWeekDays[moment(v, 'e').format('ddd')];
        });
    }

    ngOnChanges(){
        if (this.schedules.length){
            this.radio  = 1;
        } else {
            this.radio  = 0;
        }
        this.outVals    = this.schedules || [];
        setImmediate(()=>{
            this.populateSlots();
        });
    }


    private mDown(e: any){
        e.preventDefault();
        this.isMDown    = true;
        this.mode       = !e.target.classList.contains('on');
    }

    private mUp(e: any){
        e.preventDefault();
        this.isMDown    = false;
    }

    private bindPhase(e: any, phase: number, click?:boolean){
        let el  = e.target;
        if (this.isMDown || click){
            if (this.mode){
                if (!~this.slots.indexOf(phase)){
                    this.slots.push(phase);

                    this.countPhase();
                }
            } else {
                el.classList.remove('on');
                this.slots  = this.slots.filter(v => {
                    return v !== phase;
                });
                this.countPhase();
            }
        }
    }

    private countPhase(){
        let last    = null,
            lval    = '';

        this.outVals    = [];
        this.slots.sort((a, b) => {
            return a - b;
        }).forEach(v => {
            if (lval){
                if (v !== last + 1){
                    this.outVals.push(lval + ((last + 1) * this.slot_size - 1));
                    lval    = (v * this.slot_size) + ':';
                    last    = v;
                } else {
                    last    = v;
                }
            } else {
                lval    = (v * this.slot_size) + ':';
                last    = v;
            }
        });
        if (last !== null && lval){
            this.outVals.push(lval + ((last + 1) * this.slot_size - 1));
        }
        this.onScheduleChange.emit(this.outVals);
    }

    public typeChange(e: any){
        setImmediate(() => {
            if (this.radio === 0){
                this.onScheduleChange.emit([]);
            } else {
                this.populateSlots();
                this.onScheduleChange.emit(this.outVals);
            }
        });
    }

    private populateSlots(){
        this.slots    = [];
        this.outVals.forEach(v => {
            let a       = v.split(':'),
                start   = +a[0]/this.slot_size,
                end     = (+a[1]+1)/this.slot_size;

                for (let i = start; i < end; i++) {
                    this.slots.push(i);
                }
        });
    }
}
