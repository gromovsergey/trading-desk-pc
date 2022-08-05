import {FlightComponent} from './flight/flight.component';
import {FlightSummaryComponent} from './flight-summary/flight-summary.component';
import {FlightChannelsComponent} from './flight-channels/flight-channels.component';
import {FlightCreativesComponent} from './flight-creatives/flight-creatives.component';
import {FlightLineItemsComponent} from './flight-lineitems/flight-lineitems.component';
import {FlightSitesComponent} from './flight-sites/flight-sites.component';
import {FlightTabsComponent} from './flight-tabs/flight-tabs.component';
import {FlightEditComponent} from './flight-edit/flight-edit.component';
import {FlightEditConversionsComponent} from './flight-edit/flight-edit-conversion-tracking.component/flight-edit-conversion-tracking.component';
import {FlightEditDevicesComponent} from './flight-edit/flight-edit-devices/flight-edit-devices.component';
import {FlightEditDscheduleComponent} from './flight-edit/flight-edit-delivery-schedule/flight-edit-delivery-schedule.component';
import {FlightEditFrequencyCapsComponent} from './flight-edit/flight-edit-frequency-caps/flight-edit-frequency-caps.component';
import {FlightEditLimitsComponent} from './flight-edit/flight-edit-limits/flight-edit-limits.component'
import {FlightEditGeotargetComponent} from './flight-edit/flight-edit-geotarget/flight-edit-geotarget.component';
import {FlightEditInventorySourceComponent} from './flight-edit/flight-edit-inventory-source/flight-edit-inventory-source.component';
import {FlightEditMainComponent} from './flight-edit/flight-edit-main/flight-edit-main.component';
import {FlightScanIOComponent} from './flight-scan-io/flight-scan-io.component';
import {FlightCreativesConnectComponent} from './flight-creatives-connect/flight-creatives-connect.component';
import {FlightSitesConnectComponent} from './flight-sites-connect/flight-sites-connect.component';
import {FlightChannelsTreeComponent} from './flight-channels-tree/flight-channels-tree.component';
import {FlightChannelTreeSearchComponent} from "./flight-channel-tree-search/flight-channel-tree-search.component";
import {FlightGeotargetComponent} from "./flight/flight-geotarget/flight-geotarget.component";
import {SiteTargetingEditComponent} from "./flight-lineitems/site-targeting-edit/site-targeting-edit.component";
import {GeoTargetingEditComponent} from "./flight-lineitems/geo-targeting-edit/geo-targeting-edit.component";
import {DeviceEditComponent} from "./flight-lineitems/device-edit/device-edit.component";
import {RatesEditComponent} from "./flight-lineitems/rates-edit/rates-edit.component";

export const FLIGHT_EXPORTED_COMPONENTS = [
  FlightSummaryComponent,
  FlightChannelsComponent,
  FlightChannelsTreeComponent,
  FlightCreativesComponent,
  FlightLineItemsComponent,
  FlightSitesComponent,
  FlightTabsComponent,
  FlightEditComponent,
  FlightSitesConnectComponent,
  FlightChannelTreeSearchComponent,
  FlightGeotargetComponent,
  FlightEditMainComponent,
  FlightEditGeotargetComponent,
  FlightEditDscheduleComponent,
  FlightEditConversionsComponent,
  FlightEditDscheduleComponent,
  FlightEditConversionsComponent,
  FlightEditDevicesComponent,
  FlightEditInventorySourceComponent,
  FlightEditFrequencyCapsComponent,
  FlightEditLimitsComponent,
];

export const FLIGHT_COMPONENTS = [
  ...FLIGHT_EXPORTED_COMPONENTS,
  FlightComponent,
  FlightEditConversionsComponent,
  FlightEditDevicesComponent,
  FlightEditDscheduleComponent,
  FlightEditFrequencyCapsComponent,
  FlightEditLimitsComponent,
  FlightGeotargetComponent,
  FlightEditGeotargetComponent,
  FlightEditInventorySourceComponent,
  FlightEditMainComponent,
  FlightScanIOComponent,
  FlightCreativesConnectComponent,
  SiteTargetingEditComponent,
  GeoTargetingEditComponent,
  DeviceEditComponent,
  RatesEditComponent
];


