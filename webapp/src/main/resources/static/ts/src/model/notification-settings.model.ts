export interface NotificationSettings {
    readonly notify: boolean;
    readonly frequencyInWeeks: number;
    readonly notificationAtReleaseDate: boolean;
    readonly notificationAtAnnouncementDate: boolean;
    readonly telegramNotificationsActive: boolean;
    readonly registrationId: number;
}
