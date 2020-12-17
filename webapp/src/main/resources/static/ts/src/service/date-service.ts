import relativeTime from "dayjs/plugin/relativeTime";
import advancedFormat from "dayjs/plugin/advancedFormat";
import dayjs from "dayjs";

export class DateService {

    constructor() {
        dayjs.extend(relativeTime);
        dayjs.extend(advancedFormat);
    }

    public formatRelative(dateStr: string): string {
        return dayjs(dateStr).fromNow();
    }

    public format(dateStr: string, dateFormat: DateFormat): string {
        return dayjs(dateStr).format(dateFormat);
    }

    public yesterday(): string {
        const yesterdayAsDayJs = dayjs().subtract(1, "day");
        return this.format(yesterdayAsDayJs.toString(), DateFormat.UTC);
    }
}

export enum DateFormat {

    LONG = "MMMM Do YYYY",
    LONG_WITH_TIME = "MMMM Do YYYY, h:mm:ss a",
    SHORT = "MMM Do YY",
    SHORT_WITH_TIME = "MMM Do YY, h:mm:ss a",
    UTC = "YYYY-MM-DD"

}
