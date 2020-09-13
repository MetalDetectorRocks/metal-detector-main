import moment from "moment";

export class DateFormatService {

    constructor() {
    }

    public formatRelative(dateStr: string): string {
        return moment(dateStr).fromNow();
    }

    public format(dateStr: string, dateFormat: DateFormat): string {
        return moment(dateStr).format(dateFormat);
    }
}

export enum DateFormat {

    LONG = "MMMM Do YYYY",
    LONG_WITH_TIME = "MMMM Do YYYY, h:mm:ss a",
    SHORT = "MMM Do YY",
    SHORT_WITH_TIME = "MMM Do YY, h:mm:ss a",
    UTC = "YYYY-MM-DD"

}
