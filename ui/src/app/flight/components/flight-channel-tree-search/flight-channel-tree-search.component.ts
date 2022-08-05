import {
  Component,
  Input,
  ElementRef,
  ViewChild,
  OnInit,
  Inject
} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {FlightService} from "../../services/flight.service";
import {ChannelService} from "../../../channel/services/channel.service";

@Component({
  selector: 'ui-flight-channel-tree-search',
  templateUrl: 'flight-channel-tree-search.component.html',
  styleUrls: ['./flight-channel-tree-search.component.scss']
})
export class FlightChannelTreeSearchComponent implements OnInit {

  @ViewChild('textInput') textInputEl: ElementRef;

  @Input() sources: string[] = [];
  @Input() accountId: number;

  popupWait = false;
  autocomplete: any[];
  channelsLink: any[];
  save: (arg0: any[]) => void;
  setChannelList: (arg0: any, arg1: number, arg2: boolean) => void;
  private inputTimer;

  constructor(
    private flightService: FlightService,
    private channelService: ChannelService,
    @Inject(MAT_DIALOG_DATA) public data: {
      accountId: number
      channels: any[]
      popupSave: (arg0: any[]) => void
    }
  ) {
  }

  ngOnInit(): void {
    this.accountId = this.data.accountId;
    this.channelsLink = []
    this.channelsLink = this.data.channels
    this.save = this.data.popupSave
  }

  addChannel(channel: any): void {
    this.channelsLink.push(channel);
    this.clearTextarea();
    this.hideAutocomplete();
  }

  clearTextarea(): void {
    this.textInputEl.nativeElement.value = '';
    this.textInputEl.nativeElement.focus();
  }

  removeChannel(e: any, id: number): void {
    e.preventDefault();
    this.channelsLink = this.channelsLink.filter(v => v.id !== id);
  }

  hideAutocomplete(): void {
    this.autocomplete = null;
  }

  removeDuplicates(list: Array<any>): Array<any> {
    if (list.length && this.channelsLink.length) {
      return list.filter(v => !this.channelsLink.find(f => f.id === v.id));
    } else {
      return list;
    }
  }
  saveChannelsLink(): void {
    this.save(this.channelsLink)
  }

  textInputChange(): void {
    if (this.popupWait) {
      return;
    }

    const textarea = this.textInputEl.nativeElement;

    if (this.inputTimer) {
      clearTimeout(this.inputTimer);
    }

    this.inputTimer = setTimeout(() => {
      const text = textarea.value;

      this.hideAutocomplete();

      if (text.length >= 3) {
        this.popupWait = true;

        if (text.includes('\n')) {
          const rows = text.split('\n');
          const rowsFormatted = rows.map(v => {
            const parts = v.split('|');
            return {
              name: parts[0] || null,
              accountName: parts[1] || null
            };
          });
          this.channelService
            .channelsSearch(this.accountId, rowsFormatted)
            .then(list => {
              list = this.removeDuplicates(list);
              this.channelsLink.push(...list);
              this.popupWait = false;

              window.setTimeout(() => {
                this.textInputEl.nativeElement.value = rows.filter(v =>
                  !this.channelsLink.find(f => f.name + '|' + f.accountName === v)).join('\n');
                this.textInputEl.nativeElement.focus();
              });
            });
        } else {
          this.channelService
            .getAccountChannels(this.accountId, text.split('|')[0])
            .then(list => {
              this.autocomplete = this.removeDuplicates(list);
              this.popupWait = false;

              window.setTimeout(() => {
                this.textInputEl.nativeElement.focus();
              });
            });
        }
      }
    }, 500);
  }
}
