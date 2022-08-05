import {AdvertiserService} from './advertiser.service';
import {AdvertiserReportService} from './advertiser_report.service';
import {ConversionsReportService} from './conversions_report.service';
import {SegmentsReportService} from './segments_report.service';
import {DomainsReportService} from './domains_report.service';

export const ADVERTISER_SERVICES = [
  AdvertiserService,
  AdvertiserReportService,
  ConversionsReportService,
  SegmentsReportService,
  DomainsReportService
];
