import { Component, Input, Output, OnChanges, EventEmitter, OnInit } from '@angular/core';
import { RouterModule }                                              from '@angular/router';
import { DomSanitizer }                                              from '@angular/platform-browser';

import { PopupComponent }    from '../shared/popup.component';
import { ConversionService } from './conversion.service';


@Component({
    selector: 'ui-conversion-preview',
    templateUrl: 'preview.html'
})

export class ConversionPreview implements OnChanges, OnInit {
    @Input() show: boolean = false;
    @Input() title: string = 'Conversion Pixel Code';
    @Input() absencePreviewMsg: string = 'Can\'t show preview';
    @Input() closeBtnTitle = 'Close';
    @Input() conversion;
    @Output() onClose = new EventEmitter();

    public wait: boolean;
    public previewData;
    public popupOptions;


    constructor(private conversionService: ConversionService, private sanitizer: DomSanitizer){
    }

    ngOnInit() {
        this.popupOptions = {
            title: this.title,
            size: 'md'
        };
    }

    ngOnChanges(changes: any){
        if (this.conversion){
            this.previewData    = null;
            this.wait           = true;
            this.conversionService
                .getPixelCode(this.conversion.conversion.id)
                .then( conversion => {
                    this.previewData            = conversion;
                    this.wait   = false;
                });
        }
    }

    public onPopupClose(e?: any){
        this.onClose.emit(e);
    }
}
