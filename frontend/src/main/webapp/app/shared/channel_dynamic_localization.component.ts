import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {ChannelService} from '../channel/channel.service';
import {DynamicLocalizationModel} from "../channel/dynamic_localization.model";
import {L10nLanguages} from "../common/L10n.const";


@Component({
    selector: 'ui-channel-dynamic-localization',
    templateUrl: 'channel_dynamic_localization.html'
})
export class ChannelDynamicLocalizationComponent implements OnInit {

    @Input() channelId: number;

    @Output() close = new EventEmitter<void>();
    @Output() save = new EventEmitter<any>();

    public L10nLanguages = L10nLanguages;
    public options;
    public languages: Array<string> = ['ru', 'en'];
    public wait: boolean = false;

    private initialDynamicLocalizations: Array<DynamicLocalizationModel> = [];
    private dynamicLocalizations: Array<DynamicLocalizationModel> = [];

    constructor(protected channelService: ChannelService,
                protected route: ActivatedRoute) {
        this.options = {
            title: '_L10N_(channel.blockName.channel.localization)',
            btnTitle: '_L10N_(button.save)',
            btnIcon: null,
            btnIconDisabled: false,
            size: 'lg'
        };
    }

    public ngOnInit() : void {
        this.languages.forEach(lang => this.initialDynamicLocalizations.push(new DynamicLocalizationModel(lang)));
        this.wait = true;
        this.channelService.getDynamicLocalizations(this.channelId)
            .then(resources => {
                resources.forEach(r => {
                    let localization = this.initialDynamicLocalizations.find(dl => dl.lang == r.lang);
                    if (localization) {
                        localization.value = r.value;
                    }
                });

                this.dynamicLocalizations = this.initialDynamicLocalizations.map(l => Object.assign({}, l));

                this.wait = false;
            })
            .catch(e => {
                this.wait = false;
            });
    }

    public getLocalization(lang: string): string {
        return this.dynamicLocalizations.find(dl => dl.lang == lang).value;
    }

    public setLocalization(lang: string, value): void {
        return this.dynamicLocalizations.find(dl => dl.lang == lang).value = value;
    }

    public onClose() : void {
        this.close.emit();
    }

    public onSave() : void {
        this.wait = true;

        let toUpdate: Array<DynamicLocalizationModel> = [];
        let toDelete: Array<DynamicLocalizationModel> = [];
        this.dynamicLocalizations.forEach(dl => {
            dl.value = dl.value.trim();
            let initial = this.initialDynamicLocalizations.find(idl => idl.lang == dl.lang);
            if (dl.value == initial.value) {
                return;
            }

            if (dl.value) {
                toUpdate.push(dl);
            } else {
                toDelete.push(dl);
            }
        });

        Promise.all([
            toUpdate.length ? this.channelService.updateDynamicLocalizations(this.channelId, toUpdate) : Promise.resolve(null),
            toDelete.length ? this.channelService.deleteDynamicLocalizations(this.channelId, toDelete) : Promise.resolve(null)
        ])
        .then(res => {
            this.wait = false;
            this.save.emit();
        })
        .catch(e => {
            this.wait = false;
            this.save.emit(e);
        });
    }
}
