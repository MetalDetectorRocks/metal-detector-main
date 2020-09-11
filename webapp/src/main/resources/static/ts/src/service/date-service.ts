export class DateService {

    constructor() {
    }

    public calculateTimeRange(date: Date): number {
        const now = Date.now();
        const then = new Date(date).valueOf();
        return Math.round((then - now) / 1000 / 60 / 60 / 24);
    }
}
