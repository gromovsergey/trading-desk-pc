import {Component, Input, OnInit} from '@angular/core';
import {country} from '../../../common/country.const';
import {L10nMajorStatuses, L10nCountries, L10nTimeZones} from '../../../common/L10n.const';
import {UserSessionModel} from '../../../user/models/user-session.model';
import {AgencyService} from '../../services/agency.service';
import {AgencySessionModel} from '../../models/agency-session.model';
import {ThemePalette} from '@angular/material/core/common-behaviors/color';

@Component({
  selector: 'ui-agency-properties',
  templateUrl: './agency-properties.component.html',
  styleUrls: ['./agency-properties.component.scss']
})
export class AgencyPropertiesComponent implements OnInit {

  @Input() agencyId: number;
  @Input() showSelfServiceCommission: boolean;

  agency: AgencyModel = new AgencySessionModel().data;
  countries = country;
  country;
  currencyCode = new AgencySessionModel().currencyCode;
  spentBudget = 0;
  showBudget = true;
  progressColor: ThemePalette = 'primary';
  progressValue = 0;

  L10nMajorStatuses = L10nMajorStatuses;
  L10nCountries = L10nCountries;
  L10nTimeZones = L10nTimeZones;

  constructor(protected agencyService: AgencyService) {
  }

  ngOnInit(): void {
    const user = new UserSessionModel();
    this.country = this.countries.find(t => (t.code === this.agency.countryCode));

    this.agencyService.getAvailableBudget(this.agency.id)
      .then(res => {
        if (res) {
          this.showBudget = true;
          this.spentBudget = this.agency.prepaidAmount - res;
          if (this.agency.prepaidAmount) {
            const value = Math.ceil(this.spentBudget / this.agency.prepaidAmount * 100);
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
}
