import { Component, Input, Output, EventEmitter, ElementRef, ViewChild } from '@angular/core';
import { RouterModule }                                                  from '@angular/router';
import { DomSanitizer }                                                  from '@angular/platform-browser';

import { IconComponent }                   from '../shared/icon.component';
import { HintComponent }                   from '../shared/hint.component';
import { FlightAttachmentUploadComponent } from '../shared/flight_attachement_upload.component';
import { PopupComponent }                  from '../shared/popup.component';
import { FlightService }                   from './flight.service';

@Component({
    selector: 'ui-flight-scan-io',
    templateUrl: 'scan_io.html'
})

export class FlightScanIOComponent {

    @Input() flightId: number;
    @Input() readonly: boolean = false;
    @Output() close = new EventEmitter();
    @ViewChild('downloadBtn') downloadBtnEl: ElementRef;

    public wait: boolean       = true;
    public attachments: Array<any>    = [];
    public options;
    public errors;
    public downloadUrl;
    public downloadName;


    constructor(private flightService: FlightService,
                private sanitizer: DomSanitizer){
        this.options = {
            title: '_L10N_(flight.button.scanIo)'
        };
    }

    ngOnChanges(){
        this.loadList();
    }

    public onClose(e: any){
        this.close.emit(e);
    }

    private loadList(){
        this.wait   = true;
        return this.flightService.getAttachments(this.flightId)
            .then(list => {
                this.attachments    = list;
                this.wait           = false;
            });
    }

    private downloadItem(e: any, item: string){
        e.preventDefault();

        this.wait   = true;
        this.flightService.downloadAttachments(this.flightId, item).then(res => {
            this.downloadName   = item;
            if (navigator && navigator.msSaveBlob) {
                // ie hack
                navigator.msSaveBlob(res.target.response, this.downloadName);
            } else {
                let reader  = new FileReader();
                reader.readAsDataURL(res.target.response);
                reader.addEventListener('load', (e) => {
                    this.downloadUrl    = this.sanitizer.bypassSecurityTrustUrl(reader.result);
                    setImmediate(() => {
                        this.downloadBtnEl.nativeElement.click();
                    });
                });
            }
            this.wait   = false;
        });
    }

    private deleteItem(e: any, item: string){
        e.preventDefault();

        this.wait   = true;
        this.flightService.deleteAttachments(this.flightId, item).then(() => {
            this.loadList();
        });
    }
}
