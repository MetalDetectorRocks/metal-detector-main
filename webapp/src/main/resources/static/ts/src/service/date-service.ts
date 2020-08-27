import {Release} from "../model/release.model";

export class DateService {

    constructor() {
    }

    public calculatePastTimeRange(date: Date): number {
        const now = Date.now();
        const then = new Date(date).valueOf();
        return Math.round((now - then) / 1000 / 60 / 60 / 24);
    }

    public calculateFutureTimeRange(date: Date): number {
        const now = Date.now();
        const then = new Date(date).valueOf();
        return Math.round((then - now) / 1000 / 60 / 60 / 24);
    }

    public compareReleaseDates(release1: Release, release2: Release): number {
        const date1AsNumber = release1.releaseDate.valueOf();
        const date2AsNumber = release2.releaseDate.valueOf();

        if (date1AsNumber < date2AsNumber) {
            return -1;
        }
        if (date2AsNumber < date1AsNumber) {
            return 1;
        }
        return 0;
    }
}
