import {Component, Input, ViewChild, ElementRef, Output, EventEmitter, OnInit, HostListener} from '@angular/core';
import {MAX_UPLOAD_FILE_SIZE} from '../common/common.const';
import {FileService} from './services/file.service';
import {ErrorHelperStatic} from './static/error-helper.static';

@Component({
  selector: 'ui-file-upload',
  templateUrl: 'components/account-document-upload/account-document-upload.component.html'
})
export class FileUploadComponent implements OnInit {

  @Input() title = 'Upload File';
  @Input() clearBtnTitle = 'Clear';
  @Input() name = 'file';
  @Input() accountId: number;
  @Input() value: string;
  @Output() beginUpload = new EventEmitter();
  @Output() uploadError = new EventEmitter();
  @ViewChild('fileUpload') fileUploadEl: ElementRef;

  wait = false;
  hint = '';
  errors;
  actionErrors;
  btnDisable = false;
  uploadData = {name: null, url: null};

  constructor(protected fileService: FileService) {
  }

  @HostListener('window:focus')
  btnEnable(): void {
    this.btnDisable = false;
  }

  ngOnInit(): void {
    if (!this.accountId) {
      throw new Error('Account ID is missing');
    }

    if (this.value) {
      this.uploadData.name = this.value;
    }
  }

  selectFile(e: any): void {
    e.preventDefault();

    this.fileUploadEl.nativeElement.click();
  }

  doUpload(e: any): void {
    const el = this.fileUploadEl.nativeElement;
    this.errors = [];

    if (el.files.length === 0) {
      return;
    }

    if (el.files[0].size > MAX_UPLOAD_FILE_SIZE) {
      this.errors.push('File size should not exceed ' + Math.round(MAX_UPLOAD_FILE_SIZE / (1024 * 1024)) + 'Mb');
      return;
    }

    if (el.files.length > 1) {
      this.errors.push('Can\'t upload several files. Only first one will be uploaded');
    }

    const formData = new FormData();
    formData.append(this.name, el.files[0]);

    this.wait = true;
    this.btnDisable = true;
    this.uploadService(formData)
      .then(data => {
        this.wait = false;
        this.btnEnable();
        this.uploadData = data;
        this.beginUpload.emit(data.name);
      })
      .catch(err => {
        this.wait = false;
        this.btnEnable();
        this.errors = ErrorHelperStatic.matchErrors(err) || [];
        this.uploadError.emit(err);
      });
  }

  uploadService(data: any): Promise<any> {
    return this.fileService.creativeUpload(data, this.accountId);
  }

  clearField(e: any): void {
    e.preventDefault();
    this.uploadData = null;
    this.beginUpload.emit(null);
  }
}
