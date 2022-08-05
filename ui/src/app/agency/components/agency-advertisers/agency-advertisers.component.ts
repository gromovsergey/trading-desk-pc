import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';
import {AdvertiserService} from '../../../advertiser/services/advertiser.service';
import {AgencyService} from '../../services/agency.service';
import {FileService} from '../../../shared/services/file.service';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {UserSessionModel} from "../../../user/models/user-session.model";


@Component({
  selector: 'ui-agency-advertisers',
  templateUrl: './agency-advertisers.component.html',
  styleUrls: ['./agency-advertisers.component.scss']
})
export class AgencyAdvertisersComponent implements OnInit, OnDestroy {

  agency: AgencyModel;
  advertiserList: Array<any>;
  routerSubscription: Subscription;
  wait = true;
  canCreateAdvertiser: boolean;
  canUpdateAdvertisers: boolean;
  public displayedColumns: string[];
  public sessionModel: UserSessionModel;

  constructor(private agencyService: AgencyService,
              private advertiserService: AdvertiserService,
              private fileService: FileService,
              private route: ActivatedRoute) {
    this.sessionModel = new UserSessionModel();
  }

  ngOnInit(): void {
    this.routerSubscription = this.route.params.subscribe(params => {
      this.agencyService
        .getById(+params.id)
        .then(agency => {
          this.agency = agency;
          new AdvertiserSessionModel().clear();

          return Promise.all([
            this.advertiserService.getListByAgencyId(agency.id),
            this.agencyService.isAllowedLocal(agency.id, 'account.createAdvertiserInAgency'),
            this.agencyService.isAllowedLocal(agency.id, 'account.updateAdvertisersInAgency')
          ]);
        })
        .then(res => {
          this.advertiserList = res[0];

          this.canCreateAdvertiser = res[1];
          this.canUpdateAdvertisers = res[2];
          this.setDisplayedColumns();
          this.wait = false;
        });
    });
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  changeStatus(advertiser: any): void {
    this.advertiserService
      .updateStatus(advertiser.advertiserId, advertiser.statusChangeOperation)
      .then(updatedAdvertiser => {
        advertiser.displayStatus = updatedAdvertiser.displayStatus.split('|')[0];
      });
  }

  deleteAdvertiser(e: any, advertiser: any): void {
    this.advertiserService
      .updateStatus(advertiser.advertiserId, 'DELETE')
      .then(updatedAdvertiser => {
        advertiser.displayStatus = updatedAdvertiser.displayStatus.split('|')[0];

        this.advertiserList = this.advertiserList.filter(adv => adv.advertiserId !== advertiser.advertiserId);
      });
  }

  private setDisplayedColumns(): void {
    this.displayedColumns = ['advertiser', 'imps', 'clicks', 'ctr', 'totalCost', 'ecpm'];
    this.canUpdateAdvertisers ? this.displayedColumns.push('action') : null;
  }
}
