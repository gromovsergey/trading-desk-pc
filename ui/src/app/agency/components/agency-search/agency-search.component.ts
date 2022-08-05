import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {AuthService} from '../../../common/services/auth.service';
import {UserSessionModel} from '../../../user/models/user-session.model';
import {AgencyService} from '../../services/agency.service';
import {AgencySessionModel} from '../../models/agency-session.model';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AdvertiserSessionModel} from '../../../advertiser/models';
import {SEARCH_DEFAULT_VALUE} from '../../../const';

@Component({
  selector: 'ui-agency-search',
  templateUrl: './agency-search.component.html',
  styleUrls: ['./agency-search.component.scss']
})
export class AgencySearchComponent implements OnInit {

  searchForm = new FormGroup({
    name: new FormControl('', [Validators.required]),
    displayStatuses: new FormControl(SEARCH_DEFAULT_VALUE),
    country: new FormControl(''),
    accountRole: new FormControl(SEARCH_DEFAULT_VALUE),
  });
  filterState = false;
  wait = false;
  waitParams = false;
  agencyList: Array<any>;
  searchParams: AgencySearchParams;
  displayedColumns = ['account', 'imps', 'clicks', 'ctr', 'revenue'];

  readonly defaultValue = SEARCH_DEFAULT_VALUE;

  public constructor(private agencyService: AgencyService,
                     private router: Router,
                     private route: ActivatedRoute,
                     private authService: AuthService) {

    const user = new UserSessionModel();
    if (!user.isInternal()) {
      this.authService.navigateDefault(user.role, user.accountId);
    }
  }

  ngOnInit(): void {
    AgencySessionModel.clear();
    new AdvertiserSessionModel().clear();

    this.loadSearchParams().catch(err => console.error(err));
  }

  async search(): Promise<any> {
    this.agencyList = null;
    this.wait = true;

    try {
      const value = this.searchForm.value;
      if (!value.country) {
        delete value.country;
      }
      this.agencyList = await this.agencyService.search(value);
    } catch (err) {
      console.error(err);
    } finally {
      this.wait = false;
    }
  }

  async loadSearchParams(): Promise<any> {
    this.waitParams = true;
    this.searchParams = null;
    try {
      this.searchParams = await this.agencyService.getAccountSearchParams();
    } finally {
      this.waitParams = false;
    }
  }
}
