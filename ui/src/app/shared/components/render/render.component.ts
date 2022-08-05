import {
  Component,
  ComponentFactoryResolver,
  ComponentRef,
  Input,
  OnInit, Type,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import {SubscriptionLike} from "rxjs";

export type ComponentRendererComponentUpdater<TComponent, TData, TRendererData> = (componentInstance: TComponent, data: TData, rowData?: any) => (() => void) | void;

@Component({
  selector: 'ui-render',
  template: `<ng-container #container></ng-container>`
})
export class RenderComponent<TComponent, TData> implements OnInit {

  @Input() component: Type<TComponent>;
  @Input() updater?: ComponentRendererComponentUpdater<TComponent, TData, any>;
  @Input() set data(data: TData) { this.setData(data); }
  @Input() rendererData: any;
  @ViewChild('container', { read: ViewContainerRef, static: true }) container: ViewContainerRef;

  public componentRef: ComponentRef<TComponent>;
  private _data: TData;
  private initialized = false;
  private unsubscriber: (() => void) | SubscriptionLike | void;

  constructor(private componentFactoryResolver: ComponentFactoryResolver) { }

  ngOnInit(): void {
    this.renderComponent();
  }

  private renderComponent() {
    if (this.component) {
      const factory = this.componentFactoryResolver.resolveComponentFactory(this.component);
      this.componentRef = this.container.createComponent(factory);
      this.initialized = true;
      this.setData(this._data);
    }
  }

  private setData(data: TData) {
    this._data = data;
    if (this.initialized && this.updater && this.componentRef) {
      this.unsubscribe();
      this.unsubscriber = this.updater(this.componentRef.instance, this._data, this.rendererData);
    }
  }

  private unsubscribe() {
    if (typeof this.unsubscriber === 'function') {
      this.unsubscriber();
    } else if (this.unsubscriber && this.unsubscriber.unsubscribe) {
      this.unsubscriber.unsubscribe();
    }
    this.unsubscriber = null;
  }

}
