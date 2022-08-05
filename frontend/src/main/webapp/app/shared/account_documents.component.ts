import {Component, Input, Output, EventEmitter, ElementRef, ViewChild} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {FileService} from "./file.service";

@Component({
    selector: 'ui-account-documents',
    templateUrl: 'account_documents.html'
})

export class AccountDocumentsComponent {

    @Input() accountId: number;
    @Input() accountName: string;
    @Output() close = new EventEmitter();
    @ViewChild('downloadBtn') downloadBtnEl: ElementRef;

    public wait: boolean = true;
    public canUpdate: boolean = false;
    public documents: Array<any> = [];
    public options;
    public errors;
    public downloadUrl;
    public downloadName;

    constructor(private fileService: FileService,
                private sanitizer: DomSanitizer) {
    }

    ngOnChanges(){
        this.options = {
            title: '_L10N_(account.documents.for)' + ' ' + this.accountName
        };
        this.checkCanUpdate();
        this.loadList();
    }

    public onClose(e: any){
        this.close.emit(e);
    }

    public loadList() {
        this.wait = true;
        return this.fileService.getDocuments(this.accountId)
            .then(list => {
                this.documents = list;
                this.wait = false;
            });
    }

    private checkCanUpdate() : void {
        this.fileService.isAllowedLocal(this.accountId, 'account.updateAdvertisingDocuments')
            .then(res => {
                this.canUpdate = res;
            });
    }

    private downloadItem(e: any, item: string) {
        e.preventDefault();

        this.wait = true;
        this.fileService.downloadDocuments(this.accountId, item).then(res => {
            this.downloadName = item;
            if (navigator && navigator.msSaveBlob) {
                // ie hack
                navigator.msSaveBlob(res.target.response, this.downloadName);
            } else {
                let reader = new FileReader();
                reader.readAsDataURL(res.target.response);
                reader.addEventListener('load', (e) => {
                    this.downloadUrl = this.sanitizer.bypassSecurityTrustUrl(reader.result);
                    setImmediate(() => {
                        this.downloadBtnEl.nativeElement.click();
                    });
                });
            }
            this.wait = false;
        });
    }

    private deleteItem(e: any, item: string) {
        e.preventDefault();

        this.wait = true;
        this.fileService.deleteDocuments(this.accountId, item).then(() => {
            this.loadList();
        });
    }
}
