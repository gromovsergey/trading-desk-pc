import {
  Component,
  Input,
  Output,
  EventEmitter,
  ElementRef,
  ViewChild,
  ContentChild,
  OnInit,
  Inject
} from '@angular/core';
import {ChannelService} from '../../services/channel.service';
import {L10nChannelTreeSources} from '../../../common/L10n.const';
import {FlatTreeControl} from "@angular/cdk/tree";
import {DynamicFlatNode} from "../../../flight/components/flight-channels-tree/flight-channels-tree.component";
import {ExpressionChannel} from "../../models/expression_channel.model";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'ui-channel-tree',
  templateUrl: 'channel-tree.component.html',
  styleUrls: ['./channel-tree.component.scss']
})
export class ChannelTreeComponent implements OnInit {

  @ViewChild('textInput') textInputEl: ElementRef;

  @Input() sources: string[] = [];
  @Input() accountId: number;
  @Input() selectedChannelsInput: IdName[] = [];

  @Output() treeClose: EventEmitter<void> = new EventEmitter<void>();
  @Output() save: EventEmitter<IdName[]> = new EventEmitter<IdName[]>();

  @ContentChild('customContent') customContentEl: ElementRef;
  @ViewChild('customContent') customContentChildEl: ElementRef;
  @ViewChild('tree') tree: ElementRef;

  options: any = null;
  L10nChannelTreeSources = L10nChannelTreeSources;
  selectedChannels: IdName[] = [];

  channel: ExpressionChannel;
  accounts: any[];
  popupOptions;
  popupVisible = false;
  popupWait = false;
  autocomplete: any[];
  channelsLink: any[];
  showChannelTreeFlag = false;
  currentIdx: number
  isCurrentExcluded: boolean
  setChannelList: (arg0: any, arg1: number, arg2: boolean) => void;
  private inputTimer;

  constructor(
    private channelService: ChannelService,
    @Inject(MAT_DIALOG_DATA) public data: {
      currentIdx: number,
      isCurrentExcluded: boolean,
      channelsLink: [],
      channel: ExpressionChannel,
      setChannelsLink: (arg0: any, arg1: number, arg2: boolean) => void
    }
  ) {
  }

  ngOnInit(): void {
    this.channel = this.data.channel
    this.channelsLink = this.data.channelsLink
    this.setChannelList = this.data.setChannelsLink;
    this.isCurrentExcluded = this.data.isCurrentExcluded
    this.currentIdx = this.data.currentIdx
    this.selectedChannels = this.selectedChannelsInput.concat([]);
    this.setChannelTree();
    if (this.customContentChildEl && this.customContentEl) {
      this.customContentChildEl.nativeElement.appendChild(this.customContentEl.nativeElement);
    }
  }

  bindTreeNode(data, item): any {
    let found;
    const search = el => {
      if (found) {
        return;
      }
      if (el.id === item.id) {
        found = el;
      } else {
        if (el.nodes) {
          el.nodes.forEach(search);
        }
      }
    };
    data.forEach(search);
    if (found) {
      found.state = item.state;
    }
    return found;
  }

  isPreviouslyAdded(item): boolean {
    return !!this.selectedChannels.find(el => el.id === item.id);
  }

