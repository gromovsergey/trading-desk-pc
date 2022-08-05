import {Component, Input, ViewChild, ElementRef} from '@angular/core';

import {FileService} from '../shared/file.service';
import {FileUploadComponent} from '../shared/file_upload.component';
import {DropdownButtonMenuItem} from "./dropdown_button.component";
import {DomSanitizer} from "@angular/platform-browser";

@Component({
    selector: 'ui-channel-report-upload',
    templateUrl: 'channel_report_upload.html'
})

export class ChannelReportUploadComponent extends FileUploadComponent {

    @Input() accountId: number;

    @ViewChild('fileUpload') fileUploadEl: ElementRef;
    @ViewChild('downloadBtn') downloadBtnEl: ElementRef;

    public uploadMenu: Array<DropdownButtonMenuItem>;
    public downloadMenu: Array<DropdownButtonMenuItem>;

    private beelineFileName: string;
    private accountFileName: string;

    private accountSelected: boolean = false;

    public downloadUrl;
    public downloadName;

    constructor(protected fileService: FileService, private sanitizer: DomSanitizer) {
        super(fileService);
    }

    ngOnInit() {
        window.addEventListener('focus', (e) => {
            this.btnEnable();
        });

        this.fileService.isAllowedLocal0("channel.uploadReport").then(res => {
            if (res) {
                if (this.accountId != null) {
                    this.uploadMenu = [
                        new DropdownButtonMenuItem('_L10N_(channel.report.beelineChannels)', {onclick: this.selectFile.bind(this, false)}),
                        new DropdownButtonMenuItem('_L10N_(channel.report.accountChannels)', {onclick: this.selectFile.bind(this, true)})
                    ];
                } else {
                    this.uploadMenu = [
                        new DropdownButtonMenuItem('_L10N_(channel.report.beelineChannels)', {onclick: this.selectFile.bind(this, false)})
                    ];
                }
            }
        });

        this.initDownload();
    }

    private initDownload() {
        this.downloadMenu = [];
        if (this.accountId != null) {
            Promise.all([
                this.fileService.getChannelReportList(null),
                this.fileService.getChannelReportList(this.accountId)
            ]).then(res => {
                if (res[0] && res[0].length > 0) {
                    this.beelineFileName = res[0][0];
                    this.downloadMenu.push(new DropdownButtonMenuItem('_L10N_(channel.report.beelineChannels)', {onclick: this.downloadReport.bind(this, false)}));
                }
                if (res[1] && res[1].length > 0) {
                    this.accountFileName = res[1][0];
                    this.downloadMenu.push(new DropdownButtonMenuItem('_L10N_(channel.report.accountChannels)', {onclick: this.downloadReport.bind(this, true)}));
                }
            });
        } else {
            this.fileService.getChannelReportList(null).then(list => {
                if (list && list.length > 0) {
                    this.beelineFileName = list[0];
                    this.downloadMenu.push(new DropdownButtonMenuItem('_L10N_(channel.report.beelineChannels)', {onclick: this.downloadReport.bind(this, false)}));
                }
            });
        }
    }

    public selectFile(accountSelected: boolean){
        this.accountSelected = accountSelected;
        if (this.fileUploadEl) {
            this.fileUploadEl.nativeElement.click();
        }
    }

    protected uploadService(data) {
        this.actionErrors = null;

        return this.fileService.channelReportUpload(data, this.accountSelected ? this.accountId : null).catch(e => {
            this.actionErrors = e.json().actionError || null;
            return false;
        }).then(() => {
            this.onUpload.emit(true);
            this.initDownload();
        });
    }

    private downloadReport(accountSelected: boolean) {
        this.accountSelected = accountSelected;
        this.fileService.channelReportDownload(this.accountSelected ? this.accountId : null,
            this.accountSelected ? this.accountFileName : this.beelineFileName).then(res => {
            this.downloadName = this.accountSelected ? this.accountFileName : this.beelineFileName;
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
        });
    }
}