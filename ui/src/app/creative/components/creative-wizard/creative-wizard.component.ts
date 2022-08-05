import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AdvertiserService} from '../../../advertiser/services/advertiser.service';
import {AdvertiserModel, AdvertiserSessionModel} from '../../../advertiser/models';
import {Router} from '@angular/router';

@Component({
  selector: 'ui-creative-wizard',
  templateUrl: 'creative-wizard.component.html',
  styleUrls: ['./creative-wizard.component.scss'],
})
export class CreativeWizardComponent implements OnInit {

  wait: boolean;
  templateList: any[];
  template: any;
  advertiser: AdvertiserModel;
  sizeList: any;

  constructor(private advertiserService: AdvertiserService,
              private dialogRef: MatDialogRef<any>,
              private router: Router,
              @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    this.loadTemplates();
  }

  async loadTemplates(): Promise<any> {
    this.advertiser = new AdvertiserSessionModel().data;

    try {
      this.wait = true;
      this.templateList = await this.advertiserService.getTemplates(this.advertiser.id);
    } catch (e) {
      console.error(e);
    } finally {
      this.wait = false;
    }
  }

  async selectTemplate(template: any): Promise<any> {
    this.template = template;

    try {
      this.wait = true;
      this.sizeList = await this.advertiserService.getSizes(this.template.id, this.advertiser.id);
    } catch (e) {
      console.error(e);
    } finally {
      this.wait = false;
    }
  }

  selectSize(size: any): void {
    if (this.template && size) {
      this.router.navigate(
        ['/advertiser', this.advertiser.id, 'creative', 'new', 'template', this.template.id, 'size', size.id]
      ).then(() => {
        this.dialogRef.close();
      });
    }
  }
}
