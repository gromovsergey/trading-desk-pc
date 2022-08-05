import {Component, Input, Output, EventEmitter, OnChanges, OnInit, OnDestroy} from '@angular/core';
import {FlightService} from '../../../services/flight.service';
import {FlightModel} from '../../../models/flight.model';
import {AdvertiserSessionModel} from '../../../../advertiser/models';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {MatSelectionListChange} from '@angular/material/list';

@Component({
  selector: 'ui-flight-edit-devices',
  templateUrl: 'flight-edit-devices.component.html',
  styleUrls: ['./flight-edit-devices.component.scss']
})
export class FlightEditDevicesComponent implements OnChanges, OnInit {

  @Input() flight: FlightModel;
  @Output() deviceChange = new EventEmitter();

  wait: boolean;
  devices: Array<any> = [];
  smartphones: any;
  tablets: any;
  devicesNonMobile: any;
  devicesApplications: any;
  isApplicationsSelected: boolean;

  constructor(private flightService: FlightService) {}

  ngOnInit(): void {
    this.wait = true;
    this.flightService.getDeviceTree(new AdvertiserSessionModel().id)
        .then(devices => {
          this.setDevicesRoots(devices);
          this.onLoadCheck(this.devices);

          this.wait = false;
        });
  }

  // Does not work with dynamic components' creation...
  ngOnChanges(): void {}

  setDevicesRoots(devices: Array<any>): void {
    for (const dev of devices) {
      if (dev.name === 'Applications') {
        this.devicesApplications = dev;
        this.devices.push(dev);
      } else if (dev.name === 'Non-mobile Devices') {
        this.devicesNonMobile = dev;
        this.devices.push(dev);
      } else if (dev.name === 'Smartphones') {
        this.smartphones = dev;
        this.devices.push(dev);
      } else if (dev.name === 'Tablet Computers') {
        this.tablets = dev;
        this.devices.push(dev);
      } else if (dev.name === 'Browsers' ||
        dev.name === 'Mobile Devices') {
        if (dev.children) {
          this.setDevicesRoots(dev.children);
        }
      }
    }
  }

  onLoadCheck(devices: Array<any>): void {
    if (devices && devices.length) {
      if (this.flight.deviceChannelIds.length === 0) {
        devices.forEach(v => {
          v.checked = true;
          if (v.children) {
            v.children.forEach(c => {
              c.checked = true;
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

  checkRecursive(item, checked?: boolean): void {
    const ch = (checked !== undefined) ? checked : (this.flight.deviceChannelIds.indexOf(item.id) !== -1);

    item.checked = ch;
    if (item.children) {
      item.children.forEach(v => {
        if (checked === undefined && !ch) {
          this.checkRecursive(v);
        } else {
          this.checkRecursive(v, ch);
        }
      });
    }
  }

  isCheckedChildrenRecursive(item): boolean {
    let b = true;

    if (item.children) {
      item.children.forEach(v => {
        b = b && v.checked && this.isCheckedChildrenRecursive(v);
      });
    }
    return b;
  }

  checkItem(e: MatSelectionListChange): void {
    const item = e.option.value;
    item.checked = !item.checked;
    if (this.devices && this.devices.length) {
      this.devices.forEach(v => {
        v.checked = this.isCheckedChildrenRecursive(v);
      });
    }
    this.changeEmit();
  }

  toggleAll(e: MatCheckboxChange, item: string): void {
    this.checkRecursive(item, e.checked);
    this.changeEmit();
  }

  toggleApplications(e: MatCheckboxChange): void {
    this.isApplicationsSelected = e.checked;
    this.changeEmit();
  }

  changeEmit(): void {
    const ids = [];
    if (!(this.smartphones.checked &&
      this.tablets.checked &&
      this.devicesNonMobile.checked &&
      this.isApplicationsSelected)) {
      const pushIds = (item) => {
        if (item.checked) {
          ids.push(item.id);
        } else if (item.children) {
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
    this.deviceChange.emit(ids);
  }

  someComplete(item: any): boolean {
    if (item.children && item.children.length) {
      const checked = item.children.filter(child => child ? child.checked : false).length;
      return checked > 0 && checked < item.children.length;
    }
    return false;
  }
}
