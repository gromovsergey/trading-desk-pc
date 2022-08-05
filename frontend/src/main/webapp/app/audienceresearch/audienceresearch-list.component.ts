import {Component, OnInit} from '@angular/core';

import {moment, dateFormatShort} from '../common/common.const';
import {PageComponent} from '../shared/page.component';
import {AudienceResearchService} from "./audienceresearch.service";

@Component({
    selector: 'ui-audience-research-list',
    templateUrl: 'list.html'
})

export class AudienceResearchListComponent extends PageComponent implements OnInit {
    public wait: boolean;
    public audienceResearchList: Array<any>;

    public moment = moment;
    public dateFormatShort = dateFormatShort;

    public canUpdateResearch: boolean;

    constructor(private audienceResearchService: AudienceResearchService) {
        super();
        this.initResources();
    }

    private initResources(): void {
        this.title = '_L10N_(audienceResearch.list)';
    }

    ngOnInit() {
        this.wait = true;

        Promise.all([
            this.audienceResearchService.getAudienceResearches(),
            this.audienceResearchService.isAllowedLocal0('audienceResearch.edit')
        ]).then(res => {
            this.audienceResearchList = res[0];
            this.canUpdateResearch = res[1];
            this.wait = false;
        });
    }

    public delete(e: any, id: number) {
        if (!confirm('_L10N_(audienceResearch.deleteConfirm)')) {
            return;
        }
        this.audienceResearchService.delete(id)
            .then(res => {
                this.audienceResearchList = this.audienceResearchList.filter(v => v.id !== id);
            });
    }
}