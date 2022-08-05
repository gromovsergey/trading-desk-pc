import { Component, Input, ContentChild, ViewChild, ElementRef, OnChanges, EventEmitter, Output } from '@angular/core';

import { IconComponent }    from '../shared/icon.component';
import { LoadingComponent } from '../shared/loading.component';

@Component({
    selector: 'ui-popup',
    templateUrl: 'popup.html'
})

export class PopupComponent implements OnChanges{

    @Input() visible: boolean = false;
    @Input() blocked: boolean = false;
    @Input() options;
    @Input() closeBtnTitle = 'Close';

    @Output() close     = new EventEmitter();
    @Output() save      = new EventEmitter();

    @ContentChild('modal') bodyContentEl: ElementRef;
    @ViewChild('modal') bodyChildEl: ElementRef;

    private _defaults   = {
        title: '',
        icon: '',
        hint: '',
        btnTitle:   '',
        btnIcon:    '',
        btnIconDisabled: false,
        size: 'md'
    };


    ngOnChanges(){
        this.options    = Object.assign(this._defaults, this.options || {});
        this.bodyChildEl.nativeElement.appendChild(this.bodyContentEl.nativeElement);
    }

    public _hide(e: any){
        this.close.emit(true);
    }

    private _save(e: any){
        this.save.emit(true);
    }
}
