import {Component, Input, ViewChild, ElementRef, Output, EventEmitter, OnInit} from '@angular/core';
import {FileService} from './services/file.service';
import {FileUploadComponent} from './file_upload.component';
import {ErrorHelperStatic} from './static/error-helper.static';

@Component({
  selector: 'ui-flight-attachment-upload',
  templateUrl: 'components/account-document-upload/account-document-upload.component.html'
})
export class FlightAttachmentUploadComponent extends FileUploadComponent implements OnInit {

  @Input() title: string;
  @Input() flightId: number;
  @Input() hint: string;

  @ViewChild('fileUpload') fileUploadEl: ElementRef;
  @Output() uploadError = new EventEmitter();
  @Output() beginUpload = new EventEmitter();

  constructor(protected fileService: FileService) {
    super(fileService);
  }

  ngOnInit(): void {
    if (!this.flightId) {
      throw new Error('Account ID is missing');
    }

    window.addEventListener('focus', (e) => {
      this.btnEnable();
    });
  }

  uploadService(data): Promise<any> {
    this.actionErrors = null;
    return this.fileService.attachmentUpload(data, this.flightId).catch(err => {
      this.actionErrors = ErrorHelperStatic.matchErrors(err).actionError || null;
      return false;
    }).then(() => {
      this.beginUpload.emit(true);
    });
  }
}
