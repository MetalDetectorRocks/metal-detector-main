export interface NotificationSettings {
    readonly emailConfig: DefaultNotificationConfig;
    readonly telegramConfig: TelegramNotificationConfig;
}
export interface DefaultNotificationConfig {
    readonly notify: boolean;
    readonly frequencyInWeeks: number;
    readonly notificationAtReleaseDate: boolean;
    readonly notificationAtAnnouncementDate: boolean;
    readonly notifyReissues: boolean;
}
export interface TelegramNotificationConfig extends DefaultNotificationConfig {
    readonly notificationsActivated: boolean;
    readonly registrationId: number;
}
export interface UpdateNotificationSettingsRequest extends DefaultNotificationConfig {
    readonly channel: NotificationChannel;
}
export enum NotificationChannel {
    EMAIL = "EMAIL",
    TELEGRAM = "TELEGRAM",
}
