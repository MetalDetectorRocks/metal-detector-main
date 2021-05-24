import { AbstractRenderService } from "./abstract-render-service";
import { AlertService } from "./alert-service";
import { LoadingIndicatorService } from "./loading-indicator-service";
import { NotificationChannel, NotificationSettings } from "../model/notification-settings.model";
import { NotificationSettingsRestClient } from "../clients/notification-settings-rest-client";
import { UNKNOWN_ERROR_MESSAGE } from "../config/messages.config";

export class EmailNotificationSettingsRenderService extends AbstractRenderService<NotificationSettings> {
    private readonly notificationSettingsRestClient: NotificationSettingsRestClient;
    private nonePeriodicNotificationsRb!: HTMLInputElement;
    private twoWeeklyFrequencyRb!: HTMLInputElement;
    private fourWeeklyFrequencyRb!: HTMLInputElement;
    private releaseDateNotificationToggle!: HTMLInputElement;
    private announcementDateNotificationToggle!: HTMLInputElement;

    constructor(
        notificationSettingsRestClient: NotificationSettingsRestClient,
        alertService: AlertService,
        loadingIndicatorService: LoadingIndicatorService,
    ) {
        super(alertService, loadingIndicatorService);
        this.notificationSettingsRestClient = notificationSettingsRestClient;
        this.initDocumentElements();
        this.addEventListener();
    }

    private initDocumentElements(): void {
        this.nonePeriodicNotificationsRb = document.getElementById("email-none-rb") as HTMLInputElement;
        this.twoWeeklyFrequencyRb = document.getElementById("email-2-weekly-rb") as HTMLInputElement;
        this.fourWeeklyFrequencyRb = document.getElementById("email-4-weekly-rb") as HTMLInputElement;
        this.releaseDateNotificationToggle = document.getElementById(
            "email-release-date-notification-toggle",
        ) as HTMLInputElement;
        this.announcementDateNotificationToggle = document.getElementById(
            "email-announcement-date-notification-toggle",
        ) as HTMLInputElement;
    }

    private addEventListener(): void {
        this.nonePeriodicNotificationsRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.twoWeeklyFrequencyRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.fourWeeklyFrequencyRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.releaseDateNotificationToggle.addEventListener("change", this.onAnyValueChange.bind(this));
        this.announcementDateNotificationToggle.addEventListener("change", this.onAnyValueChange.bind(this));
    }

    protected getHostElementId(): string {
        return "notification-settings-container";
    }

    protected onRendering(notificationSettings: NotificationSettings): void {
        const emailConfig = notificationSettings.emailConfig;
        this.nonePeriodicNotificationsRb.checked = !emailConfig.notify;
        if (emailConfig.notify) {
            this.twoWeeklyFrequencyRb.checked = emailConfig.frequencyInWeeks === 2;
            this.fourWeeklyFrequencyRb.checked = emailConfig.frequencyInWeeks === 4;
        }
        this.releaseDateNotificationToggle.checked = emailConfig.notificationAtReleaseDate;
        this.announcementDateNotificationToggle.checked = emailConfig.notificationAtAnnouncementDate;
    }

    private onAnyValueChange(): void {
        this.persistCurrentSettings();
    }

    private persistCurrentSettings(): void {
        this.notificationSettingsRestClient
            .updateNotificationSettings({
                notify: !this.nonePeriodicNotificationsRb.checked,
                frequencyInWeeks: this.evaluateFrequency(),
                notificationAtReleaseDate: this.releaseDateNotificationToggle.checked,
                notificationAtAnnouncementDate: this.announcementDateNotificationToggle.checked,
                channel: NotificationChannel.EMAIL,
            })
            .catch(() => {
                const message = `<h3 class="h5">${UNKNOWN_ERROR_MESSAGE}</h3>Your changes may not have been saved. Please try again later.`;
                const infoMessage = this.alertService.renderErrorAlert(message, false);
                this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
            });
    }

    private evaluateFrequency(): number {
        if (this.nonePeriodicNotificationsRb.checked) {
            return 0;
        }
        return this.twoWeeklyFrequencyRb.checked ? 2 : 4;
    }
}