  modifyLinkedChannels(item, state): void {
    if (!item || !item.id) {
      return;
    }

    if (!state.selected) {
      this.selectedChannels = this.selectedChannels.filter(channel => channel.id !== item.id);
      return;
    }

    if (!this.selectedChannels.find(channel => channel.id === item.id)) {
      this.selectedChannels.push({id: item.id, name: item.name});
    }
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

  popupSave(e?: any): void {
    let selectedChannelsResult = this.selectedChannels.concat([]);
    this.selectedChannels.forEach(parent =>
      selectedChannelsResult = selectedChannelsResult
        .filter(channel => parent.name === channel.name || channel.name.search(parent.name) !== 0));

    this.save.emit(selectedChannelsResult);
  }

  popupClose(e?: any): void {
    this.treeClose.emit();
  }

  getRootNodes(): any {
    let index = -1;

    return this.sources.map(name => ({
      name,
      text: this.L10nChannelTreeSources[name],
      id: index--,
      hasChildren: true,
      selectable: false
    }));
  }

  getNodes(node: any): Promise<any> {
    if (!node) {
      return Promise.resolve(this.getRootNodes());
    }

    if (!node.isNew) {
      return Promise.resolve(node.nodes);
    }

    if (node.id < 0) {
      return this.channelService.channelRubricNodesSearch(this.accountId, node.name);
    }

    return this.channelService.channelNodesSearch(node.id);
  }

  setChannelTree(data?, node?): void {
    const parentSelected = node && node.state && node.state.selected;
    const tree = this.tree.nativeElement;

    this.getNodes(node)
      .then(res => {
        res.forEach(item => {
          if (item.hasOwnProperty('isNew') && !item.isNew) {
            return;
          }
          item.isNew = true;

          item.state = {
            disabled: parentSelected,
            selected: !parentSelected && this.isPreviouslyAdded(item),
            expanded: this.hasSelectedChildren(item)
          };
          if (item.hasChildren) {
            item.nodes = [{text: '_L10N_(messages.loading)', state: {disabled: true}}];
          }
        });
        if (data && node) {
          this.bindTreeNode(data, node).nodes = res;
          this.bindTreeNode(data, node).isNew = false;
        } else {
          data = data || res;
        }

        tree.treeview({
          levels: 1,
          multiSelect: true,
          selectedColor: '#333',
          selectedBackColor: '#fbce5d',
          data
        });
        tree.on('nodeExpanded', (event, node2) => {
          this.setChannelTree(data, node2);
        });
        tree.on('nodeCollapsed nodeDisabled', (event, node2) => {
          this.bindTreeNode(data, node2);
        });
        tree.on('nodeEnabled', (event, node2) => {
          this.bindTreeNode(data, node2);
          if (this.isPreviouslyAdded(node2)) {
            tree.treeview('selectNode', node2.nodeId);
          }
        });
        tree.on('nodeSelected nodeUnselected', (event, node2) => {
          if (node2.state.disabled) {
            return;
          }

          this.modifyLinkedChannels(this.bindTreeNode(data, node2), node2.state);
          if (node2.nodes) {
            node2.nodes.forEach(el => tree.treeview(node2.state.selected ? 'disableNode' : 'enableNode', el.nodeId));
          }
        });


        return Promise.resolve(res);
      })
      .then(nodes => {
        nodes.forEach(node2 => {
          if (node2.isNew && this.hasSelectedChildren(node2)) {
            this.setChannelTree(data, node2);
          }
        });
      });
  }

  hasSelectedChildren(node: any): boolean {
    if (!node.hasChildren) {
      return false;
    }

    if (node.id > 0) {
      return !!this.selectedChannels.find(c => +c.name !== node.name && c.name.search(node.name) === 0);
    }

    // node.id < 0 means SOURCES, so it must be in name following country, e.g. "RU.SOURCE1."
    return !!this.selectedChannels.find(c => +c.name.search('.' + node.name + '.') === 2);
  }

  showPopup(): void {
    this.hideAutocomplete();
    this.popupVisible = true;

    window.setTimeout(() => {
      this.textInputEl.nativeElement.focus();
    });
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
    this.setChannelList(this.channelsLink, this.currentIdx, this.isCurrentExcluded)
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
        this.channelService
          .getExpressionChannels(this.channel.accountId, this.channel.country, text)
          .then(list => {
            this.autocomplete = this.removeDuplicates(list);
            this.popupWait = false;

            window.setTimeout(() => {
              this.textInputEl.nativeElement.focus();
            });
          });

      }
    }, 500);
  }
}
