import {Component, Input, ViewChild, ElementRef, Output, EventEmitter, OnInit, HostListener} from '@angular/core';
import {FileService} from '../../services/file.service';
import {FileUploadComponent} from '../../file_upload.component';
import {ErrorHelperStatic} from '../../static/error-helper.static';

@Component({
  selector: 'ui-account-document-upload',
  templateUrl: './account-document-upload.component.html'
})
export class AccountDocumentUploadComponent extends FileUploadComponent implements OnInit {

  @ViewChild('fileUpload') fileUploadEl: ElementRef;
  @Input() title: string;
  @Input() accountId: number;
  @Input() hint: string;
  @Output() uploadError = new EventEmitter();
  @Output() beginUpload = new EventEmitter();

  constructor(protected fileService: FileService) {
    super(fileService);
  }

  @HostListener('window:focus', [])
  windowFocus(): void {
    this.btnEnable();
  }

  ngOnInit(): void {
    if (!this.accountId) {
      console.error('Account ID is missing');
    }
  }

  async uploadService(data): Promise<any> {
    this.actionErrors = null;
    try {
      await this.fileService.documentUpload(data, this.accountId);
      this.beginUpload.emit(true);
    } catch (err) {
      this.actionErrors = ErrorHelperStatic.matchErrors(err).actionError || null;
    }
  }
}
