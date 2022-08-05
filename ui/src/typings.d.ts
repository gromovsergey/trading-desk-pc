declare interface User {
  accountId: number;
  id: number;
  key: string;
  role: string;
  token: string;
}

declare interface AgencySearchParams {
  countries: AgencyCountry[];
  displayStatuses: AgencyDisplayStatus[];
  accountRole: AgencyRole[];
}

declare interface AgencyDisplayStatus {
  name: string;
  type: string;
}

declare interface AgencyRole {
  name: string;
  roleId: string;
}

declare interface AgencyCountry {
  name: string;
  code: string;
}

declare interface AgencyModel {
  id: number;
  name: string;
  displayStatus: string;
  currencyCode: string;
  countryCode: string;
  timeZone: string;
  prepaidAmount: number;
  selfServiceCommission: number;
  selfServiceFlag: boolean;
}

declare interface UserModel {
  id?: number;
  accountId: number;
  roleId: number;
  token?: string;
  key?: string;
  role?: string;
  email?: string;
  firstName: string;
  lastName: string;
  advertiserIds: number[];
  date_token?: Date;
}

declare interface UserRoleModel {
  id: number;
  name: string;
  advLevelAccessAvailable: boolean;
}

declare interface CustomDateRange {
  dateStart: string;
  dateEnd: string;
  value?: string;
}

declare interface IdName {
  id: number;
  name: string;
  siteId?: number;
}

declare interface AudienceResearchStat {
   channelName: string;
   dates: string[];
   ticks: string[];
   values: number[][];
   lastDate: string;
   lastValues: number[];
}

declare interface AccountModel {
  id: number;
  role: string;
  countryCode: string;
}

declare interface ChannelSearchModel {
  name: string;
  accountId: string;
  channelType: string;
  visibility: string;
}

declare interface CountryCode {
  name: string;
  code: string;
}

declare interface GeoPlace {
  id: number;
  name: string;
}

declare interface GeoAddress {
  id: number;
  address: string;
  latitude: number;
  longitude: number;
  radius: number;
  radiusUnits: string;
}

declare interface Creative {
  id: number;
  accountId: number;
  agencyId: number;
  sizeId: number;
  templateId: number;
  width: number;
  height: number;
  version: number;
  name: string;
  sizeName: string;
  templateName: string;
  displayStatus: string;
  expansion: string;
  options: CreativeOption[];
  contentCategories: CreativeContentCategory[];
  visualCategories: any[];
  expandable: boolean;
}

declare interface CreativeOption {
  id: number;
  token: string;
  value: string;
}

declare interface CreativeContentCategory {
  id: number;
  name: string;
}

declare interface IdName {
  id: number;
  name: string;
}

declare interface ReportColumn {
  id: string;
  name: string;
  location: string;
  selected?: boolean;
}

declare interface ReportMeta {
  available: string[];
  defaults: string[];
  required: string[];
  columnsInfo: ReportColumn[];
}

declare interface ReportParameters {
  dateStart: string;
  dateEnd: string;
  selectedColumns: string[];
}

declare interface AdvertiserReportParameters extends ReportParameters {
  accountId: number;
  flightIds: number[];
}

declare interface ConversionsReportParameters extends ReportParameters {
  accountId: number;
  flightIds: number[];
  lineItemIds: number[];
  conversionIds: number[];
}

declare interface SegmentsReportParameters extends ReportParameters {
  accountId: number;
  flightIds: number[];
  lineItemIds: number[];
}

declare interface DomainsReportParameters extends ReportParameters {
  accountId: number;
}

declare interface PublisherReportParameters extends ReportParameters {
  accountId: number;
}

declare interface ReferrerReportParameters extends ReportParameters {
  accountId: number;
  tagIds: number[];
}

declare interface DetailedReportParameters extends ReportParameters {
  advertiserAccountId: number;
  publisherAccountId: number;
}

declare interface Flight {
  id: number;
  accountId: number;
  flightId: number;
  name: string;
  budget: number;
  dateStart: string;
  dateEnd: string;
  bidStrategy: string;
  minCtrGoal: number;
  rateType: string;
  blackList: string;
  whiteList: string;
  rateValue: number;
  displayStatus: string;
  deliveryPacing: string;
  dailyBudget: number;
  frequencyCap: FlightFrequencyCap;
  version: number;
  version2: number;
  channelIds: Array<number>;
  creativeIds: Array<number>;
  siteIds: Array<number>;
  conversionIds: Array<number>;
  deviceChannelIds: Array<number>;
  geoChannelIds: Array<number>;
  excludedGeoChannelIds: Array<number>;
  addresses: GeoAddress[];
  excludedAddresses: GeoAddress[];
  schedules: FlightSchedule[];
  specialChannelId: number;
  specialChannelLinked: boolean;
  emptyProps: Array<any>;
  propsWithFlightValues?: any;
}

declare interface FlightFrequencyCap {
  id: number;
  windowCount: number;
  lifeCount: number;
  version: number;
  period: FlightFrequencyCapWindow;
  windowLength: FlightFrequencyCapWindow;
}

declare interface FlightFrequencyCapWindow {
  value: number;
  unit: string;
}

declare interface FlightSchedule {
  id: number;
  timeFrom: number;
  timeTo: number;
}
