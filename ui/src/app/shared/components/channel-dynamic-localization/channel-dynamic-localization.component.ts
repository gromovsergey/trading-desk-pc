import {Component, Inject, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ChannelService} from '../../../channel/services/channel.service';
import {DynamicLocalizationModel} from '../../../channel/models/dynamic-localization.model';
import {L10nLanguages} from '../../../common/L10n.const';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';


@Component({
  selector: 'ui-channel-dynamic-localization',
  templateUrl: 'channel-dynamic-localization.component.html',
  styleUrls: ['./channel-dynamic-localization.component.scss']
})
export class ChannelDynamicLocalizationComponent implements OnInit {
  L10nLanguages = L10nLanguages;
  options;
  languages = ['ru', 'en'];
  wait = false;

  private initialDynamicLocalizations: DynamicLocalizationModel[] = [];
  private dynamicLocalizations: DynamicLocalizationModel[] = [];

  constructor(private channelService: ChannelService,
              private dialogRef: MatDialogRef<any>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    this.languages.forEach(lang => this.initialDynamicLocalizations.push(new DynamicLocalizationModel(lang)));
    this.wait = true;
    this.channelService.getDynamicLocalizations(this.data.channelId)
      .then(resources => {
        resources.forEach(r => {
          const localization = this.initialDynamicLocalizations.find(dl => dl.lang === r.lang);
          if (localization) {
            localization.value = r.value;
          }
        });
        this.dynamicLocalizations = this.initialDynamicLocalizations.map(l => Object.assign({}, l));
        this.wait = false;
      })
      .catch(() => {
        this.wait = false;
      });
  }

  getLocalization(lang: string): string {
    return this.dynamicLocalizations.find(dl => dl.lang === lang).value;
  }

  setLocalization(lang: string, value): void {
    return this.dynamicLocalizations.find(dl => dl.lang === lang).value = value;
  }

  async onSave(): Promise<any> {
    this.wait = true;

    const toUpdate = [];
    const toDelete = [];
    this.dynamicLocalizations.forEach(dl => {
      dl.value = dl.value.trim();
      const initial = this.initialDynamicLocalizations.find(idl => idl.lang === dl.lang);
      if (dl.value === initial.value) {
        return;
      }

      if (dl.value) {
        toUpdate.push(dl);
      } else {
        toDelete.push(dl);
      }
    });
    try {
      await Promise.all([
        toUpdate.length ? this.channelService.updateDynamicLocalizations(this.data.channelId, toUpdate) : Promise.resolve(null),
        toDelete.length ? this.channelService.deleteDynamicLocalizations(this.data.channelId, toDelete) : Promise.resolve(null)
      ]);
    } finally {
      this.wait = false;
      this.dialogRef.close();
    }
  }
}
