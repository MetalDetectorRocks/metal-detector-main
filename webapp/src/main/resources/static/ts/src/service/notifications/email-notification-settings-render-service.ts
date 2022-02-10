import { AlertService } from "../util/alert-service";
import { LoadingIndicatorService } from "../util/loading-indicator-service";
import {
    DefaultNotificationConfig,
    NotificationChannel,
    NotificationSettings,
} from "../../model/notification-settings.model";
import { NotificationSettingsRestClient } from "../../clients/notification-settings-rest-client";
import { AbstractNotificationSettingsRenderService } from "./abstract-notification-settings-render-service";

export class EmailNotificationSettingsRenderService extends AbstractNotificationSettingsRenderService<DefaultNotificationConfig> {
    constructor(
        notificationSettingsRestClient: NotificationSettingsRestClient,
        alertService: AlertService,
        loadingIndicatorService: LoadingIndicatorService,
    ) {
        super(notificationSettingsRestClient, alertService, loadingIndicatorService);
    }

    protected getChannel(): NotificationChannel {
        return NotificationChannel.EMAIL;
    }

    protected getElementIdPrefix(): string {
        return "email";
    }

    protected getNotificationConfig(notificationSettings: NotificationSettings): DefaultNotificationConfig {
        return notificationSettings.emailConfig;
    }
}
