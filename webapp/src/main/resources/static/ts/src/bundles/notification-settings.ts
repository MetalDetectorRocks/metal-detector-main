import { AlertService } from "../service/alert-service";
import { LoadingIndicatorService } from "../service/loading-indicator-service";
import { NotificationSettingsRenderService } from "../service/notification-settings-render-service";
import { NotificationSettingsRestClient } from "../clients/notification-settings-rest-client";

const alertService = new AlertService();
const loadingIndicatorService = new LoadingIndicatorService();
const notificationSettingsRestClient = new NotificationSettingsRestClient();
const notificationRenderService = new NotificationSettingsRenderService(
    notificationSettingsRestClient,
    alertService,
    loadingIndicatorService,
);

const notificationSettings = notificationSettingsRestClient.fetchNotificationSettings();
notificationRenderService.render(notificationSettings);
