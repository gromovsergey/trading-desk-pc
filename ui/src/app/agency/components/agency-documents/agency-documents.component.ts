import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {FileService} from '../../../shared/services/file.service';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'ui-agency-documents',
  templateUrl: './agency-documents.component.html',
  styleUrls: ['./agency-documents.component.scss']
})
export class AgencyDocumentsComponent implements OnInit {
  @ViewChild('downloadBtn') downloadBtnEl: ElementRef;
  wait: boolean;
  documents: any[];
  canUpdate: boolean;
  downloadName: string;
  downloadUrl: SafeUrl;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private fileService: FileService,
              private sanitizer: DomSanitizer) {
  }

  ngOnInit(): void {
    this.loadList();
  }

  async loadList(): Promise<any> {
    this.wait = true;
    try {
      this.documents = await this.fileService.getDocuments(this.data.accountId);
      this.canUpdate = await this.fileService.isAllowedLocal(this.data.accountId, 'account.updateAdvertisingDocuments');
    } catch (err) {
      console.error(err);
    } finally {
      this.wait = false;
    }
  }

  async downloadItem(item: string): Promise<any> {
    this.wait = true;
    try {
      const res = await this.fileService.downloadDocuments(this.data.accountId, item);
      this.downloadName = item;

      const reader = new FileReader();
      reader.readAsDataURL(res.target.response);
      reader.addEventListener('load', () => {
        this.downloadUrl = this.sanitizer.bypassSecurityTrustUrl(reader.result.toString());
        window.setTimeout(() => {
          this.downloadBtnEl.nativeElement.click();
        });
      });
    } catch (err) {
      console.error(err);
    } finally {
      this.wait = false;
    }
  }

  async deleteItem(item: string): Promise<any> {
    this.wait = true;
    await this.fileService.deleteDocuments(this.data.accountId, item);
    await this.loadList();
  }
}
