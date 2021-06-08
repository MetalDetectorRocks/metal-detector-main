import { AlertService } from "../alert-service";
import { LoadingIndicatorService } from "../loading-indicator-service";
import {
    NotificationChannel,
    NotificationSettings,
    TelegramNotificationConfig,
} from "../../model/notification-settings.model";
import { NotificationSettingsRestClient } from "../../clients/notification-settings-rest-client";
import { UNKNOWN_ERROR_MESSAGE } from "../../config/messages.config";
import { AbstractNotificationSettingsRenderService } from "./abstract-notification-settings-render-service";

export class TelegramNotificationSettingsRenderService extends AbstractNotificationSettingsRenderService<TelegramNotificationConfig> {
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
        super(notificationSettingsRestClient, alertService, loadingIndicatorService);
    }

    protected getChannel(): NotificationChannel {
        return NotificationChannel.TELEGRAM;
    }

    protected getElementIdPrefix(): string {
        return "telegram";
    }

    protected getNotificationConfig(notificationSettings: NotificationSettings): TelegramNotificationConfig {
        return notificationSettings.telegramConfig;
    }

    initDocumentElements(): void {
        super.initDocumentElements();
        this.telegramArea = document.getElementById("telegram-area") as HTMLDivElement;
        this.telegramActivationArea = document.getElementById("telegram-activation-area") as HTMLDivElement;
        this.generateRegistrationIdButton = document.getElementById("telegram-id-button") as HTMLButtonElement;
        this.telegramDeactivationArea = document.getElementById("telegram-deactivation-area") as HTMLDivElement;
        this.deactivateTelegramNotificationButton = document.getElementById(
            "deactivate-telegram-button",
        ) as HTMLButtonElement;
    }

    addEventListener(): void {
        super.addEventListener();
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
        super.onRendering(notificationSettings);
        if (notificationSettings.telegramConfig.notificationsActivated) {
            this.telegramArea.removeChild(this.telegramActivationArea);
        } else {
            this.telegramArea.removeChild(this.telegramDeactivationArea);
            if (notificationSettings.telegramConfig.registrationId !== null) {
                this.generateRegistrationIdButton.textContent = String(
                    notificationSettings.telegramConfig.registrationId,
                );
            }
        }
    }

    private generateRegistrationId(): void {
        this.notificationSettingsRestClient
            .generateRegistrationId()
            .then((response) => {
                this.generateRegistrationIdButton.textContent = String(response);
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
