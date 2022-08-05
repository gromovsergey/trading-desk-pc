import {Component, Input, OnChanges} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {CreativeService} from '../../services/creative.service';

@Component({
  selector: 'ui-creative-live-preview',
  templateUrl: 'creative-live-preview.component.html',
  styleUrls: ['./creative-live-preview.component.scss']
})
export class CreativeLivePreviewComponent implements OnChanges {

  @Input() creative: Creative;
  @Input() rnd: number;

  wait: boolean;
  preview;

  constructor(private creativeService: CreativeService,
              private sanitizer: DomSanitizer) {
  }

  ngOnChanges(): void {
    if (!this.wait) {
      this.updatePreview();
    }
  }

  updatePreview(): void {
    this.wait = true;
    this.creativeService.getLivePreview(this.creative)
      .then(preview => {
        this.preview = preview;
        this.preview.secureUrl = this.sanitizer.bypassSecurityTrustResourceUrl(preview.url);
        this.wait = false;
      })
      .catch(() => {
        this.wait = false;
        this.preview = null;
      });
  }
}
