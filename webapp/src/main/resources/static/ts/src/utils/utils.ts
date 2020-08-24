export class Utils {

    static calculateTimeRange(date: Date) {
        const now = Date.now();
        const then = new Date(date).valueOf();
        return Math.round((now - then) / 1000 / 60 / 60 / 24);
    }
}