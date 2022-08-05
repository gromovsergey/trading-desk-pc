import {Component, Input, Output, EventEmitter, ElementRef, ViewChild, ContentChild, OnInit} from '@angular/core';
import {IdName} from "./idname.model";
import {jQuery as $} from "../common/common.const";
import {ChannelService} from "../channel/channel.service";
import {L10nChannelTreeSources} from '../common/L10n.const';

@Component({
    selector: 'ui-channel-tree',
    templateUrl: 'channel_tree.html'
})

export class ChannelTreeComponent implements OnInit {

    @Input('sources') sources: Array<string> = [];
    @Input('accountId') accountId: number;
    @Input('selectedChannels') selectedChannelsInput: Array<IdName> = [];

    @Output() close: EventEmitter<void> = new EventEmitter<void>();
    @Output() save: EventEmitter<Array<IdName>> = new EventEmitter<Array<IdName>>();

    @ContentChild('customContent') customContentEl: ElementRef;
    @ViewChild('customContent') customContentChildEl: ElementRef;
    @ViewChild('tree') tree: ElementRef;

    public options: any = null;
    public L10nChannelTreeSources = L10nChannelTreeSources;

    private selectedChannels: Array<IdName> = [];

    public constructor(private channelService: ChannelService) {
    }

    public ngOnInit() {
        this.options = {
            title: '_L10N_(flight.button.linkChannels)',
            btnTitle: '_L10N_(button.link)',
            btnIcon: 'link',
            btnIconDisabled: false
        };

        this.selectedChannels = this.selectedChannelsInput.concat([]);

        this.setChannelTree();

        if (this.customContentChildEl && this.customContentEl) {
            this.customContentChildEl.nativeElement.appendChild(this.customContentEl.nativeElement);
        }
    }

    private bindTreeNode(data, item): any {
        let found;
        let search = el => {
            if (found) return;
            if (el.id === item.id) {
                found = el;
            } else {
                if (el.nodes) el.nodes.forEach(search);
            }
        };
        data.forEach(search);
        if (found) found.state = item.state;
        return found;
    }

    private isPreviouslyAdded(item): boolean {
        return !!this.selectedChannels.find(el => el.id === item.id);
    }

    private modifyLinkedChannels(item, state): void {
        if (!item || !item.id) {
            return;
        }

        if (!state.selected) {
            this.selectedChannels = this.selectedChannels.filter(channel => channel.id != item.id);
            return;
        }

        if (!this.selectedChannels.find(channel => channel.id === item.id)) {
            this.selectedChannels.push(new IdName(item.id, item.name));
        }
    }

    public popupSave(e?: any): void {
        let selectedChannelsResult = this.selectedChannels.concat([]);
        this.selectedChannels.forEach( parent =>
            selectedChannelsResult = selectedChannelsResult
                .filter(channel => parent.name === channel.name || channel.name.search(parent.name) !== 0));

        this.save.emit(selectedChannelsResult);
    }

    public popupClose(e?: any): void {
        this.close.emit();
    }

    private getRootNodes(): any {
        let result = [];
        let index = -1;

        this.sources.forEach( sourceName => {
            let obj = {};
            obj['name'] = sourceName;
            obj['text'] = this.L10nChannelTreeSources[sourceName];
            obj['id'] = index--;
            obj['hasChildren'] = true;
            obj['selectable'] = false;

            result.push(obj);
        });

        return result;
    }

    private getNodes(node: any): Promise<any> {
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

    private setChannelTree(data?, node?): void {
        let parentSelected = node && node.state && node.state.selected;
        let tree = $(this.tree.nativeElement);
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
                tree.on('nodeExpanded', (event, node) => {
                    this.setChannelTree(data, node);
                });
                tree.on('nodeCollapsed nodeDisabled', (event, node) => {
                    this.bindTreeNode(data, node);
                });
                tree.on('nodeEnabled', (event, node) => {
                    this.bindTreeNode(data, node);
                    if (this.isPreviouslyAdded(node)) {
                        tree.treeview('selectNode', node.nodeId);
                    }
                });
                tree.on('nodeSelected nodeUnselected', (event, node) => {
                    if (node.state.disabled) {
                        return;
                    }

                    this.modifyLinkedChannels(this.bindTreeNode(data, node), node.state);
                    node.nodes && node.nodes.forEach(el => tree.treeview(node.state.selected ? 'disableNode' : 'enableNode', el.nodeId));
                });

                return Promise.resolve(res);
            })
            .then(nodes => {
                nodes.forEach(node => {
                    if (node.isNew && this.hasSelectedChildren(node)) {
                        this.setChannelTree(data, node);
                    }
                });
            })
    }

    private hasSelectedChildren(node: any): boolean {
        if (!node.hasChildren) {
            return false;
        }

        if (node.id > 0) {
            return !!this.selectedChannels.find(c => c.name !== node.name && c.name.search(node.name) == 0);
        }

        // node.id < 0 means SOURCES, so it must be in name following country, e.g. "RU.SOURCE1."
        return !!this.selectedChannels.find(c => c.name.search('.' + node.name + '.') == 2);
    }
}
