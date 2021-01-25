import {AbstractRenderService} from "./abstract-render-service";
import {AlertService} from "./alert-service";
import {LoadingIndicatorService} from "./loading-indicator-service";
import {NotificationSettings} from "../model/notification-settings.model";
import {NotificationSettingsRestClient} from "../clients/notification-settings-rest-client";

export class NotificationSettingsRenderService extends AbstractRenderService<NotificationSettings> {

    private readonly notificationSettingsRestClient: NotificationSettingsRestClient;

    private regularNotificationToggle!: HTMLInputElement;
    private twoWeeklyFrequencyRb!: HTMLInputElement;
    private fourWeeklyFrequencyRb!: HTMLInputElement;
    private releaseDateNotificationCb!: HTMLInputElement;
    private announcementDateNotificationCb!: HTMLInputElement;

    constructor(notificationSettingsRestClient: NotificationSettingsRestClient,
                alertService: AlertService,
                loadingIndicatorService: LoadingIndicatorService) {
        super(alertService, loadingIndicatorService);
        this.notificationSettingsRestClient = notificationSettingsRestClient;
        this.initDocumentElements();
        this.addEventListener();
    }

    private initDocumentElements(): void {
        this.regularNotificationToggle = document.getElementById("notification-toggle") as HTMLInputElement;
        this.twoWeeklyFrequencyRb = document.getElementById("2-weekly-rb") as HTMLInputElement;
        this.fourWeeklyFrequencyRb = document.getElementById("4-weekly-rb") as HTMLInputElement;
        this.releaseDateNotificationCb = document.getElementById("release-date-notification-cb") as HTMLInputElement;
        this.announcementDateNotificationCb = document.getElementById("announcement-date-notification-cb") as HTMLInputElement;
    }

    private addEventListener(): void {
        this.regularNotificationToggle.addEventListener("change", this.onRegularNotificationToggleValueChange.bind(this));
        this.twoWeeklyFrequencyRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.fourWeeklyFrequencyRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.releaseDateNotificationCb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.announcementDateNotificationCb.addEventListener("change", this.onAnyValueChange.bind(this));
    }

    protected getHostElementId(): string {
        return "notification-settings-container";
    }

    protected onRendering(notificationSettings: NotificationSettings): void {
        this.regularNotificationToggle.checked = notificationSettings.notify;
        this.twoWeeklyFrequencyRb.disabled = !this.regularNotificationToggle.checked;
        this.fourWeeklyFrequencyRb.disabled = !this.regularNotificationToggle.checked;
        this.twoWeeklyFrequencyRb.checked = notificationSettings.frequencyInWeeks === 2;
        this.fourWeeklyFrequencyRb.checked = notificationSettings.frequencyInWeeks === 4;
        this.releaseDateNotificationCb.checked = notificationSettings.notificationAtReleaseDate;
        this.announcementDateNotificationCb.checked = notificationSettings.notificationAtAnnouncementDate;
    }

    private onRegularNotificationToggleValueChange(): void {
        this.twoWeeklyFrequencyRb.disabled = !this.twoWeeklyFrequencyRb.disabled;
        this.fourWeeklyFrequencyRb.disabled = !this.fourWeeklyFrequencyRb.disabled;
        this.persistCurrentSettings();
    }

    private onAnyValueChange(): void {
        this.persistCurrentSettings();
    }

    private persistCurrentSettings(): void {
        this.notificationSettingsRestClient.updateNotificationSettings({
            notify: this.regularNotificationToggle.checked,
            frequencyInWeeks: this.twoWeeklyFrequencyRb.checked ? 2 : 4,
            notificationAtReleaseDate: this.releaseDateNotificationCb.checked,
            notificationAtAnnouncementDate: this.announcementDateNotificationCb.checked
        }).then(response => {
            // ToDo DanielW: Handle error
            console.log(response);
        });
    }
}
