import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';

import { country }                      from '../common/country.const';
import { L10nMajorStatuses, L10nCountries, L10nTimeZones } from '../common/L10n.const';
import { UserSessionModel }             from '../user/user_session.model';

import { AgencyService }      from './agency.service';
import { AgencyModel }        from './agency.model';
import { AgencySessionModel } from './agency_session.model';

@Component({
    selector: 'ui-agency-properties',
    templateUrl: 'properties.html'
})
export class AgencyPropertiesComponent implements OnInit{

    @Input() agencyId: number;
    @Input() showSelfServiceCommission: boolean;
    @ViewChild('budgetBar') budgetBarEl: ElementRef;

    public agency: AgencyModel = new AgencySessionModel().data;
    private countries = country;
    private country;
    public currencyCode = new AgencySessionModel().currencyCode;
    public spentBudget: number = 0;
    public showBudget: boolean = true;

    public L10nMajorStatuses = L10nMajorStatuses;
    public L10nCountries = L10nCountries;
    public L10nTimeZones = L10nTimeZones;

    constructor(protected agencyService: AgencyService) {
    }

    ngOnInit() {
        let user = new UserSessionModel();
        this.country = this.countries.find(t => {
            return (t.code === this.agency.countryCode);
        })

        this.agencyService.getAvailableBudget(this.agency.id)
            .then(res => {
                if (!res) {
                    this.showBudget = false;
                    return;
                }

                this.showBudget = true;
                this.spentBudget = this.agency.prepaidAmount - res;
                if (this.agency.prepaidAmount) {
                    let value = Math.ceil(this.spentBudget / this.agency.prepaidAmount * 100);
                    if (value > 100) {
                        this.budgetBarEl.nativeElement.classList.add("progress-bar-danger");
                    }
                    this.budgetBarEl.nativeElement.style.width = value + '%';
                } else {
                    this.budgetBarEl.nativeElement.style.width  = '0px';
                }
            });
    }
}
