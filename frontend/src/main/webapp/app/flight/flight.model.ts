import {Flight, FlightFrequencyCap, FlightSchedule, FlightFrequencyCapWindow} from './flight';
import {moment, dateFormatShort}                                  from '../common/common.const';
import { GeoAddress }                                                         from '../common/geotarget';

export class FlightModel implements Flight {
    public id: number;
    public accountId:    number;
    public flightId:     number;
    public name:         string;
    public budget:       number = 0;
    public dateStart:    string = moment().format(dateFormatShort);
    public dateEnd:      string = null;
    public bidStrategy:  string = 'MAXIMISE_REACH';
    public minCtrGoal:   number = 0;
    public rateType:     string = 'CPC';
    public rateValue:    number = 0;
    public displayStatus:    string;
    public deliveryPacing:   string = "U";
    public dailyBudget:      number = 0;
    public frequencyCap:     FlightFrequencyCap = new FrequencyCaps();
    public version:          number;
    public version2:         number;
    public channelIds:       Array<number>  = [];
    public creativeIds:      Array<number>  = [];
    public siteIds:          Array<number>  = [];
    public conversionIds:    Array<number>  = [];
    public schedules:        Array<FlightSchedule>  = [];
    public specialChannelId: number;
    public specialChannelLinked: boolean = false;
    public blackList:        string;
    public whiteList:        string;
    public geoChannelIds:           Array<number>  = [];
    public excludedGeoChannelIds:   Array<number>  = [];
    public addresses:               Array<FlightGeoAddress>  = [];
    public excludedAddresses:       Array<FlightGeoAddress>  = [];
    public deviceChannelIds: Array<number>  = [];
    public emptyProps:       Array<any> = [];
}

export function dynamicBudget(flight: FlightModel, spentBudget: number): number {
    if (flight.deliveryPacing !== 'D' ||
        !flight.budget ||
        !flight.dateStart ||
        !flight.dateEnd) {
        return NaN;
    }

    let momentDateEnd = moment(flight.dateEnd);
    let currentMoment = moment().startOf('day');
    let momentDateStart = moment(flight.dateStart);
    momentDateStart = momentDateStart.diff(currentMoment, 'days') > 0 ? momentDateStart : currentMoment;
    if (!flight.dateStart || !flight.dateEnd || momentDateEnd.diff(momentDateStart, 'days') < 0) {
        return 0;
    }

    let weekDaysSelected = (!flight.schedules || flight.schedules.length == 0)? new Set([1, 2, 3, 4, 5, 6, 7]) :
        new Set(flight.schedules.map(s => toIsoDayOfWeek(s)));

    let daysNum = countDays(momentDateStart, momentDateEnd, weekDaysSelected);
    return daysNum == 0 ? 0 : (+flight.budget - spentBudget) / daysNum;
}

export class FrequencyCaps implements FlightFrequencyCap {
    public id: number;
    public windowCount:  number = null;
    public lifeCount:    number = null;
    public version:      number;
    public period:       FlightFrequencyCapWindow   = new FrequencyCapsWindow();
    public windowLength: FlightFrequencyCapWindow   = new FrequencyCapsWindow();
}


export class FrequencyCapsWindow implements FlightFrequencyCapWindow {
    public value:  number   = null;
    public unit:   string   = 'SECOND';
}

export class FlightGeoAddress implements GeoAddress {
    public id: number;
    public address: string;
    public latitude: number;
    public longitude: number;
    public radius: number;
    public radiusUnits: string;
}

function divAsInt(val1: number, val2: number): number {
    return (val1 - val1 % val2) / val2;
}

function toIsoDayOfWeek(schedule: any) : number {
    let minutesInDay = 1440;
    return divAsInt(parseInt(schedule.toString().split(':')[0]), minutesInDay) + 1;
}

function countDays(from: any, to: any, weekDaysSelected: Set<number>) : number {
    let daysNum = 0;
    let nextDay = from;
    while (to.diff(nextDay, 'days') >= 0) {
        daysNum += weekDaysSelected.has(nextDay.isoWeekday()) ? 1 : 0;
        nextDay = nextDay.add(1, 'days');
    }

    return daysNum;
}