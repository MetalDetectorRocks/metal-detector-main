import { AlertService } from "../service/alert-service";
import { LoadingIndicatorService } from "../service/loading-indicator-service";
import { EmailNotificationSettingsRenderService } from "../service/notifications/email-notification-settings-render-service";
import { NotificationSettingsRestClient } from "../clients/notification-settings-rest-client";
import { TelegramNotificationSettingsRenderService } from "../service/notifications/telegram-notification-settings-render-service";

const alertService = new AlertService();
const loadingIndicatorService = new LoadingIndicatorService();
const notificationSettingsRestClient = new NotificationSettingsRestClient();
const emailNotificationSettingsRenderService = new EmailNotificationSettingsRenderService(
    notificationSettingsRestClient,
    alertService,
    loadingIndicatorService,
);
const telegramNotificationSettingsRenderService = new TelegramNotificationSettingsRenderService(
    notificationSettingsRestClient,
    alertService,
    loadingIndicatorService,
);

const notificationSettings = notificationSettingsRestClient.fetchNotificationSettings();
emailNotificationSettingsRenderService.render(notificationSettings);
telegramNotificationSettingsRenderService.render(notificationSettings);
