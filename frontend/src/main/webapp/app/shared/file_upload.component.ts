import { Component, Input, ViewChild, ElementRef, Output, EventEmitter } from '@angular/core';

import { MAX_UPLOAD_FILE_SIZE } from '../common/common.const';
import { FileService }          from './file.service';
import { IconComponent }        from './icon.component';

@Component({
    selector: 'ui-file-upload',
    templateUrl: 'file_upload.html'
})

export class FileUploadComponent {

    @Input() title: string  = 'Upload File';
    @Input() clearBtnTitle: string  = 'Clear';
    @Input() name: string   = 'file';
    @Input() accountId: number;

    @Input() value: string;

    @Output() onUpload  = new EventEmitter();
    @Output() onError   = new EventEmitter();

    @ViewChild('fileUpload') fileUploadEl: ElementRef;

    public wait: boolean   = false;
    public hint: string = '';
    public errors;
    public actionErrors;
    public btnDisable: boolean = false;
    public uploadData    = {name: null, url: null};

    constructor(protected fileService: FileService){}

    ngOnInit(){
        if (!this.accountId){
            throw new Error('Account ID is missing');
        }

        if (this.value){
            this.uploadData.name    = this.value;
        }
    }

    public selectFile(e: any){
        e.preventDefault();

        this.fileUploadEl.nativeElement.click();
    }

    public doUpload(e: any){
        let el  = this.fileUploadEl.nativeElement;
        this.errors = [];

        if (el.files.length === 0){
            return;
        }

        if (el.files[0]['size'] > MAX_UPLOAD_FILE_SIZE) {
            this.errors.push('File size should not exceed ' + Math.round(MAX_UPLOAD_FILE_SIZE/(1024*1024)) + 'Mb');
            return;
        }

        if (el.files.length > 1) {
            this.errors.push('Can\'t upload several files. Only first one will be uploaded');
        }

        let formData    = new FormData();
        formData.append(this.name, el.files[0]);

        this.wait   = true;
        this.btnDisable = true;
        this.uploadService(formData)
            .then(data => {
                this.wait   = false;
                this.btnEnable();
                this.uploadData = data;
                this.onUpload.emit(data.name);
            })
            .catch(e => {
                this.wait   = false;
                this.btnEnable();
                this.errors  = e && e.json ? e.json()['actionError'] : [];
                this.onError.emit(e);
            });
    }

    protected uploadService(data: any){
        return this.fileService.creativeUpload(data, this.accountId);
    }

    protected btnEnable(e?: any){
        this.btnDisable = false;
    }

    protected clearField(e: any){
        e.preventDefault();
        this.uploadData = null;
        this.onUpload.emit(null);
    }
}
