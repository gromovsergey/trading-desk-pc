import {Component, Input, OnInit} from '@angular/core';
import {BehavioralParameters} from '../../models/behavioral-parameters.model';
import {BehavioralChannel} from '../../models/behavioral-channel.model';
import {Triggers} from '../../models/trigger.model';
import {L10nStatic} from '../../../shared/static/l10n.static';

@Component({
  selector: 'ui-channel-triggers-edit',
  templateUrl: 'channel-triggers-edit.component.html',
  styleUrls: ['./channel-triggers-edit.component.scss']
})
export class ChannelTriggersEditComponent implements OnInit {
  @Input() channel: BehavioralChannel;
  @Input() errors;
  @Input() type: 'P' | 'S' | 'U' | 'R' | string; // P, S, U, R

  sectionName: string;
  triggers: Triggers;
  behavioralParameters: BehavioralParameters;
  visitsValues: number[];
  daysValues: number[];

  constructor() {
  }

  ngOnInit(): void {
    switch (this.type) {
      case 'P':
        this.sectionName = L10nStatic.translate('channel.blockName.keywords.pageKeywords');
        this.triggers = this.channel.pageKeywords;
        break;
      case 'S':
        this.sectionName = L10nStatic.translate('channel.blockName.keywords.searchKeywords');
        this.triggers = this.channel.searchKeywords;
        break;
      case 'U':
        this.sectionName = L10nStatic.translate('channel.blockName.keywords.urls');
        this.triggers = this.channel.urls;
        break;
      case 'R':
        this.sectionName = L10nStatic.translate('channel.blockName.keywords.urlKeywords');
        this.triggers = this.channel.urlKeywords;
        break;
    }
    this.behavioralParameters = this.channel.behavioralParameters.find(bp => bp.triggerType === this.type);
    if (this.behavioralParameters === undefined) {
      this.behavioralParameters = new BehavioralParameters(1, 0, 0, this.type);
    }

    this.visitsValues = new Array(100);
    for (let i = 0; i < this.visitsValues.length; i++) {
      this.visitsValues[i] = i + 1;
    }

    this.daysValues = Array.from(Array(180).keys());
  }

  updatePositiveTriggers(e: any): void {
    const text = e.target.value;
    this.triggers.positive = text.split('\n');

    if (text.length === 0) {
      this.channel.behavioralParameters = this.channel.behavioralParameters.filter(bp => bp.triggerType !== this.type);
      this.triggers.positive = null;
    } else {
      const bpIndex = this.channel.behavioralParameters.findIndex(bp => bp.triggerType === this.type);
      if (bpIndex === -1) {
        this.channel.behavioralParameters.push(this.behavioralParameters);
      }
    }
  }

  triggersErrorKeyByType(): string {
    switch (this.type) {
      case 'P':
        return 'pageKeywords';
      case 'S':
        return 'searchKeywords';
      case 'U':
        return 'urls';
      case 'R':
        return 'urlKeywords';
    }
  }

  behavioralParametersErrorKeyByType(): string {
    return 'behavioralParameters[' + this.type + ']';
  }
}
