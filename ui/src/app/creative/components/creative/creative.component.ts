import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';
import {AdvertiserService} from '../../../advertiser/services/advertiser.service';
import {AgencyService} from '../../../agency/services/agency.service';
import {CreativeService} from '../../services/creative.service';
import {FileService} from '../../../shared/services/file.service';
import {AdvertiserComponent} from '../../../advertiser/components/advertiser/advertiser.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'ui-creative',
  templateUrl: 'creative.component.html'
})
export class CreativeComponent extends AdvertiserComponent implements OnInit, OnDestroy {

  title: string;
  creative: Creative;
  private creativeRouterSubscription: Subscription;

  constructor(protected advertiserService: AdvertiserService,
              protected agencyService: AgencyService,
              protected fileService: FileService,
              protected route: ActivatedRoute,
              private creativeService: CreativeService,
              protected dialog: MatDialog) {
    super(advertiserService, agencyService, fileService, route, dialog);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.creativeRouterSubscription = this.route.params.subscribe(params => {
      this.promise = this.creativeService
        .getCreative(+params.creativeId)
        .then(creative => {
          this.creative = creative;
          this.title = 'Creative: ' + creative.name;
          this.wait = false;
        });
    });
  }

  ngOnDestroy(): void {
    if (this.creativeRouterSubscription) {
      this.creativeRouterSubscription.unsubscribe();
    }
  }
}
