import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import { CurrencyPipe }                                    from '@angular/common';
import { Router }                                          from '@angular/router';

import { IconComponent }                from '../shared/icon.component';
import { DisplayStatusDirective }       from '../shared/display_status.directive';
import { CellComponent }                from '../shared/cell.component';
import { DisplayStatusButtonComponent } from '../shared/display_status_button.component';
import { country }                      from "../common/country.const";
import { L10nMajorStatuses, L10nCountries, L10nTimeZones } from '../common/L10n.const';
import { UserSessionModel }             from '../user/user_session.model';

import { AdvertiserService }            from './advertiser.service';
import { AdvertiserModel }              from './advertiser.model';
import { AdvertiserSessionModel }       from './advertiser_session.model';

@Component({
    selector: 'ui-advertiser-properties',
    templateUrl: 'properties.html'
})

export class AdvertiserPropertiesComponent implements OnInit {

    @Input() accountId: number;
    @Input() statusChangeable: boolean;
    @Input() canViewFinance: boolean;
    @ViewChild('budgetBar') budgetBarEl: ElementRef;

    public advertiser: AdvertiserModel = new AdvertiserSessionModel().data;
    public L10nMajorStatuses = L10nMajorStatuses;
    public L10nCountries = L10nCountries;
    public L10nTimeZones = L10nTimeZones;
    public currencyCode = new AdvertiserSessionModel().currencyCode;
    private countries   = country;
    private country;
    public spentBudget: number = 0;
    public showBudget: boolean = true;

    constructor(private advertiserService: AdvertiserService,
                private router: Router){}

    ngOnInit(){
        this.country    = this.countries.find(t => {
            if (t.code  === this.advertiser.countryCode){
                return true;
            }
            return false;
        });

        if (!this.advertiser.agencyId){
            this.statusChangeable = false;
        }

        this.advertiserService.getAvailableBudget(this.advertiser.id)
            .then(res => {
                if (!res) {
                    this.showBudget = false;
                    return;
                }

                this.showBudget = true;
                this.spentBudget = this.advertiser.prepaidAmount - res;
                if (this.advertiser.prepaidAmount) {
                    let value = Math.ceil(this.spentBudget / this.advertiser.prepaidAmount * 100);
                    if (value > 100) {
                        this.budgetBarEl.nativeElement.classList.add("progress-bar-danger");
                    }
                    this.budgetBarEl.nativeElement.style.width = value + '%';
                } else {
                    this.budgetBarEl.nativeElement.style.width  = '0px';
                }
            });
    }

    public statusChange(name: any){
        this.advertiserService.updateStatus(this.accountId, name)
            .then(updatedAdvertiser => {
                this.advertiser.displayStatus = updatedAdvertiser.displayStatus;
                if (this.advertiser.displayStatus === 'DELETED') {
                    this.router.navigateByUrl(`/agency/${this.advertiser.agencyId}/advertisers`);
                }
            });
    }
}
