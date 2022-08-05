import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs/Rx';
import {PageComponent} from '../shared/page.component';
import {AgencyService} from './agency.service';
import {AgencyModel} from './agency.model';
import {FileService} from "../shared/file.service";


@Component({
    selector: 'ui-agency',
    templateUrl: 'index.html'
})

export class AgencyComponent extends PageComponent implements OnInit, OnDestroy {

    public title: string;
    protected titlePrefix: string;
    protected agency: AgencyModel;
    public wait: boolean = true;
    protected promise: Promise<any>;

    private routerSubscription:Subscription;
    private canCreateUser: boolean;

    private documents: boolean;
    private documentsViewAllowed: boolean;
    public financeViewAllowed: boolean;
    private documentsExist: boolean;

    constructor(protected agencyService: AgencyService,
                protected fileService: FileService,
                protected route: ActivatedRoute){
        super();
        this.initResources();
    }

    protected initResources(): void {
        this.title = '_L10N_(agencyAccount.agencyAccount)';
        this.titlePrefix = '_L10N_(accountSearch.account)' + ': ';
    }

    ngOnInit(){
        this.onInit();
    }

    protected onInit(){
        this.routerSubscription   = this.route.params.subscribe(params => {
            let id  = +params['id'];

            this.promise = Promise.all([
                this.agencyService.getById(id),
                this.agencyService.isAllowedLocal(id, 'user.create'),
                this.agencyService.isAllowedLocal(id, 'account.viewAdvertisingDocuments'),
                this.agencyService.isAllowedLocal(id, 'account.viewAdvertisingFinance')
            ])
            .then(res => {
                this.agency = res[0];
                this.title  = this.titlePrefix + this.agency.name;

                this.canCreateUser = res[1];
                this.documentsViewAllowed = res[2];
                this.financeViewAllowed = res[3];

                return this.documentsViewAllowed ? this.fileService.checkDocuments(id) : Promise.resolve(false);
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
        this.fileService.getDocuments(this.agency.id).then(list => {
            this.documentsExist = list.length > 0;
        });
        this.documents = false;
    }
}
