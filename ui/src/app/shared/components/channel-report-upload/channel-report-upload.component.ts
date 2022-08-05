import {Component, Input, ViewChild, ElementRef, OnInit} from '@angular/core';
import {FileService} from '../../services/file.service';
import {FileUploadComponent} from '../../file_upload.component';
import {DropdownButtonMenuItem} from '../dropdown-button/dropdown-button.component';
import {DomSanitizer} from '@angular/platform-browser';
import {L10nStatic} from '../../static/l10n.static';
import {ErrorHelperStatic} from '../../static/error-helper.static';

@Component({
  selector: 'ui-channel-report-upload',
  templateUrl: './channel-report-upload.component.html'
})
export class ChannelReportUploadComponent extends FileUploadComponent implements OnInit {

  @Input() accountId: number;
  @ViewChild('fileUpload') fileUploadEl: ElementRef;
  @ViewChild('downloadBtn') downloadBtnEl: ElementRef;

  uploadMenu: Array<DropdownButtonMenuItem>;
  downloadMenu: Array<DropdownButtonMenuItem>;
  beelineFileName: string;
  accountFileName: string;
  accountSelected = false;
  downloadUrl;
  downloadName;

  constructor(protected fileService: FileService, private sanitizer: DomSanitizer) {
    super(fileService);
  }

  ngOnInit(): void {
    window.addEventListener('focus', (e) => {
      this.btnEnable();
    });

    this.fileService.isAllowedLocal0('channel.uploadReport').then(res => {
      if (res) {
        if (this.accountId != null) {
          this.uploadMenu = [
            new DropdownButtonMenuItem(L10nStatic.translate('channel.report.beelineChannels'),
              {onclick: this.selectFile.bind(this, false)}),
            new DropdownButtonMenuItem(L10nStatic.translate('channel.report.accountChannels'),
              {onclick: this.selectFile.bind(this, true)})
          ];
        } else {
          this.uploadMenu = [
            new DropdownButtonMenuItem(L10nStatic.translate('channel.report.beelineChannels'), {onclick: this.selectFile.bind(this, false)})
          ];
        }
      }
    });

    this.initDownload();
  }

  initDownload(): void {
    this.downloadMenu = [];
    if (this.accountId != null) {
      Promise.all([
        this.fileService.getChannelReportList(null),
        this.fileService.getChannelReportList(this.accountId)
      ]).then(res => {
        if (res[0] && res[0].length > 0) {
          this.beelineFileName = res[0][0];
          this.downloadMenu.push(new DropdownButtonMenuItem(L10nStatic.translate('channel.report.beelineChannels'),
            {onclick: this.downloadReport.bind(this, false)}));
        }
        if (res[1] && res[1].length > 0) {
          this.accountFileName = res[1][0];
          this.downloadMenu.push(new DropdownButtonMenuItem(L10nStatic.translate('channel.report.accountChannels'),
            {onclick: this.downloadReport.bind(this, true)}));
        }
      });
    } else {
      this.fileService.getChannelReportList(null).then(list => {
        if (list && list.length > 0) {
          this.beelineFileName = list[0];
          this.downloadMenu.push(new DropdownButtonMenuItem(L10nStatic.translate('channel.report.beelineChannels'),
            {onclick: this.downloadReport.bind(this, false)}));
        }
      });
    }
  }

  selectFile(accountSelected: boolean): void {
    this.accountSelected = accountSelected;
    if (this.fileUploadEl) {
      this.fileUploadEl.nativeElement.click();
    }
  }

  uploadService(data): Promise<any> {
    this.actionErrors = null;

    return this.fileService.channelReportUpload(data, this.accountSelected ? this.accountId : null)
      .catch(err => {
        this.actionErrors = ErrorHelperStatic.matchErrors(err).actionError || null;
        return false;
      }).then(() => {
        this.beginUpload.emit(true);
        this.initDownload();
      });
  }

  downloadReport(accountSelected: boolean): void {
    this.accountSelected = accountSelected;
    this.fileService.channelReportDownload(this.accountSelected ? this.accountId : null,
      this.accountSelected ? this.accountFileName : this.beelineFileName).then(res => {
      this.downloadName = this.accountSelected ? this.accountFileName : this.beelineFileName;
      if (navigator && navigator.msSaveBlob) {
        // ie hack
        navigator.msSaveBlob(res.target.response, this.downloadName);
      } else {
        const reader = new FileReader();
        reader.readAsDataURL(res.target.response);
        reader.addEventListener('load', (e) => {
          this.downloadUrl = this.sanitizer.bypassSecurityTrustUrl(reader.result.toString());
          window.setTimeout(() => {
            this.downloadBtnEl.nativeElement.click();
          });
        });
      }
    });
  }
}
