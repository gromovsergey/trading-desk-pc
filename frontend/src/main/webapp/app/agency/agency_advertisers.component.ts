import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs/Rx';
import {PageComponent} from '../shared/page.component';
import {AdvertiserService} from '../advertiser/advertiser.service';
import {AdvertiserSessionModel} from '../advertiser/advertiser_session.model';
import {AgencyService} from './agency.service';
import {AgencyModel} from './agency.model';
import {FileService} from '../shared/file.service';


@Component({
    selector: 'ui-agency-advertisers',
    templateUrl: 'advertisers.html'
})

export class AgencyAdvertisersComponent extends PageComponent implements OnInit, OnDestroy {

    private titlePrefix: string = 'Account Summary by Advertiser: ';
    private agency: AgencyModel;
    private advertiserList: Array<any>;
    private routerSubscription: Subscription;
    public wait: boolean = true;
    private canCreateAdvertiser: boolean;
    private canUpdateAdvertisers: boolean;

    constructor(private agencyService: AgencyService,
                private advertiserService: AdvertiserService,
                private fileService: FileService,
                private route: ActivatedRoute){
        super();

        this.titlePrefix = '_L10N_(agencyAccount.advertisersSummary)' + ': ';
        if (this.agency) {
            this.initTitle();
        }
    }

    ngOnInit(){
        this.routerSubscription   = this.route.params.subscribe(params => {
            this.agencyService
                .getById(+params['id'])
                .then(agency => {
                    this.agency = agency;
                    this.initTitle();

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

                    this.wait   = false;
                });
        });
    }

    private initTitle() {
        this.title = this.titlePrefix + this.agency.name;
    }

    ngOnDestroy(){
        if (this.routerSubscription){
            this.routerSubscription.unsubscribe();
        }
    }

    private changeStatus(advertiser: any){
        this.advertiserService
            .updateStatus(advertiser.advertiserId, advertiser.statusChangeOperation)
            .then(updatedAdvertiser => {
                advertiser.displayStatus = updatedAdvertiser.displayStatus.split('|')[0];
            });
    }

    private deleteAdvertiser(e: any, advertiser: any){
        this.advertiserService
            .updateStatus(advertiser.advertiserId, 'DELETE')
            .then(updatedAdvertiser => {
                advertiser.displayStatus = updatedAdvertiser.displayStatus.split('|')[0];

                this.advertiserList   = this.advertiserList.filter(adv => {
                    return adv.advertiserId !== advertiser.advertiserId;
                });
            });
    }
}
