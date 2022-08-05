import { Component, Input } from '@angular/core';
import { RouterModule }     from '@angular/router';

import { IconComponent }          from './icon.component';
import { DisplayStatusDirective } from './display_status.directive';

@Component({
    selector: 'ui-dropdown-btn',
    template: `<div class="dropdown {{cssClass}}" [ngClass]="{'h-inline-block': isInline}">
              <button class="btn {{btnCssClass}}" [ngClass]="{'btn-default': !btnCssClass}" [displayStatusType]="'bg'" [displayStatus]="status" (click)="showMenu($event)" (blur)="hideMenu($event)">
                  <ui-icon *ngIf="icon" [icon]="icon.type" [ngStyle]="{color: icon.color}"></ui-icon>
                  {{title}}
                  <ui-icon *ngIf="menu" [icon]="'caret-down'"></ui-icon>
              </button>
              <ul *ngIf="menu" class="dropdown-menu" [ngClass]="{show: menuVisible}">
                  <li [ngClass]="{active: item.active}" *ngFor="let item of menu">
                      <a *ngIf="item.link && !item.onclick" [routerLink]="[item.link]">{{item.text}}</a>
                      <a *ngIf="!item.link && item.onclick" href="#" (click)="menuClick($event, item)">{{item.text}}</a>
                  </li>
              </ul>
              </div>`
})

export class DropdownButtonComponent {

    @Input() title: string;
    @Input() status: string;
    @Input() icon: DropdownButtonIcon;
    @Input() menu: Array<DropdownButtonMenuItem>;
    @Input('class') cssClass: string;
    @Input('btnClass') btnCssClass: string;
    @Input() isInline: boolean  = false;

    private menuVisible: boolean    = false;

    public showMenu(e: any){
        e.stopPropagation();
        e.preventDefault();

        this.menuVisible    = true;
    }

    public hideMenu(e: any){
        setTimeout(()=>{
            this.menuVisible    = false;
        }, 200);
    }

    private menuClick(e: any, item?: DropdownButtonMenuItem){
        e.stopPropagation();
        e.preventDefault();

        this.menu.forEach((v)=>{
            v.deactivate();
        });

        if (item.onclick !== undefined) {
            item.activate();
            item.onclick.call(this);
        }
    }
}

export class DropdownButtonIcon {
    type: string;
    color: string;
}

export class DropdownButtonMenuItem {

    public text: string;
    public operation: string;
    public settings: {};

    constructor(text: string, settings?: {}, operation?: string){
        this.text       = text;
        this.operation  = operation;
        this.settings   = Object.assign({
            active: false
        }, settings);
    }

    get link(): string{
        return this.settings['link'] || '';
    }

    set onclick(f: any){
        this.settings['onclick']    = f;
    }

    get onclick(): any{
        return this.settings['onclick'] || '';
    }

    get active(): boolean{
        return this.settings['active'] || false;
    }

    activate() {
        this.settings['active'] = true;
    }

    deactivate() {
        this.settings['active'] = false;
    }
}
