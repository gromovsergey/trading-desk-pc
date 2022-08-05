import { Component, Input, ViewChild, ElementRef, OnChanges, AfterViewInit } from '@angular/core';
import { RouterModule }                                                      from '@angular/router';
import { DomSanitizer }                                                      from '@angular/platform-browser';

import { LoadingComponent } from '../shared/loading.component';
import { CreativeService }  from './creative.service';
import { Creative }         from './creative';

@Component({
    selector: 'ui-creative-live-preview',
    templateUrl: 'live_preview.html'
})

export class CreativeLivePreview implements OnChanges, AfterViewInit {

    @Input() creative: Creative;
    @Input() rnd: number;

    @ViewChild('overlay') overlayEl: ElementRef;

    public wait: boolean;
    public preview;


    constructor(private creativeService: CreativeService,
                private sanitizer: DomSanitizer) {
    }

    ngAfterViewInit(){
        this.overlayEl.nativeElement.style.height   = this.creative.height + 'px';
    }

    ngOnChanges(){
        if (!this.wait){
            this.updatePreview();
        }
    }

    private updatePreview(){
        this.wait   = true;
        this.creativeService.getLivePreview(this.creative)
            .then(preview => {
                this.preview            = preview;
                this.preview.secureUrl  = this.sanitizer.bypassSecurityTrustResourceUrl(preview.url);
                this.wait               = false;
            })
            .catch(e => {
                this.wait       = false;
                this.preview    = null;
            });
    }
}
