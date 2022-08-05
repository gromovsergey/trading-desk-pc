import {
    AdvertiserReportParameters,
    ConversionsReportParameters,
    DomainsReportParameters,
    PublisherReportParameters,
    ReferrerReportParameters,
    DetailedReportParameters,
    ReportColumn,
    ReportMeta,
    ReportParameters, SegmentsReportParameters
} from './report';
import {dateFormatParse, moment} from "../common/common.const";

export class ReportParametersModel implements ReportParameters {
    public accountId: number = null;
    public dateStart: string = moment().format(dateFormatParse);
    public dateEnd: string = moment().format(dateFormatParse);
    public selectedColumns: Array<string> = [];
}

export class PublisherReportParametersModel extends ReportParametersModel implements PublisherReportParameters {
}

export class AdvertiserReportParametersModel extends ReportParametersModel implements AdvertiserReportParameters {
    public flightIds: Array<number> = null;
}

export class ConversionsReportParametersModel extends ReportParametersModel implements ConversionsReportParameters {
    public flightIds: Array<number> = null;
    public lineItemIds: Array<number> = null;
    public conversionIds: Array<number> = null;
}

export class DomainsReportParametersModel extends ReportParametersModel implements DomainsReportParameters {
}

export class ReferrerReportParametersModel extends ReportParametersModel implements ReferrerReportParameters {
    public tagIds: Array<number> = null;
}

export class DetailedReportParametersModel extends ReportParametersModel implements DetailedReportParameters {
    public advertiserAccountId = null;
    public publisherAccountId = null;
}

export class SegmentsReportParametersModel extends ReportParametersModel implements SegmentsReportParameters {
    public lineItemId = null;
}

export class ReportMetaModel implements ReportMeta {
    public available: Array<string> = [];
    public defaults: Array<string> = [];
    public required: Array<string> = [];
    public columnsInfo: Array<ReportColumn> = [];
}
