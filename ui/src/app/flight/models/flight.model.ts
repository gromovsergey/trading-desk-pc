import {moment, dateFormatShort} from '../../common/common.const';

export class FlightModel implements Flight {
  public id: number;
  public accountId: number;
  public flightId: number;
  public name: string;
  public budget = 0;
  public dateStart: string = moment().format(dateFormatShort);
  public dateEnd: string = null;
  public bidStrategy = 'MAXIMISE_REACH';
  public minCtrGoal = 0;
  public rateType = 'CPM';
  public rateValue = 0;
  public displayStatus: string;
  public deliveryPacing = 'U';
  public dailyBudget = 0;
  public impressionsPacing: string = 'U'
  public impressionsDailyLimit: number = null
  public impressionsTotalLimit: number = null
  public clicksPacing: string = 'U'
  public clicksDailyLimit: number = null
  public clicksTotalLimit: number = null
  public frequencyCap: FlightFrequencyCap = new FrequencyCaps();
  public version: number;
  public version2: number;
  public channelIds: Array<number> = [];
  public creativeIds: Array<number> = [];
  public siteIds: Array<number> = [];
  public conversionIds: Array<number> = [];
  public schedules: Array<FlightSchedule> = [];
  public specialChannelId: number;
  public specialChannelLinked = false;
  public blackList: string;
  public whiteList: string;
  public geoChannelIds: Array<number> = [];
  public excludedGeoChannelIds: Array<number> = [];
  public addresses: Array<FlightGeoAddress> = [];
  public excludedAddresses: Array<FlightGeoAddress> = [];
  public deviceChannelIds: Array<number> = [];
  public emptyProps: Array<any> = [];
  public propsWithFlightValues: any[];
}

export function dynamicBudget(flight: FlightModel, spentBudget: number): number {
  if (flight.deliveryPacing !== 'D' ||
    !flight.budget ||
    !flight.dateStart ||
    !flight.dateEnd) {
    return NaN;
  }

  const momentDateEnd = moment(flight.dateEnd);
  const currentMoment = moment().startOf('day');
  let momentDateStart = moment(flight.dateStart);
  momentDateStart = momentDateStart.diff(currentMoment, 'days') > 0 ? momentDateStart : currentMoment;
  if (!flight.dateStart || !flight.dateEnd || momentDateEnd.diff(momentDateStart, 'days') < 0) {
    return 0;
  }

  const weekDaysSelected = (!flight.schedules || flight.schedules.length === 0) ? new Set([1, 2, 3, 4, 5, 6, 7]) :
    new Set(flight.schedules.map(s => toIsoDayOfWeek(s)));

  const daysNum = countDays(momentDateStart, momentDateEnd, weekDaysSelected);
  return daysNum === 0 ? 0 : (+flight.budget - spentBudget) / daysNum;
}

export class FrequencyCaps implements FlightFrequencyCap {
  public id: number;
  public windowCount: number = null;
  public lifeCount: number = null;
  public version: number;
  public period: FlightFrequencyCapWindow = new FrequencyCapsWindow();
  public windowLength: FlightFrequencyCapWindow = new FrequencyCapsWindow();
}

export class FrequencyCapsWindow implements FlightFrequencyCapWindow {
  public value: number = null;
  public unit = 'SECOND';
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

function toIsoDayOfWeek(schedule: any): number {
  const minutesInDay = 1440;
  return divAsInt(parseInt(schedule.toString().split(':')[0], 10), minutesInDay) + 1;
}

function countDays(from: any, to: any, weekDaysSelected: Set<number>): number {
  let daysNum = 0;
  let nextDay = from;
  while (to.diff(nextDay, 'days') >= 0) {
    daysNum += weekDaysSelected.has(nextDay.isoWeekday()) ? 1 : 0;
    nextDay = nextDay.add(1, 'days');
  }

  return daysNum;
}
