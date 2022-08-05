import { Directive, ElementRef, Input, OnChanges } from '@angular/core';

import { DisplayStatusColors } from './display_status_directive';

@Directive({
    selector: '[displayStatus]',
})

export class DisplayStatusDirective implements OnChanges{

    @Input('displayStatus') status: string;
    @Input('displayStatusType') statusType: string = 'color';

    private el: HTMLElement;
    private colors: DisplayStatusColors = colors;


    constructor(el: ElementRef) {
        this.el = el.nativeElement;
    }

    ngOnChanges(){
        this.displayStatus(this.status);
    }

    private displayStatus(status: string) {

        if (status === undefined) return;

        let elStyle = 'color';
        if (this.statusType === 'bg'){
            elStyle = 'backgroundColor';
            this.el.style['color']  = '#fff';
        }

        let aStatus = status.split('|');

        switch (aStatus[0]) {
            case 'LIVE':
                this.el.style[elStyle] = this.colors.green;
                this.el.title   = '_L10N_(majorStatus.LIVE)'+this.getTitle(aStatus[1]);
                break;
            case 'LIVE_NEED_ATT':
                this.el.style[elStyle] = this.colors.amber;
                this.el.title   = '_L10N_(majorStatus.LIVE)'+this.getTitle(aStatus[1]);
                break;
            case 'NOT_LIVE':
                this.el.style[elStyle] = this.colors.red;
                this.el.title   = '_L10N_(majorStatus.NOT_LIVE)'+this.getTitle(aStatus[1]);
                break;
            case 'DELETED':
                this.el.style[elStyle] = this.colors.gray;
                this.el.title   = '_L10N_(majorStatus.DELETED)'+this.getTitle(aStatus[1]);
                break;
            case 'INACTIVE':
            default:
                this.el.style[elStyle] = this.colors.gray;
                this.el.title   = '_L10N_(majorStatus.INACTIVE)'+this.getTitle(aStatus[1]);
        }
    }

    private getTitle(status: string): string{
        switch (status){
            case 'overdraft':
                return ' — ' + '_L10N_(status.messages.overdraft)';
            case 'noBillingContact':
                return ' — ' + '_L10N_(status.messages.noBillingContact)';
            default:
                return '';
        }
    }
}

export const colors: DisplayStatusColors = {
    'green': '#92bf1e',
    'amber': '#e58425',
    'red': '#e03b23',
    'gray': '#a6a5ab'
};
