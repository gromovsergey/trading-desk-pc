import { Component, OnInit }            from '@angular/core';
import { RouterModule, ActivatedRoute } from '@angular/router';

import { FileService }              from '../shared/file.service';
import { LoadingComponent }         from '../shared/loading.component';
import { IconComponent }            from '../shared/icon.component';
import { DisplayStatusDirective }   from '../shared/display_status.directive';
import { L10nConversionTypes }          from '../common/L10n.const';
import { AgencyService }            from '../agency/agency.service';
import { ConversionService }        from '../conversion/conversion.service';
import { ConversionPreview }        from '../conversion/conversion_preview.component';
import { ConversionContainerModel } from '../conversion/conversion.container.model';

import { AdvertiserService }        from './advertiser.service';
import { AdvertiserComponent }      from './advertiser.component';


@Component({
    selector: 'ui-advertiser-conversions',
    templateUrl: 'conversions.html'
})

export class AdvertiserConversionsComponent extends AdvertiserComponent implements OnInit {

    private conversionList:Array<ConversionContainerModel>;

    protected titlePrefix: string;

    private conversionPreview;
    private canCreateConversion: boolean;
    private canUpdateConversions: boolean;
    public _wait: boolean;
    public L10nConversionTypes = L10nConversionTypes;

    constructor(protected advertiserService: AdvertiserService,
                protected agencyService: AgencyService,
                protected fileService: FileService,
                protected route: ActivatedRoute,
                private conversionsService: ConversionService) {
        super(advertiserService, agencyService, fileService, route);
    }

    protected initResources(): void {
        this.titlePrefix = '_L10N_(advertiserAccount.accountConversions)' + ': ';
    }

    ngOnInit() {
        this._wait  = true;
        this.onInit();

        this.promise.then(data => {
            return Promise.all([
                this.conversionsService.getListByAdvertiserId(this.advertiser.id),
                this.advertiserService.isAllowedLocal(this.advertiser.id, 'advertiserEntity.create'),
                this.advertiserService.isAllowedLocal(this.advertiser.id, 'advertiserEntity.update')
            ]);
        }).then(res  => {
            this.conversionList = res[0];
            this.canCreateConversion = res[1];
            this.canUpdateConversions = res[2];

            this._wait  = false;
        });

    }

    private deleteConversion(e: any, conversion: any){
        this.conversionsService
            .updateStatus(conversion.id, 'DELETE')
            .then(newStatus => {
                conversion.displayStatus = newStatus.split('|')[0];

                this.conversionList   = this.conversionList.filter(c => {
                    return c.conversion.id !== conversion.id;
                });
            });
    }

    private preview(e: any, conversion: any) {
        e.preventDefault();
        this.conversionPreview        = conversion;
    }

    private onPreviewClose(e?: any) {
        this.conversionPreview        = null;
    }

}
