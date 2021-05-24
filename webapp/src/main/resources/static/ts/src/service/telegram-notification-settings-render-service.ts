// import { AbstractRenderService } from "./abstract-render-service";
// import { AlertService } from "./alert-service";
// import { LoadingIndicatorService } from "./loading-indicator-service";
// import { NotificationSettings } from "../model/notification-settings.model";
// import { NotificationSettingsRestClient } from "../clients/notification-settings-rest-client";
// import { UNKNOWN_ERROR_MESSAGE } from "../config/messages.config";
//
// export class EmailNotificationSettingsRenderService extends AbstractRenderService<NotificationSettings> {
//     private readonly notificationSettingsRestClient: NotificationSettingsRestClient;
//     private nonePeriodicNotificationsRb!: HTMLInputElement;
//     private twoWeeklyFrequencyRb!: HTMLInputElement;
//     private fourWeeklyFrequencyRb!: HTMLInputElement;
//     private emailReleaseDateNotificationToggle!: HTMLInputElement;
//     private emailAnnouncementDateNotificationToggle!: HTMLInputElement;
//     private generateRegistrationIdButton!: HTMLButtonElement;
//     private deactivateTelegramNotificationButton!: HTMLButtonElement;
//     private telegramArea!: HTMLDivElement;
//     private telegramActivationArea!: HTMLDivElement;
//     private telegramDeactivationArea!: HTMLDivElement;
//
//     constructor(
//         notificationSettingsRestClient: NotificationSettingsRestClient,
//         alertService: AlertService,
//         loadingIndicatorService: LoadingIndicatorService,
//     ) {
//         super(alertService, loadingIndicatorService);
//         this.notificationSettingsRestClient = notificationSettingsRestClient;
//         this.initDocumentElements();
//         this.addEventListener();
//     }
//
//     private initDocumentElements(): void {
//         this.nonePeriodicNotificationsRb = document.getElementById("none-rb") as HTMLInputElement;
//         this.twoWeeklyFrequencyRb = document.getElementById("2-weekly-rb") as HTMLInputElement;
//         this.fourWeeklyFrequencyRb = document.getElementById("4-weekly-rb") as HTMLInputElement;
//         this.emailReleaseDateNotificationToggle = document.getElementById(
//             "email-release-date-notification-toggle",
//         ) as HTMLInputElement;
//         this.emailAnnouncementDateNotificationToggle = document.getElementById(
//             "email-announcement-date-notification-toggle",
//         ) as HTMLInputElement;
//         this.generateRegistrationIdButton = document.getElementById("telegram-id-button") as HTMLButtonElement;
//         this.deactivateTelegramNotificationButton = document.getElementById(
//             "deactivate-telegram-button",
//         ) as HTMLButtonElement;
//         this.telegramArea = document.getElementById("telegram-area") as HTMLDivElement;
//         this.telegramActivationArea = document.getElementById("telegram-activation-area") as HTMLDivElement;
//         this.telegramDeactivationArea = document.getElementById("telegram-deactivation-area") as HTMLDivElement;
//     }
//
//     private addEventListener(): void {
//         this.nonePeriodicNotificationsRb.addEventListener("change", this.onAnyValueChange.bind(this));
//         this.twoWeeklyFrequencyRb.addEventListener("change", this.onAnyValueChange.bind(this));
//         this.fourWeeklyFrequencyRb.addEventListener("change", this.onAnyValueChange.bind(this));
//         this.emailReleaseDateNotificationToggle.addEventListener("change", this.onAnyValueChange.bind(this));
//         this.emailAnnouncementDateNotificationToggle.addEventListener("change", this.onAnyValueChange.bind(this));
//         this.generateRegistrationIdButton.addEventListener("click", this.generateRegistrationId.bind(this));
//         this.deactivateTelegramNotificationButton.addEventListener(
//             "click",
//             this.deactivateTelegramNotifications.bind(this),
//         );
//     }
//
//     protected getHostElementId(): string {
//         return "notification-settings-container";
//     }
//
//     protected onRendering(notificationSettings: NotificationSettings): void {
//         this.nonePeriodicNotificationsRb.checked = notificationSettings.notify;
//         this.twoWeeklyFrequencyRb.checked = notificationSettings.frequencyInWeeks === 2;
//         this.fourWeeklyFrequencyRb.checked = notificationSettings.frequencyInWeeks === 4;
//         this.emailReleaseDateNotificationToggle.checked = notificationSettings.notificationAtReleaseDate;
//         this.emailAnnouncementDateNotificationToggle.checked = notificationSettings.notificationAtAnnouncementDate;
//         if (notificationSettings.telegramNotificationsActive) {
//             this.telegramArea.removeChild(this.telegramActivationArea);
//         } else {
//             this.telegramArea.removeChild(this.telegramDeactivationArea);
//             if (notificationSettings.registrationId !== null) {
//                 this.generateRegistrationIdButton.textContent = notificationSettings.registrationId + "";
//             }
//         }
//     }
//
//     private onAnyValueChange(): void {
//         this.persistCurrentSettings();
//     }
//
//     private persistCurrentSettings(): void {
//         // ToDo DanielW: Uncomment
//         // this.notificationSettingsRestClient
//         //     .updateNotificationSettings({
//         //         notify: this.emailNotificationToggle.checked,
//         //         frequencyInWeeks: parseInt(this.emailFrequency.value),
//         //         notificationAtReleaseDate: this.emailReleaseDateNotificationToggle.checked,
//         //         notificationAtAnnouncementDate: this.emailAnnouncementDateNotificationToggle.checked,
//         //         telegramNotificationsActive: false,
//         //         registrationId: 0,
//         //     })
//         //     .catch(() => {
//         //         const message = `<h3 class="h5">${UNKNOWN_ERROR_MESSAGE}</h3>Your changes may not have been saved. Please try again later.`;
//         //         const infoMessage = this.alertService.renderErrorAlert(message, false);
//         //         this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
//         //     });
//     }
//
//     private generateRegistrationId(): void {
//         this.notificationSettingsRestClient
//             .generateRegistrationId()
//             .then((response) => {
//                 this.generateRegistrationIdButton.textContent = response + "";
//             })
//             .catch(() => {
//                 const message = `<h3 class="h5">${UNKNOWN_ERROR_MESSAGE}</h3>Id could not be generated. Please try again later.`;
//                 const infoMessage = this.alertService.renderErrorAlert(message, false);
//                 this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
//             });
//     }
//
//     private deactivateTelegramNotifications(): void {
//         this.notificationSettingsRestClient
//             .deactivateTelegramNotifications()
//             .then(() => {
//                 const telegramArea = document.getElementById("telegram-area") as HTMLDivElement;
//                 telegramArea.replaceChild(this.telegramActivationArea, this.telegramDeactivationArea);
//             })
//             .catch(() => {
//                 const message = `<h3 class="h5">${UNKNOWN_ERROR_MESSAGE}</h3>Telegram notifications could not be deactivated. Please try again later.`;
//                 const infoMessage = this.alertService.renderErrorAlert(message, false);
//                 this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
//             });
//     }
// }
