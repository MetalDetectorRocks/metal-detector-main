import { AbstractRenderService } from "./abstract-render-service";
import { AlertService } from "./alert-service";
import { LoadingIndicatorService } from "./loading-indicator-service";
import { NotificationSettings } from "../model/notification-settings.model";
import { NotificationSettingsRestClient } from "../clients/notification-settings-rest-client";
import { UNKNOWN_ERROR_MESSAGE } from "../config/messages.config";

export class NotificationSettingsRenderService extends AbstractRenderService<NotificationSettings> {
    private readonly notificationSettingsRestClient: NotificationSettingsRestClient;

    private regularNotificationToggle!: HTMLInputElement;
    private twoWeeklyFrequencyRb!: HTMLInputElement;
    private fourWeeklyFrequencyRb!: HTMLInputElement;
    private releaseDateNotificationToggle!: HTMLInputElement;
    private announcementDateNotificationToggle!: HTMLInputElement;
    private generateRegistrationIdButton!: HTMLButtonElement;
    private deactivateTelegramNotificationButton!: HTMLButtonElement;
    private telegramArea!: HTMLDivElement;
    private telegramActivationArea!: HTMLDivElement;
    private telegramDeactivationArea!: HTMLDivElement;

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
        this.regularNotificationToggle = document.getElementById("notification-toggle") as HTMLInputElement;
        this.twoWeeklyFrequencyRb = document.getElementById("2-weekly-rb") as HTMLInputElement;
        this.fourWeeklyFrequencyRb = document.getElementById("4-weekly-rb") as HTMLInputElement;
        this.releaseDateNotificationToggle = document.getElementById(
            "release-date-notification-toggle",
        ) as HTMLInputElement;
        this.announcementDateNotificationToggle = document.getElementById(
            "announcement-date-notification-toggle",
        ) as HTMLInputElement;
        this.generateRegistrationIdButton = document.getElementById("telegram-id-button") as HTMLButtonElement;
        this.deactivateTelegramNotificationButton = document.getElementById(
            "deactivate-telegram-button",
        ) as HTMLButtonElement;
        this.telegramArea = document.getElementById("telegram-area") as HTMLDivElement;
        this.telegramActivationArea = document.getElementById("telegram-activation-area") as HTMLDivElement;
        this.telegramDeactivationArea = document.getElementById("telegram-deactivation-area") as HTMLDivElement;
    }

    private addEventListener(): void {
        this.regularNotificationToggle.addEventListener(
            "change",
            this.onRegularNotificationToggleValueChange.bind(this),
        );
        this.twoWeeklyFrequencyRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.fourWeeklyFrequencyRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.releaseDateNotificationToggle.addEventListener("change", this.onAnyValueChange.bind(this));
        this.announcementDateNotificationToggle.addEventListener("change", this.onAnyValueChange.bind(this));
        this.generateRegistrationIdButton.addEventListener("click", this.generateRegistrationId.bind(this));
        this.deactivateTelegramNotificationButton.addEventListener(
            "click",
            this.deactivateTelegramNotifications.bind(this),
        );
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
        this.releaseDateNotificationToggle.checked = notificationSettings.notificationAtReleaseDate;
        this.announcementDateNotificationToggle.checked = notificationSettings.notificationAtAnnouncementDate;

        if (notificationSettings.telegramNotificationsActive) {
            this.telegramArea.removeChild(this.telegramActivationArea);
        } else {
            this.telegramArea.removeChild(this.telegramDeactivationArea);
            if (notificationSettings.registrationId !== null) {
                this.generateRegistrationIdButton.textContent = notificationSettings.registrationId + "";
            }
        }
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
        this.notificationSettingsRestClient
            .updateNotificationSettings({
                notify: this.regularNotificationToggle.checked,
                frequencyInWeeks: this.twoWeeklyFrequencyRb.checked ? 2 : 4,
                notificationAtReleaseDate: this.releaseDateNotificationToggle.checked,
                notificationAtAnnouncementDate: this.announcementDateNotificationToggle.checked,
                telegramNotificationsActive: false,
                registrationId: 0,
            })
            .catch(() => {
                const message = `<h3 class="h5">${UNKNOWN_ERROR_MESSAGE}</h3>Your changes may not have been saved. Please try again later.`;
                const infoMessage = this.alertService.renderErrorAlert(message, false);
                this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
            });
    }

    private generateRegistrationId(): void {
        this.notificationSettingsRestClient
            .generateRegistrationId()
            .then((response) => {
                this.generateRegistrationIdButton.textContent = response + "";
            })
            .catch(() => {
                const message = `<h3 class="h5">${UNKNOWN_ERROR_MESSAGE}</h3>Id could not be generated. Please try again later.`;
                const infoMessage = this.alertService.renderErrorAlert(message, false);
                this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
            });
    }

    private deactivateTelegramNotifications(): void {
        this.notificationSettingsRestClient
            .deactivateTelegramNotifications()
            .then(() => {
                const telegramArea = document.getElementById("telegram-area") as HTMLDivElement;
                telegramArea.replaceChild(this.telegramActivationArea, this.telegramDeactivationArea);
            })
            .catch(() => {
                const message = `<h3 class="h5">${UNKNOWN_ERROR_MESSAGE}</h3>Telegram notifications could not be deactivated. Please try again later.`;
                const infoMessage = this.alertService.renderErrorAlert(message, false);
                this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
            });
    }
}
