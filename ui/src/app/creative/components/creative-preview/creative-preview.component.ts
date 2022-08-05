import {Component, ViewChild, ElementRef, Inject, OnInit} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {CreativeService} from '../../services/creative.service';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'ui-creative-preview',
  templateUrl: 'creative-preview.component.html',
  styleUrls: ['./creative-preview.component.scss'],
})
export class CreativePreviewComponent implements OnInit {
  @ViewChild('frame') creativeIframeEl: ElementRef;

  wait: boolean;
  previewData;

  get creative(): any {
    return this.data && this.data.creative ? this.data.creative : null;
  }

  constructor(private creativeService: CreativeService,
              private sanitizer: DomSanitizer,
              @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    if (this.creative) {
      if (this.creative.creativeName !== undefined) {
        this.creative.name = this.creative.creativeName;
      }
      this.previewData = null;
      this.wait = true;
      this.creativeService
        .getPreviewUrl(this.creative.creativeId)
        .then(previewData => {
          previewData.secureUrl = this.sanitizer.bypassSecurityTrustResourceUrl(previewData.url);

          this.previewData = previewData;
          this.wait = false;
        });
    } else {
      // Prevent IFRAME refresh on removing #345
      if (this.creativeIframeEl) {
        this.creativeIframeEl.nativeElement.src = 'about:blank';
      }
    }
  }
}
