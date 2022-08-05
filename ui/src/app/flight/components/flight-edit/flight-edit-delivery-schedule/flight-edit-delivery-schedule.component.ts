import {Component, Input, Output, EventEmitter, OnChanges} from '@angular/core';
import {moment} from '../../../../common/common.const';
import {L10nWeekDays} from '../../../../common/L10n.const';
import {L10nStatic} from '../../../../shared/static/l10n.static';
import {MatRadioChange} from '@angular/material/radio';

@Component({
  selector: 'ui-flight-edit-dschedule',
  templateUrl: 'flight-edit-delivery-schedule.component.html',
  styleUrls: ['./flight-edit-delivery-schedule.component.scss']
})
export class FlightEditDscheduleComponent implements OnChanges {

  @Input() schedules: Array<any>;
  @Output() scheduleChange = new EventEmitter();

  radio = 0;
  phases = Array(24).fill(0).map((v, i) => i);
  days;
  isMDown = false;
  mode = false;
  slots: number[] = [];
  slotSize = 30;
  outVals: string[];

  constructor() {
    this.days = [1, 2, 3, 4, 5, 6, 0].map(v => L10nStatic.translate(L10nWeekDays[moment(v, 'e').format('ddd')]));
  }

  ngOnChanges(): void {
    if (this.schedules.length) {
      this.radio = 1;
    } else {
      this.radio = 0;
    }
    this.outVals = this.schedules || [];
    window.setTimeout(() => {
      this.populateSlots();
    });
  }


  mDown(e: any): void {
    e.preventDefault();
    this.isMDown = true;
    this.mode = !e.target.classList.contains('on');
  }

  mUp(e: any): void {
    e.preventDefault();
    this.isMDown = false;
  }

  bindPhase(e: any, phase: number, click?: boolean): void {
    const el = e.target;
    if (this.isMDown || click) {
      if (this.mode) {
        if (!this.slots.includes(phase)) {
          this.slots.push(phase);
          this.countPhase();
        }
      } else {
        el.classList.remove('on');
        this.slots = this.slots.filter(v => v !== phase);
        this.countPhase();
      }
    }
  }

  countPhase(): void {
    let last = null;
    let lval = '';

    this.outVals = [];
    this.slots.sort((a, b) => a - b).forEach(v => {
      if (lval) {
        if (v !== last + 1) {
          this.outVals.push(lval + ((last + 1) * this.slotSize - 1));
          lval = (v * this.slotSize) + ':';
          last = v;
        } else {
          last = v;
        }
      } else {
        lval = (v * this.slotSize) + ':';
        last = v;
      }
    });
    if (last !== null && lval) {
      this.outVals.push(lval + ((last + 1) * this.slotSize - 1));
    }
    this.scheduleChange.emit(this.outVals);
  }

  typeChange(e: MatRadioChange): void {
    this.radio = e.value;
    window.setTimeout(() => {
      if (this.radio === 0) {
        this.scheduleChange.emit([]);
      } else {
        this.populateSlots();
        this.scheduleChange.emit(this.outVals);
      }
    });
  }

  populateSlots(): void {
    this.slots = [];
    this.outVals.forEach(v => {
      const a = v.split(':');
      const start = +a[0] / this.slotSize;
      const end = (+a[1] + 1) / this.slotSize;

      for (let i = start; i < end; i++) {
        this.slots.push(i);
      }
    });
  }
}
