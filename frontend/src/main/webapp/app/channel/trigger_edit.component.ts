import { Component, Input, OnInit } from '@angular/core';
import { RouterModule }             from '@angular/router';
import { FormsModule }              from '@angular/forms';

import { PanelComponent }       from '../shared/panel.component';
import { BehavioralParameters } from './behavioral_parameters.model';
import { BehavioralChannel }    from './behavioral_channel.model';
import { Triggers }             from './trigger.model';

@Component({
    selector: 'ui-triggers-edit',
    templateUrl: 'edit_triggers.html'
})

export class TriggersEditComponent implements OnInit {
    @Input() channel: BehavioralChannel;
    @Input() errors;
    @Input() type: string; // P, S, U, R

    public sectionName: string;
    public triggers: Triggers;
    public behavioralParameters: BehavioralParameters;

    public visitsValues: Array<number>;
    public daysValues: Array<number>;

    constructor(){
    }

    ngOnInit() {
        switch (this.type) {
            case 'P':
                this.sectionName = '_L10N_(channel.blockName.keywords.pageKeywords)';
                this.triggers = this.channel.pageKeywords;
                break;
            case 'S':
                this.sectionName = '_L10N_(channel.blockName.keywords.searchKeywords)';
                this.triggers = this.channel.searchKeywords;
                break;
            case 'U':
                this.sectionName = '_L10N_(channel.blockName.keywords.urls)';
                this.triggers = this.channel.urls;
                break;
            case 'R':
                this.sectionName = '_L10N_(channel.blockName.keywords.urlKeywords)';
                this.triggers = this.channel.urlKeywords;
                break;
        }
        this.behavioralParameters = this.channel.behavioralParameters.find(bp => bp.triggerType === this.type);
        if (this.behavioralParameters === undefined) {
            this.behavioralParameters = new BehavioralParameters(1, 0 , 0, this.type);
        }

        this.visitsValues = new Array(100);
        for (let i = 0; i < this.visitsValues.length; i++) {
            this.visitsValues[i] = i+1;
        }

        this.daysValues = Array.from(Array(180).keys());
    }

    public updatePositiveTriggers(e: any) {
        let text = e.target.value;
        this.triggers.positive = text.split('\n');

        if (text.length === 0) {
            this.channel.behavioralParameters = this.channel.behavioralParameters.filter(bp => bp.triggerType !== this.type);
            this.triggers.positive = null;
        } else {
            let bpIndex = this.channel.behavioralParameters.findIndex(bp => bp.triggerType === this.type);
            if (bpIndex === -1) {
                this.channel.behavioralParameters.push(this.behavioralParameters);
            }
        }
    }

    public triggersErrorKeyByType() {
        switch (this.type) {
            case 'P': return 'pageKeywords';
            case 'S': return 'searchKeywords';
            case 'U': return 'urls';
            case 'R': return 'urlKeywords';
        }
    }

    public behavioralParametersErrorKeyByType() {
        return 'behavioralParameters[' + this.type + ']';
    }
}
