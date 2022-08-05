import {Component, Inject, OnInit, OnDestroy, ViewChild, ViewContainerRef, ChangeDetectorRef} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Action} from "../flight-lineitems.component";
import {ITabSettings} from "../site-targeting-edit/site-targeting-edit.component";
import {FlightEditGeotargetComponent} from "../../flight-edit/flight-edit-geotarget/flight-edit-geotarget.component";
import {FlightModel} from "../../../models/flight.model";
import {MatTabChangeEvent} from "@angular/material/tabs";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {LineItemService} from "../../../../lineitem/services/lineitem.service";
import {RenderComponent} from "../../../../shared/components/render/render.component";

@Component({
  selector: 'ui-geo-targeting-edit',
  templateUrl: './geo-targeting-edit.component.html',
  styleUrls: ['./geo-targeting-edit.component.scss']
})
export class GeoTargetingEditComponent implements OnInit, OnDestroy {

  public tabSettings: ITabSettings[];
  public activeItem: ITabSettings;
  public animationDuration: number;
  public disabled: boolean;
  public component: Action;
  public geoAddress: number[];
  public geotarget: number[];
  public excludedGeotarget: number[];
  public excludedGeoAddress: number[];
  public operationResult: 'info' | 'danger' | 'warning' | 'success';
  public operationText: string;
  public operationDone: boolean;
  protected flight: FlightModel;
  private unSubscribe$: Subject<boolean>;

  @ViewChild('uiRender', { static: false }) uiRender: RenderComponent<any, any>;

  constructor(
      public dialogRef: MatDialogRef<GeoTargetingEditComponent>,
      private lineItemService: LineItemService,
      private changeDetectorRef: ChangeDetectorRef,
      @Inject(MAT_DIALOG_DATA) public data) {
    this.unSubscribe$ = new Subject<boolean>();
    this.flight = new FlightModel();
    this.animationDuration = 1000;
    this.disabled = false;
    this.geoAddress = [];
    this.geotarget = [];
    this.excludedGeotarget = [];
    this.excludedGeoAddress = [];
    this.activeItem = { value: 'SET', label: 'button.setTo' };
    this.tabSettings = [
      { value: 'SET', label: 'button.setTo' },
      { value: 'ADD', label: 'button.Add' },
      { value: 'DELETE', label: 'button.Remove'}
    ];
    this.operationText = '';
    this.operationDone = false;
  }

  ngOnInit(): void {
    this.component = this.createFlightEditGeotargetAction(this.flight);
  }

  ngOnDestroy(): void {
    this.unSubscribe$.next(true);
    this.unSubscribe$.complete();
  }

  get language(): string {
    return localStorage.getItem('lang');
  }

  public onClose(): void {
    this.dialogRef.close();
  }

  public onSubmit(): void {
    let cityIds = {
      excludedGeoChannelIds: this.excludedGeotarget,
      geoChannelIds: this.geotarget
    };
    this.lineItemService.changeGeo$(this.data, cityIds, this.activeItem.value, 'GEO')
        .pipe(takeUntil(this.unSubscribe$)).subscribe({
      next: (response) => {},
      error: (error) => {
        this.operationResult = 'danger';
        this.operationText = 'messages.operation.text.wrong';
        this.operationDone = true;
        this.disabled = false;
      },
      complete: () => {
        this.operationResult = 'success';
        this.operationText = 'messages.operation.text.success';
        this.operationDone = true;
        this.disabled = false;
      }
    })
  }

  public setActiveItem(event: MatTabChangeEvent): void {
    this.activeItem = this.tabSettings[event.index];
    this.destroyInfoPanel();
  }

  public createFlightEditGeotargetAction = (data: FlightModel | any): Action => {
    return {
      component: FlightEditGeotargetComponent,
      initializer: (instance: FlightEditGeotargetComponent) => {
        return new Promise(resolve => {
          instance.flightId = data.flightId;
          instance.geoChannelIds = data.geoChannelIds;
          instance.excludedGeoChannelIds = data.excludedGeoChannelIds;

          instance.geoAddressChange
              .pipe(takeUntil(this.unSubscribe$))
              .subscribe(geoAddress => {
            this.geoAddress = [...geoAddress];
          });
          instance.geotargetChange
              .pipe(takeUntil(this.unSubscribe$))
              .subscribe(geotarget => {
                this.geotarget = [...geotarget];
          });
          instance.excludedGeotargetChange
              .pipe(takeUntil(this.unSubscribe$))
              .subscribe(excludedGeotarget => {
                this.excludedGeotarget = [...excludedGeotarget];
          });
          instance.excludedGeoAddressChange
              .pipe(takeUntil(this.unSubscribe$))
              .subscribe(excludedGeoAddress => {
                this.excludedGeoAddress = [...excludedGeoAddress];
          });

          resolve(true);
        });
      }
    }
  };

  public destroyInfoPanel(): void {
    this.operationDone = false;
  }

  private resetGeoComponentData(): void {
    this.uiRender.componentRef.instance.locations = [];
    this.uiRender.componentRef.instance.excludedLocations = [];
    this.uiRender.componentRef.instance._ids_geoChannelIds = [];
    this.uiRender.componentRef.instance._id_excludedGeoChannelIds = []
    this.changeDetectorRef.detectChanges();
  }
}
