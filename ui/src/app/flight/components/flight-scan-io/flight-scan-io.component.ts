import {Component, ElementRef, ViewChild, Inject, OnInit} from '@angular/core';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {FlightService} from '../../services/flight.service';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'ui-flight-scan-io',
  templateUrl: 'flight-scan-io.component.html',
  styleUrls: ['./flight-scan-io.component.scss']
})
export class FlightScanIOComponent implements OnInit {
  @ViewChild('downloadBtn') downloadBtnEl: ElementRef;
  wait: boolean;
  attachments = [];
  options: any;
  errors: any;
  downloadUrl: SafeUrl;
  downloadName: string;

  constructor(private flightService: FlightService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private sanitizer: DomSanitizer) {
  }

  ngOnInit(): void {
    this.loadList();
  }

  loadList(): any {
    this.wait = true;
    return this.flightService.getAttachments(this.data.flightId)
      .then(list => {
        this.attachments = list;
        this.wait = false;
      });
  }

  downloadItem(item: string): void {
    this.wait = true;
    this.flightService.downloadAttachments(this.data.flightId, item).then(res => {
      this.downloadName = item;
      if (navigator && navigator.msSaveBlob) {
        // ie hack
        navigator.msSaveBlob(res.target.response, this.downloadName);
      } else {
        const reader = new FileReader();
        reader.readAsDataURL(res.target.response);
        reader.addEventListener('load', () => {
          this.downloadUrl = this.sanitizer.bypassSecurityTrustUrl(reader.result.toString());
          window.setTimeout(() => {
            this.downloadBtnEl.nativeElement.click();
          });
        });
      }
      this.wait = false;
    });
  }

  deleteItem(item: string): void {
    this.wait = true;
    this.flightService.deleteAttachments(this.data.flightId, item).then(() => {
      this.loadList();
    });
  }
}
