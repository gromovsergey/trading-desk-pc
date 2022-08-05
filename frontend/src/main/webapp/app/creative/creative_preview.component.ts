import { Component, Input, Output,
         OnChanges, EventEmitter,
         ViewChild, ElementRef } from '@angular/core';
import { RouterModule }          from '@angular/router';
import { DomSanitizer }          from '@angular/platform-browser';

import { PopupComponent }  from '../shared/popup.component';
import { CreativeService } from './creative.service';

@Component({
    selector: 'ui-creative-preview',
    templateUrl: 'preview.html'
})

export class CreativePreview implements OnChanges {
    @Input() show: boolean  = false;
    @Input() creative;
    @Output() onClose   = new EventEmitter();

    @ViewChild('frame') creativeIframeEl: ElementRef;

    public wait: boolean;
    public previewData;
    public popupOptions;


    constructor(private creativeService: CreativeService,
                private sanitizer: DomSanitizer){
        this.popupOptions = {
            title: '_L10N_(creative.blockName.creativePreview)',
            size: 'md'
        };
    }

    ngOnChanges(){
        if (this.creative){
            if (this.creative.creativeName !== undefined) {
                this.creative.name    = this.creative.creativeName;
            }
            this.previewData    = null;
            this.wait           = true;
            this.creativeService
                .getPreviewUrl(this.creative.creativeId)
                .then(previewData   => {
                    this.popupOptions = Object.assign({}, this.popupOptions, {size: previewData.width > 560 ? 'lg' : 'md'});

                    previewData.secureUrl  = this.sanitizer.bypassSecurityTrustResourceUrl(previewData.url);

                    this.previewData            = previewData;
                    this.wait   = false;
                });
        } else {
            // Prevent IFRAME refresh on removing #345
            if (this.creativeIframeEl) {
                this.creativeIframeEl.nativeElement.src = 'about:blank';
            }
        }
    }

    public onPopupClose(e?: any){
        this.onClose.emit(e);
    }
}
