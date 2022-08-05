import {dateFormatParse, moment} from '../../common/common.const';

export class ReportParametersModel implements ReportParameters {
  accountId: number = null;
  dateStart: string = moment().format(dateFormatParse);
  dateEnd: string = moment().format(dateFormatParse);
  selectedColumns: Array<string> = [];
}

export class PublisherReportParametersModel extends ReportParametersModel implements PublisherReportParameters {
  accountId: number = null;
}

export class AdvertiserReportParametersModel extends ReportParametersModel implements AdvertiserReportParameters {
  accountId: number = null;
  flightIds: Array<number> = null;
}

export class ConversionsReportParametersModel extends ReportParametersModel implements ConversionsReportParameters {
  accountId: number = null;
  flightIds: Array<number> = null;
  lineItemIds: Array<number> = null;
  conversionIds: Array<number> = null;
}

export class SegmentsReportParametersModel extends ReportParametersModel implements SegmentsReportParameters {
  accountId: number = null;
  flightIds: Array<number> = null;
  lineItemIds: Array<number> = null;
}

export class DomainsReportParametersModel extends ReportParametersModel implements DomainsReportParameters {
  accountId: number = null;
}

export class ReferrerReportParametersModel extends ReportParametersModel implements ReferrerReportParameters {
  accountId: number = null;
  tagIds: number[] = null;
}

export class DetailedReportParametersModel extends ReportParametersModel implements DetailedReportParameters {
  advertiserAccountId: number = null;
  publisherAccountId: number = null;
}

export class ReportMetaModel implements ReportMeta {
  available: string[] = [];
  defaults: string[] = [];
  required: string[] = [];
  columnsInfo: ReportColumn[] = [];
}
