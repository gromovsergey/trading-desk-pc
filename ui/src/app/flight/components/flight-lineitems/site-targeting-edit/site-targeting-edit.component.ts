import {AfterViewInit, Component, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {MatTabChangeEvent} from "@angular/material/tabs";
import {FlightModel} from "../../../models/flight.model";
import {
  FlightEditInventorySourceComponent
} from "../../flight-edit/flight-edit-inventory-source/flight-edit-inventory-source.component";
import {AdvertiserSessionModel} from "../../../../advertiser/models";
import {AdvertiserService} from "../../../../advertiser/services/advertiser.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {FlightService} from "../../../services/flight.service";
import {LineItemService} from "../../../../lineitem/services/lineitem.service";
import {IDropdownSettings} from "ng-multiselect-dropdown";
import {Action} from "../flight-lineitems.component";

export interface ITabSettings {
  english?: string;
  rus?: string;
  value?: string;
  component?: Action
  label?: string
}

@Component({
  selector: 'ui-site-targeting-edit',
  templateUrl: './site-targeting-edit.component.html',
  styleUrls: ['./site-targeting-edit.component.scss']
})
export class SiteTargetingEditComponent implements OnInit, OnDestroy, AfterViewInit{

  public tabSettings: ITabSettings[];
  public selectedOptions: IdName[];
  public sites: IdName[];
  public flight: FlightModel;
  public animationDuration: number;
  public activeItem: ITabSettings;
  public operationDone: boolean;
  public operationResult: 'info' | 'danger' | 'warning' | 'success';
  public operationText: string;
  public disabled: boolean;
  public dropdownSettings: IDropdownSettings;
  private selectedSiteList: any = [];
  private unsubscribe$: Subject<boolean>;

  @ViewChild('flightEditInventory', {static: false}) flightEditInventory: FlightEditInventorySourceComponent;

  constructor(
      public dialogRef: MatDialogRef<SiteTargetingEditComponent>,
      private advertiserService: AdvertiserService,
      private flightService: FlightService,
      private lineItemService: LineItemService,
      @Inject(MAT_DIALOG_DATA) public data: any)
  {
    this.activeItem = { english: 'Set To', rus: 'Установить', value: 'SET' };
    this.selectedOptions = [];
    this.selectedSiteList = [];
    this.unsubscribe$ = new Subject<boolean>();
    this.flight = new FlightModel();
    this.sites = [];
    this.tabSettings = [
      { english: 'Set To', rus: 'Установить', value: 'SET' },
      { english: 'Add Sites', rus: 'Добавить', value: 'ADD' },
      { english: 'Remove Sites', rus: 'Удалить', value: 'DELETE'}
    ];
    this.dropdownSettings = {
      singleSelection: false,
      idField: 'id',
      textField: 'name',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 5,
      allowSearchFilter: true,
      maxHeight: 300,
    };
    this.animationDuration = 1000;
    this.operationDone = false;
    this.disabled = false;
    this.operationText = '';
  }

  ngOnInit(): void {
    this.advertiserService.getSiteList$(new AdvertiserSessionModel().id)
        .pipe(takeUntil(this.unsubscribe$))
        .subscribe({
      next: (res) => {
        this.sites = res.map(site => ({
          id: site.siteId,
          name: site.name
        }));
      },
      error: (error) => {
        console.log(error);
      },
      complete: () => {
      }
    });
  }

  ngAfterViewInit(): void {}

  ngOnDestroy(): void {
    this.unsubscribe$.next(true);
    this.unsubscribe$.complete();
  }

  get language(): string {
    return localStorage.getItem('lang');
  }

  public setActiveItem(event: MatTabChangeEvent): void {
    this.selectedOptions = [];
    let fieldForFiltering = this.language === 'ru' ? 'rus' : 'english';
    this.activeItem = this.tabSettings.filter(tab => tab[fieldForFiltering] === event.tab.textLabel)[0];

    this.destroyInfoPanel();
  }

  public onClose(): void {
    this.dialogRef.close();
  }

  public onNgModelChange(sites: IdName[]): void {
    this.selectedOptions = [...sites];
  }

  public onSourceChange(sites: IdName[]): void {
    this.selectedOptions = [...sites];
  }

  public onSubmit(): void {
    /*
      lineItemIds=1098,1150 - id стратегий
      ADD - Добавить новое, не удаляя старое
      DELETE - Удалить из сторого передаваемый сайт
      SET - Перезаписать старое на новое

      передача id сайтов будет идти через body в PUT запросе.
    */
    this.disabled = true;
    this.lineItemService.changeSite$(this.data, this.selectedOptions.map(s => s.id), this.activeItem.value, 'SITE')
        .pipe(takeUntil(this.unsubscribe$))
        .subscribe({
      next: () => {},
      error: (error) => {
        this.operationResult = 'danger';
        this.operationText = 'Something went wrong. Please try again!';
        this.operationDone = true;
        this.disabled = false;
        },
      complete: () => {
        this.operationResult = 'success';
        this.operationText = 'Operation completed successfully!';
        this.operationDone = true;
        this.disabled = false;
      }
    });
  }

  public destroyInfoPanel(): void {
    this.operationDone = false;
  }
}

