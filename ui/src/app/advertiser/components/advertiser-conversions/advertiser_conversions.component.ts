import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FileService} from '../../../shared/services/file.service';
import {L10nConversionTypes} from '../../../common/L10n.const';
import {AgencyService} from '../../../agency/services/agency.service';
import {ConversionService} from '../../../conversion/services/conversion.service';
import {ConversionContainerModel} from '../../../conversion/models/conversion-container.model';
import {AdvertiserService} from '../../services/advertiser.service';
import {AdvertiserComponent} from '../advertiser/advertiser.component';
import {MatDialog} from '@angular/material/dialog';
import {ConversionPreviewComponent} from '../../../conversion/components/conversion-preview/conversion-preview.component';
import {UserSessionModel} from "../../../user/models/user-session.model";

@Component({
  selector: 'ui-advertiser-conversions',
  templateUrl: './advertiser_conversions.component.html',
  styleUrls: ['./advertiser_conversions.component.scss']
})
export class AdvertiserConversionsComponent extends AdvertiserComponent implements OnInit {

  conversionList: ConversionContainerModel[];
  public sessionModel: UserSessionModel;
  canCreateConversion: boolean;
  canUpdateConversions: boolean;
  wait2: boolean;
  readonly L10nConversionTypes = L10nConversionTypes;
  readonly displayedColumns = ['name', 'category', 'url', 'action'];

  constructor(protected advertiserService: AdvertiserService,
              protected agencyService: AgencyService,
              protected fileService: FileService,
              protected route: ActivatedRoute,
              private conversionsService: ConversionService,
              protected dialog: MatDialog) {
    super(advertiserService, agencyService, fileService, route, dialog);
    this.sessionModel = new UserSessionModel();
  }

  ngOnInit(): void {
    this.wait2 = true;
    super.ngOnInit();

    this.promise.then(data => Promise.all([
        this.conversionsService.getListByAdvertiserId(this.advertiser.id),
        this.advertiserService.isAllowedLocal(this.advertiser.id, 'advertiserEntity.create'),
        this.advertiserService.isAllowedLocal(this.advertiser.id, 'advertiserEntity.update')
      ])).then(res => {
      this.conversionList = res[0];
      this.canCreateConversion = res[1];
      this.canUpdateConversions = res[2];

      this.wait2 = false;
    });

  }

  deleteConversion(conversion: any): void {
    this.conversionsService
      .updateStatus(conversion.id, 'DELETE')
      .then(newStatus => {
        conversion.displayStatus = newStatus.split('|')[0];

        this.conversionList = this.conversionList.filter(c => c.conversion.id !== conversion.id);
      });
  }

  preview(conversion: any): void {
    this.dialog.open(ConversionPreviewComponent, {
      minWidth: 300,
      data: { conversion },
    });
  }
}
