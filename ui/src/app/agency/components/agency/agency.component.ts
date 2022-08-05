import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';
import {AgencyService} from '../../services/agency.service';
import {FileService} from '../../../shared/services/file.service';
import {MatDialog} from '@angular/material/dialog';
import {AgencyDocumentsComponent} from '../agency-documents/agency-documents.component';

@Component({
  selector: 'ui-agency',
  templateUrl: './agency.component.html',
  styleUrls: ['./agency.component.scss']
})
export class AgencyComponent implements OnInit, OnDestroy {
  title: string;
  agency: AgencyModel;
  wait = true;
  canCreateUser: boolean;
  documentsViewAllowed: boolean;
  financeViewAllowed: boolean;
  protected titlePrefix: string;
  protected promise: Promise<any>;
  private routerSubscription: Subscription;

  constructor(protected agencyService: AgencyService,
              protected fileService: FileService,
              protected route: ActivatedRoute,
              protected dialog: MatDialog) {
  }


  ngOnInit(): void {
    this.onInit();
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  showDocuments(): void {
    this.dialog.open(AgencyDocumentsComponent, {
      data: {
        accountId: this.agency.id,
        accountName: this.agency.name,
      },
      minWidth: 400,
    });
  }

  protected onInit(): void {
    this.routerSubscription = this.route.params.subscribe(params => {
      const id = +params.id;

      this.promise = Promise.all([
        this.agencyService.getById(id),
        this.agencyService.isAllowedLocal(id, 'user.create'),
        this.agencyService.isAllowedLocal(id, 'account.viewAdvertisingDocuments'),
        this.agencyService.isAllowedLocal(id, 'account.viewAdvertisingFinance')
      ])
        .then(res => {
          this.agency = res[0];
          this.title = this.titlePrefix + this.agency.name;

          this.canCreateUser = res[1];
          this.documentsViewAllowed = res[2];
          this.financeViewAllowed = res[3];
          this.wait = false;
          return this.documentsViewAllowed ? this.fileService.checkDocuments(id) : Promise.resolve(false);
        });
    });
  }
}
