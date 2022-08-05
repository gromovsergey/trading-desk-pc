export class BehavioralParameters {
  minimumVisits: number;
  timeFrom: number;
  timeTo: number;
  triggerType: string;

  constructor(
    minimumVisits: number,
    timeFrom: number,
    timeTo: number,
    triggerType: string
  ) {
    this.minimumVisits = minimumVisits;
    this.timeFrom = timeFrom;
    this.timeTo = timeTo;
    this.triggerType = triggerType;
  }
}
