import {Component, Inject, Injectable} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {L10nChannelTreeSources} from '../../../common/L10n.const';
import {FlatTreeControl} from '@angular/cdk/tree';
import {L10nStatic} from '../../../shared/static/l10n.static';
import {CollectionViewer, DataSource, SelectionChange} from '@angular/cdk/collections';
import {BehaviorSubject, merge, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {ChannelService} from '../../../channel/services/channel.service';
import {environment} from '../../../../environments/environment';
import {MatCheckboxChange} from '@angular/material/checkbox';

const channelTreeSources = L10nChannelTreeSources;

export class DynamicFlatNode {
  constructor(public id: number,
              public name: string,
              public text: string,
              public level = 1,
              public expandable = false,
              public isLoading = false) {
  }
}

@Injectable({providedIn: 'root'})
export class DynamicDatabase {

  rootLevelNodes: any[] = [
    {
      id: -1,
      name: L10nStatic.translate(channelTreeSources.BEELINE),
      text: L10nStatic.translate(channelTreeSources.BEELINE),
      hasChildren: true,
    },
    {
      id: -2,
      name: L10nStatic.translate(channelTreeSources.OWN_CHANNELS),
      text: L10nStatic.translate(channelTreeSources.OWN_CHANNELS),
      hasChildren: true,
    },
  ];

  mapData = {};

  constructor(private channelService: ChannelService) {
  }

  initialData(): DynamicFlatNode[] {
    return this.rootLevelNodes.map((obj: any) => new DynamicFlatNode(obj.id, obj.name, obj.text, 0, obj.hasChildren));
  }

  async getChildren(accountId: number, nodeId: number): Promise<any[]> {
    if (this.mapData[nodeId]) {
      return this.mapData[nodeId];
    }
    if (nodeId === -1) {
      return this.channelService.channelRubricNodesSearch(accountId, environment._EXTERNAL_CHANNEL_SOURCES_);
    } else if (nodeId === -2) {
      return this.channelService.channelRubricNodesSearch(accountId, environment._OWN_CHANNEL_SOURCE_);
    } else {
      return this.channelService.channelNodesSearch(nodeId);
    }
  }


  isExpandable(nodeId: number): boolean {
    return this.mapData[nodeId];
  }
}

export class DynamicDataSource implements DataSource<DynamicFlatNode> {

  dataChange = new BehaviorSubject<DynamicFlatNode[]>([]);

  get data(): DynamicFlatNode[] {
    return this.dataChange.value;
  }

  set data(value: DynamicFlatNode[]) {
    this._treeControl.dataNodes = value;
    this.dataChange.next(value);
  }

  constructor(private accountId: number,
              private _treeControl: FlatTreeControl<DynamicFlatNode>,
              private _database: DynamicDatabase) {
  }

  connect(collectionViewer: CollectionViewer): Observable<DynamicFlatNode[]> {
    this._treeControl.expansionModel.changed.subscribe(change => {
      if ((change as SelectionChange<DynamicFlatNode>).added ||
        (change as SelectionChange<DynamicFlatNode>).removed) {
        this.handleTreeControl(change as SelectionChange<DynamicFlatNode>);
      }
    });

    return merge(collectionViewer.viewChange, this.dataChange).pipe(map(() => this.data));
  }

  disconnect(collectionViewer: CollectionViewer): void {
  }

  /** Handle expand/collapse behaviors */
  handleTreeControl(change: SelectionChange<DynamicFlatNode>) {
    if (change.added) {
      change.added.forEach(node => this.toggleNode(node, true));
    }
    if (change.removed) {
      change.removed.slice().reverse().forEach(node => this.toggleNode(node, false));
    }
  }

  /**
   * Toggle the node, remove from display list
   */
  async toggleNode(node: DynamicFlatNode, expand: boolean) {
    node.isLoading = true;
    const children = await this._database.getChildren(this.accountId, node.id);
    const index = this.data.indexOf(node);

    this._database.mapData[node.id] = children && children.length ? children : null;

    if (expand) {
      const nodes = children.map(obj => new DynamicFlatNode(obj.id, obj.name, obj.text, node.level + 1, obj.hasChildren));
      this.data.splice(index + 1, 0, ...nodes);
    } else {
      let count = 0;
      for (let i = index + 1; i < this.data.length && this.data[i].level > node.level; i++, count++) {
      }
      this.data.splice(index + 1, count);
    }
    // notify the change
    this.dataChange.next(this.data);
    node.isLoading = false;
  }

  async addChildrenNode(node: DynamicFlatNode, arrWithName: IdName[]): Promise<void> {
    node.isLoading = true;
    if (node.expandable && (arrWithName.find(name => name.name.indexOf(node.name) > -1) || node.level === 0)) {
      const children = await this._database.getChildren(this.accountId, node.id);
      this._database.mapData[node.id] = children && children.length ? children : null;
      const childrenElem = children.filter(el => arrWithName.find(name => name.name.indexOf(el.name) > -1))
      if (childrenElem.length > 0) {
        this._treeControl.expand(node)
      }
    }
    node.isLoading = false;
  }
}

@Component({
  selector: 'ui-flight-channels-tree',
  templateUrl: './flight-channels-tree.component.html',
  styleUrls: ['./flight-channels-tree.component.scss']
})
export class FlightChannelsTreeComponent {
  linkSpecial: boolean;
  treeControl: FlatTreeControl<DynamicFlatNode>;
  dataSource: DynamicDataSource;
  checkedIds: number[] = [];

  get saveData(): any {
    return {
      linkSpecial: this.linkSpecial,
      checkedIds: this.checkedIds,
    };
  }

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {
      accountId: number;
      linkSpecial: boolean;
      selectedChannels: IdName[];
    },
    private database: DynamicDatabase
  ) {

  }

  ngOnInit(): void{
    this.linkSpecial = this.data.linkSpecial || false;
    this.treeControl = new FlatTreeControl<DynamicFlatNode>(this.getLevel, this.isExpandable);
    this.dataSource = new DynamicDataSource(this.data.accountId, this.treeControl, this.database);
    this.dataSource.data = this.database.initialData();
    this.checkedIds = this.data.selectedChannels.map(el => el.id);
    //this.initialData(this.dataSource, this.dataSource.data, this.data.selectedChannels)
  }

  async initialData(dataSource, nodes, selectedChannel) {
    for (let i = 0; i < nodes.length; i++) {
      await dataSource.addChildrenNode(nodes[i], selectedChannel)
    }
  }


  getLevel = (node: DynamicFlatNode) => node.level;
  isExpandable = (node: DynamicFlatNode) => node.expandable;
  hasChild = (_: number, _nodeData: DynamicFlatNode) => _nodeData.expandable;

  isChecked(id) {
    return !!this.checkedIds.find(el => el === id)
  }

  checkBoxChange(e: MatCheckboxChange, value: number) {
    const id = +value;
    if (e.checked && !this.checkedIds.includes(id)) {
      this.checkedIds.push(id);
    } else {
      this.checkedIds = this.checkedIds.filter(val => val !== id);
    }
  }
}
