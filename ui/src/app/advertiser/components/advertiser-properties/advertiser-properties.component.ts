import {Component, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {country} from '../../../common/country.const';
import {L10nMajorStatuses, L10nCountries, L10nTimeZones} from '../../../common/L10n.const';
import {AdvertiserService} from '../../services/advertiser.service';
import {AdvertiserModel, AdvertiserSessionModel} from '../../models';
import {ThemePalette} from '@angular/material/core/common-behaviors/color';

@Component({
  selector: 'ui-advertiser-properties',
  templateUrl: './advertiser-properties.component.html',
  styleUrls: ['./advertiser-properties.component.scss']
})
export class AdvertiserPropertiesComponent implements OnInit {

  @Input() accountId: number;
  @Input() statusChangeable: boolean;
  @Input() canViewFinance: boolean;

  advertiser: AdvertiserModel = new AdvertiserSessionModel().data;
  L10nMajorStatuses = L10nMajorStatuses;
  L10nCountries = L10nCountries;
  L10nTimeZones = L10nTimeZones;
  currencyCode = new AdvertiserSessionModel().currencyCode;
  spentBudget = 0;
  showBudget: boolean;
  progressColor: ThemePalette = 'primary';
  progressValue = 0;
  private countries = country;
  private country;

  constructor(private advertiserService: AdvertiserService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.country = this.countries.find(t => t.code === this.advertiser.countryCode);

    if (!this.advertiser.agencyId) {
      this.statusChangeable = false;
    }

    this.advertiserService.getAvailableBudget(this.advertiser.id)
      .then(res => {
        if (res) {
          this.showBudget = true;
          this.spentBudget = this.advertiser.prepaidAmount - res;
          if (this.advertiser.prepaidAmount) {
            const value = Math.ceil(this.spentBudget / this.advertiser.prepaidAmount * 100);
            this.progressColor = value > 100 ? 'accent' : 'primary';
            this.progressValue = value;
          } else {
            this.progressValue = 0;
          }
        } else {
          this.showBudget = false;
        }
      });
  }

  statusChange(name: any): void {
    this.advertiserService.updateStatus(this.accountId, name)
      .then(updatedAdvertiser => {
        this.advertiser.displayStatus = updatedAdvertiser.displayStatus;
        if (this.advertiser.displayStatus === 'DELETED') {
          this.router.navigate(['/agency', this.advertiser.agencyId, 'advertisers']);
        }
      });
  }
}
