import { Directive, ElementRef, Input, Output, EventEmitter, OnInit, OnDestroy, OnChanges } from '@angular/core';

import { moment, jQuery, dateFormat, dateFormatShort, dateFormatParse } from '../common/common.const';
import { DpIcons, DpOptions }                                           from './datetime_directive';

@Directive({
    selector: '[datetime]'
})

export class DatetimeDirective implements OnInit, OnDestroy, OnChanges{

    @Input() datetime: string;
    @Input('datetimeMin') datetimeMin: string;
    @Input('datetimeMax') datetimeMax: string;
    @Input('datetimeShort') short: boolean  = false;
    @Output() change:EventEmitter<any>      = new EventEmitter();

    private el: HTMLElement;
    private dpIcons: DpIcons = {
        time: "fa fa-clock-o",
        date: "fa fa-calendar",
        up: "fa fa-arrow-up",
        down: "fa fa-arrow-down",
        next: "fa fa-chevron-right",
        previous: "fa fa-chevron-left"
    };


    constructor(el: ElementRef) {
        this.el = el.nativeElement;
    }

    ngOnInit(){
        let format  = this.short ? dateFormatShort : dateFormat,
            date    = this.datetime ? moment(this.datetime, dateFormatParse) : moment(),
            options: DpOptions = {
                icons: this.dpIcons,
                defaultDate: date,
                format: format
            };

        if (this.datetimeMax){
            options.maxDate  = moment(this.datetimeMax, dateFormatParse);
        }
        if (this.datetimeMin){
            options.minDate  = moment(this.datetimeMin, dateFormatParse);
        }

        jQuery(this.el).datetimepicker(options).on('dp.change', this.datetimeChange.bind(this));
    }

    ngOnChanges(e: any){
        let jqDatetime  = jQuery(this.el).data('DateTimePicker');

        if (jqDatetime === undefined) {
            return;
        }
        if (e.datetimeMax){
            jqDatetime.maxDate(e.datetimeMax.currentValue ? moment(e.datetimeMax.currentValue): false);
        }
        if (e.datetimeMin){
            jqDatetime.minDate(e.datetimeMin.currentValue ? moment(e.datetimeMin.currentValue): false);
        }
    }

    ngOnDestroy(){
        jQuery(this.el).off('dp.change');
    }

    public datetimeChange(e: any){
        this.datetime   = e.date ? e.date.format(dateFormatParse) : '';
        this.change.emit(this.datetime);
    }
}