export class BehavioralParameters {
    public minimumVisits: number;
    public timeFrom: number;
    public timeTo: number;
    public triggerType: string;

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