import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AgencyService} from '../../../agency/services/agency.service';
import {AgencySessionModel} from '../../../agency/models/agency-session.model';
import {AdvertiserService} from '../../services/advertiser.service';
import {FileService} from '../../../shared/services/file.service';
import {Subscription} from 'rxjs';
import {AdvertiserModel} from '../../models';
import {filter, map} from 'rxjs/operators';
import {AgencyDocumentsComponent} from '../../../agency/components/agency-documents/agency-documents.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'ui-advertiser',
  templateUrl: './advertiser.component.html',
  styleUrls: ['./advertiser.component.scss']
})
export class AdvertiserComponent implements OnInit, OnDestroy {

  title: string;
  advertiser: AdvertiserModel;
  agency: AgencyModel;
  promise: Promise<any>;
  wait = true;
  routerSubscription: Subscription;
  showUserList: boolean;
  documentsViewAllowed: boolean;
  canUpdate: boolean;
  canCreateUser: boolean;
  canViewFinance: boolean;
  documents: boolean;
  documentsExist: boolean;

  constructor(protected advertiserService: AdvertiserService,
              protected agencyService: AgencyService,
              protected fileService: FileService,
              protected route: ActivatedRoute,
              protected dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.routerSubscription = this.route.paramMap.pipe(
      filter(paramMap => !!paramMap.get('id')),
      map(paramMap => +paramMap.get('id'))
    ).subscribe(id => {
      this.promise = this.loadAdvertiser(id);
    });
  }

  async loadAdvertiser(advertiserId: number): Promise<any> {
    this.wait = true;
    try {
      this.advertiser = await this.advertiserService.getById(advertiserId).toPromise();
      await this.loadRestrictions(this.advertiser ? this.advertiser.agencyId : null);
      if (this.documentsViewAllowed && this.advertiser) {
        this.documentsExist = await this.fileService.checkDocuments(this.advertiser.id);
      }
    } catch (err) {
      console.error(err);
    } finally {
      this.wait = false;
    }
  }

  async loadRestrictions(agencyId: number): Promise<any> {
    try {
      const restrictions = await Promise.all([
        agencyId ? this.agencyService.getById(agencyId) : Promise.resolve(null),
        this.agencyService.isAllowedLocal(this.advertiser.id, 'account.viewAdvertisingDocuments'),
        this.agencyService.isAllowedLocal(this.advertiser.id, 'account.updateAdvertising'),
        this.advertiserService.isAllowedLocal(this.advertiser.id, 'user.create'),
        this.advertiserService.isAllowedLocal(this.advertiser.id, 'account.viewAdvertisingFinance')
      ]);

      this.agency = restrictions[0];
      this.documentsViewAllowed = restrictions[1];
      this.canUpdate = restrictions[2];
      this.canCreateUser = restrictions[3];
      this.canViewFinance = restrictions[4];

      if (this.agency === null) {
        AgencySessionModel.clear();
        this.showUserList = true;
      } else {
        this.showUserList = false;
      }
    } catch (err) {
      console.error(err);
    }
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  showDocuments(): void {
    this.dialog.open(AgencyDocumentsComponent, {
      data: {
        accountId: this.advertiser.id,
        accountName: this.advertiser.name,
      },
      minWidth: 400,
    });
  }
}
