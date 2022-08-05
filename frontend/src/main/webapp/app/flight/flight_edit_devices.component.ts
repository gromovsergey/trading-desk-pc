import { Component, Input, Output, EventEmitter } from '@angular/core';

import { AdvertiserSessionModel } from '../advertiser/advertiser_session.model';
import { FlightService }          from './flight.service';
import { FlightModel }            from './flight.model';

@Component({
    selector: 'ui-flight-edit-devices',
    templateUrl: 'devices.html'
})

export class FlightEditDevicesComponent {

    @Input() flight: FlightModel;
    @Output() onChange  = new EventEmitter();

    public wait: boolean;
    public devices: Array<any> = [];
    private smartphones: Array<any>;
    private tablets: Array<any>;
    private devicesNonMobile: Array<any>;
    private devicesApplications: any;
    private isApplicationsSelected: boolean;


    constructor(private flightService: FlightService){}

    ngOnChanges() {
        this.wait = true;
        this.flightService.getDeviceTree(new AdvertiserSessionModel().id)
            .then(devices => {
                this.setDevicesRoots(devices);
                this.onLoadCheck(this.devices);

                this.wait = false;
            })
    }

    private setDevicesRoots(devices: Array<any>) {
        for (let dev of devices) {
            if (dev.name === 'Applications') {
                this.devicesApplications = dev;
                this.devices.push(dev);
            }
            else if (dev.name === 'Non-mobile Devices') {
                this.devicesNonMobile = dev;
                this.devices.push(dev);
            }
            else if (dev.name === 'Smartphones') {
                    this.smartphones = dev;
                    this.devices.push(dev);
            }
            else if (dev.name === 'Tablet Computers') {
                this.tablets = dev;
                this.devices.push(dev);
            }
            else if (dev.name === 'Browsers' ||
                     dev.name === 'Mobile Devices') {
                if (dev.children) {
                    this.setDevicesRoots(dev.children);
                }
            }
        }
    }

    private onLoadCheck(devices: Array<any>){
        if (devices && devices.length){
            if (this.flight.deviceChannelIds.length === 0){
                devices.forEach(v => {
                    v.checked   = true;
                    if (v.children) {
                        v.children.forEach(c => {
                            c.checked   = true;
                        });
                    }
                });
                this.isApplicationsSelected = true;
            } else {
                devices.forEach(v => {
                    this.checkRecursive(v);
                });
                this.isApplicationsSelected = this.devicesApplications &&
                    this.flight.deviceChannelIds.includes(this.devicesApplications.id);
            }
        }
    }

    private checkRecursive(item, checked?: boolean){
        let ch  = (checked !== undefined) ? checked : (this.flight.deviceChannelIds.indexOf(item.id) !== -1);

        item.checked    = ch;
        if (item.children){
            item.children.forEach(v => {
                if (checked === undefined && !ch){
                    this.checkRecursive(v);
                } else {
                    this.checkRecursive(v, ch);
                }
            });
        }
    }

    private isCheckedChildrenRecursive(item){
        let b = true;

        if (item.children){
            item.children.forEach(v => {
                b = b && v.checked && this.isCheckedChildrenRecursive(v);
            });
        }
        return b;
    }

    private checkItem(e: any, item: any) {
        e.preventDefault();
        item.checked = !item.checked;
        if (this.devices && this.devices.length) {
            this.devices.forEach(v => {
                v.checked = this.isCheckedChildrenRecursive(v);
            });
        }
        this.changeEmit();
    }

    private toggleAll(e: any, item: string){
        this.checkRecursive(item, e.target.checked);
        this.changeEmit();
    }

    private toggleApplications(e: any) : void {
        this.isApplicationsSelected = !this.isApplicationsSelected;
        this.changeEmit();
    }

    private changeEmit(){
        let ids = [];
        if (!(this.smartphones['checked'] &&
                this.tablets['checked'] &&
                this.devicesNonMobile['checked'] &&
                this.isApplicationsSelected)){
            let pushIds = (item) => {
                if (item.checked) {
                    ids.push(item.id);
                } else if (item.children){
                    item.children.forEach(v => {
                        pushIds(v);
                    });
                }
            };
            pushIds(this.smartphones);
            pushIds(this.tablets);
            pushIds(this.devicesNonMobile);

            if (this.devicesApplications && this.isApplicationsSelected) {
                ids.push(this.devicesApplications.id);
            }
        }
        this.onChange.emit(ids);
    }
}
