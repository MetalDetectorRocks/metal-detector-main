import relativeTime from "dayjs/plugin/relativeTime";
import advancedFormat from "dayjs/plugin/advancedFormat";
import dayjs, { UnitType } from "dayjs";

export class DateService {
    constructor() {
        dayjs.extend(relativeTime);
        dayjs.extend(advancedFormat);
    }

    public formatRelative(dateStr: string): string {
        return dayjs(dateStr).fromNow();
    }

    public formatRelativeInDays(dateStr: string): string {
        if (dateStr === this.today()) {
            return "today";
        } else if (dateStr === this.tomorrow()) {
            return "tomorrow";
        } else if (dateStr === this.yesterday()) {
            return "yesterday";
        } else {
            const relativeDisplayString = dayjs(dateStr).fromNow();
            const hoursFromNow = Math.abs(this.diffFromNow(dateStr, "hour"));
            if (relativeDisplayString.match(/in \d* days/)) {
                const daysFromNow = Math.ceil(hoursFromNow / 24);
                return `in ${daysFromNow} days`;
            } else if (relativeDisplayString.match(/\d days ago/)) {
                const daysFromNow = Math.floor(hoursFromNow / 24);
                return `${daysFromNow} days ago`;
            } else {
                return relativeDisplayString;
            }
        }
    }

    public format(dateStr: string, dateFormat: DateFormat): string {
        return dayjs(dateStr).format(dateFormat);
    }

    public today(): string {
        const todayAsDayJs = dayjs();
        return this.format(todayAsDayJs.toString(), DateFormat.UTC);
    }

    public yesterday(): string {
        const yesterdayAsDayJs = dayjs().subtract(1, "day");
        return this.format(yesterdayAsDayJs.toString(), DateFormat.UTC);
    }

    public tomorrow(): string {
        const yesterdayAsDayJs = dayjs().add(1, "day");
        return this.format(yesterdayAsDayJs.toString(), DateFormat.UTC);
    }

    public inAMonth(): string {
        const inAMonthAsDayJs = dayjs().add(1, "month");
        return this.format(inAMonthAsDayJs.toString(), DateFormat.UTC);
    }

    public beforeAMonth(): string {
        const beforeAMonth = dayjs().subtract(1, "month");
        return this.format(beforeAMonth.toString(), DateFormat.UTC);
    }

    public compare(dateStr1: string, dateStr2: string): number {
        return dayjs(dateStr1).isBefore(dateStr2) ? -1 : dayjs(dateStr2).isBefore(dateStr1) ? 1 : 0;
    }

    private diffFromNow(dateStr: string, unit: UnitType): number {
        const date = dayjs(dateStr);
        const now = dayjs();
        return now.diff(date, unit);
    }
}

export enum DateFormat {
    LONG = "MMMM Do YYYY",
    LONG_WITH_TIME = "MMMM Do YYYY, h:mm:ss a",
    SHORT = "MMM Do YYYY",
    SHORT_WITH_TIME = "MMM Do YY, h:mm:ss a",
    UTC = "YYYY-MM-DD",
}
