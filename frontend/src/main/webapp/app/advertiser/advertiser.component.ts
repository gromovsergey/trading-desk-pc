import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs/Rx';
import {PageComponent} from '../shared/page.component';
import {AgencyService} from '../agency/agency.service';
import {AgencyModel} from '../agency/agency.model';
import {AgencySessionModel} from '../agency/agency_session.model';
import {AdvertiserService} from './advertiser.service';
import {AdvertiserSessionModel} from './advertiser_session.model';
import {AdvertiserModel} from './advertiser.model';
import {FileService} from "../shared/file.service";

@Component({
    selector: 'ui-advertiser',
    templateUrl: 'index.html'
})

export class AdvertiserComponent extends PageComponent implements OnInit, OnDestroy {

    public title: string;
    protected titlePrefix: string;

    protected advertiserSession: AdvertiserSessionModel = new AdvertiserSessionModel();
    protected advertiser:AdvertiserModel;
    protected agency:AgencyModel;
    protected promise: Promise<any>;
    public wait: boolean   = true;
    private routerSubscription:Subscription;
    private showUserList: boolean;
    private documentsViewAllowed: boolean;
    private canUpdate: boolean;
    private canCreateUser: boolean;
    public canViewFinance: boolean;

    private documents: boolean;
    private documentsExist: boolean;

    constructor(protected advertiserService: AdvertiserService,
                protected agencyService: AgencyService,
                protected fileService: FileService,
                protected route: ActivatedRoute){
        super();
        this.initResources();
    }

    protected initResources(): void {
        this.title = '_L10N_(advertiserAccount.advertiserAccount)';
        this.titlePrefix = '_L10N_(advertiserAccount.advertiserAccount)' + ': ';
    }

    ngOnInit(){
        this.onInit();
    }

    protected onInit() {
        this.routerSubscription = this.route.params.subscribe(params => {
            this.promise = this.advertiserService.getById(+params['id'])
                .then(advertiser => {
                    this.advertiser = advertiser;
                    this.title = this.titlePrefix + advertiser.name;
                    let isStandalone = this.advertiser.agencyId == null;

                    return Promise.all([
                        isStandalone ? Promise.resolve(null) : this.agencyService.getById(advertiser.agencyId),
                        this.agencyService.isAllowedLocal(this.advertiser.id, 'account.viewAdvertisingDocuments'),
                        this.agencyService.isAllowedLocal(this.advertiser.id, 'account.updateAdvertising'),
                        this.advertiserService.isAllowedLocal(this.advertiser.id, 'user.create'),
                        this.advertiserService.isAllowedLocal(this.advertiser.id, 'account.viewAdvertisingFinance')
                    ]);
                })
                .then(res => {
                    this.agency = res[0];
                    this.documentsViewAllowed = res[1];
                    this.canUpdate = res[2];
                    this.canCreateUser = res[3];
                    this.canViewFinance = res[4];

                    if (this.agency == null) {
                        new AgencySessionModel().clear();
                        this.showUserList = true;
                    } else {
                        this.showUserList = false;
                    }

                    return this.documentsViewAllowed ? this.fileService.checkDocuments(this.advertiser.id) : Promise.resolve(false);
                })
                .then( documentsExist => {
                    this.documentsExist = documentsExist;
                    this.wait = false;
                });
        });
    }

    ngOnDestroy(){
        if (this.routerSubscription){
            this.routerSubscription.unsubscribe();
        }
    }

    private showDocuments(e: any){
        this.documents = true;
    }

    private onDocumentsClose(e: any){
        this.fileService.getDocuments(this.advertiser.id).then(list => {
            this.documentsExist = list.length > 0;
        });
        this.documents = false;
    }
}
