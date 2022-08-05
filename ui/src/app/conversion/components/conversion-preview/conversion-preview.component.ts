import {Component, Inject, OnInit, Optional} from '@angular/core';
import {ConversionService} from '../../services/conversion.service';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'ui-conversion-preview',
  templateUrl: 'conversion-preview.component.html',
  styleUrls: ['./conversion-preview.component.scss']
})
export class ConversionPreviewComponent implements OnInit {
  wait: boolean;
  previewData;
  popupOptions;

  constructor(private conversionService: ConversionService,
              @Optional() @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    if (this.data.conversion) {
      this.previewData = null;
      this.wait = true;
      this.conversionService
        .getPixelCode(this.data.conversion.id)
        .then(conversion => {
          this.previewData = conversion;
          this.wait = false;
        });
    }
  }
}
