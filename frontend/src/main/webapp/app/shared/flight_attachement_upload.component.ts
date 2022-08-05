import { Component, Input, ViewChild, ElementRef, Output, EventEmitter } from '@angular/core';

import { FileService }         from '../shared/file.service';
import { IconComponent }       from '../shared/icon.component';
import { HintComponent }       from '../shared/hint.component';
import { FileUploadComponent } from '../shared/file_upload.component';

@Component({
    selector: 'ui-flight-attachment-upload',
    templateUrl: 'file_upload.html'
})

export class FlightAttachmentUploadComponent extends FileUploadComponent {

    @Input() title: string;
    @Input() flightId: number;
    @Input() hint: string;

    @ViewChild('fileUpload') fileUploadEl: ElementRef;
    @Output() onError   = new EventEmitter();
    @Output() onUpload  = new EventEmitter();

    constructor(protected fileService: FileService){
        super(fileService);
    }

    ngOnInit(){
        if (!this.flightId){
            throw new Error('Account ID is missing');
        }

        window.addEventListener('focus', (e) => {
            this.btnEnable();
        });
    }

    protected uploadService(data){
        this.actionErrors = null;
        return this.fileService.attachmentUpload(data, this.flightId).catch(e => {
                this.actionErrors = e.json().actionError || null;
                return false;
            })
            .then(() => {
                this.onUpload.emit(true);
            });
    }
}
