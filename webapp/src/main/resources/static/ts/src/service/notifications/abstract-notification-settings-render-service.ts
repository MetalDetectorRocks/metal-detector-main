import { AbstractRenderService } from "../abstract-render-service";
import { AlertService } from "../alert-service";
import { LoadingIndicatorService } from "../loading-indicator-service";
import {
    DefaultNotificationConfig,
    NotificationChannel,
    NotificationSettings,
} from "../../model/notification-settings.model";
import { NotificationSettingsRestClient } from "../../clients/notification-settings-rest-client";
import { UNKNOWN_ERROR_MESSAGE } from "../../config/messages.config";

export abstract class AbstractNotificationSettingsRenderService<
    // eslint-disable-next-line prettier/prettier
    T extends DefaultNotificationConfig,
> extends AbstractRenderService<NotificationSettings> {
    protected readonly notificationSettingsRestClient: NotificationSettingsRestClient;
    private nonePeriodicNotificationsRb!: HTMLInputElement;
    private twoWeeklyFrequencyRb!: HTMLInputElement;
    private fourWeeklyFrequencyRb!: HTMLInputElement;
    private releaseDateNotificationToggle!: HTMLInputElement;
    private announcementDateNotificationToggle!: HTMLInputElement;
    private notifyReissuesToggle!: HTMLInputElement;

    protected constructor(
        notificationSettingsRestClient: NotificationSettingsRestClient,
        alertService: AlertService,
        loadingIndicatorService: LoadingIndicatorService,
    ) {
        super(alertService, loadingIndicatorService);
        this.notificationSettingsRestClient = notificationSettingsRestClient;
        this.initDocumentElements();
        this.addEventListener();
    }

    protected abstract getChannel(): NotificationChannel;

    protected abstract getElementIdPrefix(): string;

    protected abstract getNotificationConfig(notificationSettings: NotificationSettings): T;

    protected getHostElementId(): string {
        return "notification-settings-container";
    }

    protected initDocumentElements(): void {
        const getHtmlInputElement = (id: string) =>
            document.getElementById(`${this.getElementIdPrefix()}-${id}`) as HTMLInputElement;
        this.nonePeriodicNotificationsRb = getHtmlInputElement("none-rb");
        this.twoWeeklyFrequencyRb = getHtmlInputElement("2-weekly-rb");
        this.fourWeeklyFrequencyRb = getHtmlInputElement("4-weekly-rb");
        this.releaseDateNotificationToggle = getHtmlInputElement("release-date-notification-toggle");
        this.announcementDateNotificationToggle = getHtmlInputElement("announcement-date-notification-toggle");
        this.notifyReissuesToggle = getHtmlInputElement("reissue-notification-toggle");
    }

    protected addEventListener(): void {
        this.nonePeriodicNotificationsRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.twoWeeklyFrequencyRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.fourWeeklyFrequencyRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.releaseDateNotificationToggle.addEventListener("change", this.onAnyValueChange.bind(this));
        this.announcementDateNotificationToggle.addEventListener("change", this.onAnyValueChange.bind(this));
        this.notifyReissuesToggle.addEventListener("change", this.onAnyValueChange.bind(this));
    }

    protected onRendering(notificationSettings: NotificationSettings): void {
        const config = this.getNotificationConfig(notificationSettings);
        this.nonePeriodicNotificationsRb.checked = !config.notify;
        if (config.notify) {
            this.twoWeeklyFrequencyRb.checked = config.frequencyInWeeks === 2;
            this.fourWeeklyFrequencyRb.checked = config.frequencyInWeeks === 4;
        }
        this.releaseDateNotificationToggle.checked = config.notificationAtReleaseDate;
        this.announcementDateNotificationToggle.checked = config.notificationAtAnnouncementDate;
        this.notifyReissuesToggle.checked = config.notifyReissues;
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
                notifyReissues: this.notifyReissuesToggle.checked,
                channel: this.getChannel(),
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
