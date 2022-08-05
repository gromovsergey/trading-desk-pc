import { FlightGeoAddress } from './flight.model';

export interface Flight {
    'id': number,
    'accountId':    number,
    'flightId':     number,
    'name':         string,
    'budget':       number,
    'dateStart':    string,
    'dateEnd':      string,
    'bidStrategy':  string,
    'minCtrGoal':   number,
    'rateType':     string,
    'blackList':        string,
    'whiteList':        string,
    'rateValue':        number,
    'displayStatus':    string,
    'deliveryPacing':   string,
    'dailyBudget':      number,
    'frequencyCap':     FlightFrequencyCap,
    'version':          number,
    'version2':         number,
    'channelIds':       Array<number>,
    'creativeIds':      Array<number>,
    'siteIds':          Array<number>,
    'conversionIds':    Array<number>,
    'deviceChannelIds': Array<number>,
    'geoChannelIds':            Array<number>,
    'excludedGeoChannelIds':    Array<number>,
    'addresses':                Array<FlightGeoAddress>,
    'excludedAddresses':        Array<FlightGeoAddress>,
    'schedules':        Array<FlightSchedule>,
    'specialChannelId': number,
    'specialChannelLinked' : boolean,
    'emptyProps':       Array<any>
}

export interface FlightFrequencyCap {
    'id': number,
    'windowCount':  number,
    'lifeCount':    number,
    'version':      number,
    'period':       FlightFrequencyCapWindow,
    'windowLength': FlightFrequencyCapWindow
}

export interface FlightFrequencyCapWindow {
    value:  number,
    unit:   string
}

export interface FlightSchedule {
    id:         number,
    timeFrom:   number,
    timeTo:     number
}