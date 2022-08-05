import { NgModule }     from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule }  from '@angular/forms';

import { SharedModule }                       from '../shared/shared.module';
import { CreativeModule }                     from '../creative/creative.module';
import { ChartModule }                        from '../chart/chart.module';

import { AgencyService }                      from '../agency/agency.service';
import { AdvertiserService }                  from '../advertiser/advertiser.service';
import { ChannelService }                     from '../channel/channel.service';
import { LineItemService }                    from '../lineitem/lineitem.service';
import { ConversionService }                  from '../conversion/conversion.service';
import { FlightRoutingModule }                from './flight-routing.module';
import { FlightService }                      from './flight.service';
import { FlightComponent }                    from './flight.component';
import { FlightChannelsComponent }            from './flight_channels.component';
import { FlightCreativesComponent }           from './flight_creatives.component';
import { FlightEditComponent }                from './flight_edit.component';
import { FlightEditConversionsComponent }     from './flight_edit_conversiontracking.component';
import { FlightEditDevicesComponent }         from './flight_edit_devices.component';
import { FlightEditDscheduleComponent }       from './flight_edit_dschedule.component';
import { FlightEditFrequencyCapsComponent }   from './flight_edit_frequencycaps.component';
import { FlightEditGeotargetComponent }       from './flight_edit_geotarget.component';
import { FlightEditInventorysourceComponent } from './flight_edit_inventorysource.component';
import { FlightEditMainComponent }            from './flight_edit_main.component';
import { FlightLineItemsComponent }           from './flight_lineitems.component';
import { FlightScanIOComponent }              from './flight_scan_io.component';
import { FlightSitesComponent }               from './flight_sites.component';
import { FlightSummaryComponent }             from './flight_summary.component';
import { FlightTabsComponent }                from './flight_tabs.component';
import { NumFormatDirective }                 from './numFormat.directive';

@NgModule({
    imports:      [
        CommonModule,
        FormsModule,
        SharedModule,
        CreativeModule,
        ChartModule,
        FlightRoutingModule
    ],
    providers:    [
        FlightService,
        AgencyService,
        AdvertiserService,
        ChannelService,
        LineItemService,
        ConversionService
    ],
    declarations: [
        FlightComponent,
        FlightChannelsComponent,
        FlightCreativesComponent,
        FlightEditComponent,
        FlightEditConversionsComponent,
        FlightEditDevicesComponent,
        FlightEditDscheduleComponent,
        FlightEditFrequencyCapsComponent,
        FlightEditGeotargetComponent,
        FlightEditInventorysourceComponent,
        FlightEditMainComponent,
        FlightLineItemsComponent,
        FlightScanIOComponent,
        FlightSitesComponent,
        FlightTabsComponent,
        FlightSummaryComponent,
        NumFormatDirective
    ],
    exports: [
        FlightChannelsComponent,
        FlightCreativesComponent,
        FlightLineItemsComponent,
        FlightSummaryComponent,
        FlightSitesComponent,
        FlightTabsComponent,
        FlightEditComponent
    ]
})

export class FlightModule {}
