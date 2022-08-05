import {Component, Input, ViewChild, ElementRef, Output, EventEmitter} from '@angular/core';

import {FileService} from '../shared/file.service';
import {FileUploadComponent} from '../shared/file_upload.component';

@Component({
    selector: 'ui-account-document-upload',
    templateUrl: 'file_upload.html'
})

export class AccountDocumentUploadComponent extends FileUploadComponent {

    @Input() title: string;
    @Input() accountId: number;
    @Input() hint: string;

    @ViewChild('fileUpload') fileUploadEl: ElementRef;
    @Output() onError = new EventEmitter();
    @Output() onUpload = new EventEmitter();

    constructor(protected fileService: FileService) {
        super(fileService);
    }

    ngOnInit() {
        if (!this.accountId) {
            throw new Error('Account ID is missing');
        }

        window.addEventListener('focus', (e) => {
            this.btnEnable();
        });
    }

    protected uploadService(data) {
        this.actionErrors = null;
        return this.fileService.documentUpload(data, this.accountId).catch(e => {
            this.actionErrors = e.json().actionError || null;
            return false;
        })
            .then(() => {
                this.onUpload.emit(true);
            });
    }
}
