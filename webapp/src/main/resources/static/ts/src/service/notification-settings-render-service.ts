import {AbstractRenderService} from "./abstract-render-service";
import {AlertService} from "./alert-service";
import {LoadingIndicatorService} from "./loading-indicator-service";
import {NotificationSettings} from "../model/notification-settings.model";

export class NotificationSettingsRenderService extends AbstractRenderService<NotificationSettings> {

    private regularNotificationToggle!: HTMLInputElement;
    private twoWeeklyFrequencyRb!: HTMLInputElement;
    private fourWeeklyFrequencyRb!: HTMLInputElement;
    private releaseDateNotificationCb!: HTMLInputElement;
    private announcementDateNotificationCb!: HTMLInputElement;

    constructor(alertService: AlertService, loadingIndicatorService: LoadingIndicatorService) {
        super(alertService, loadingIndicatorService);
        this.initDocumentElements();
    }

    private initDocumentElements(): void {
        this.regularNotificationToggle = document.getElementById("notification-toggle") as HTMLInputElement;
        this.twoWeeklyFrequencyRb = document.getElementById("2-weekly-rb") as HTMLInputElement;
        this.fourWeeklyFrequencyRb = document.getElementById("4-weekly-rb") as HTMLInputElement;
        this.releaseDateNotificationCb = document.getElementById("release-date-notification-cb") as HTMLInputElement;
        this.announcementDateNotificationCb = document.getElementById("announcement-date-notification-cb") as HTMLInputElement;
    }

    protected getHostElementId(): string {
        return "notification-settings-container";
    }

    protected onRendering(notificationSettings: NotificationSettings): void {
        console.log(notificationSettings);

    }
}
