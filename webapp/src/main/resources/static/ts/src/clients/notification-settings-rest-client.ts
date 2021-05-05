import axios, { AxiosError, AxiosResponse } from "axios";
import { axiosConfig } from "../config/axios.config";
import { NotificationSettings } from "../model/notification-settings.model";

export class NotificationSettingsRestClient {
    private readonly NOTIFICATION_SETTINGS_URL = "/rest/v1/notification-config";

    public async fetchNotificationSettings(): Promise<NotificationSettings> {
        return await axios
            .get(this.NOTIFICATION_SETTINGS_URL, axiosConfig)
            .then((response: AxiosResponse<NotificationSettings>) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }

    public async updateNotificationSettings(request: NotificationSettings): Promise<boolean> {
        return await axios
            .put(this.NOTIFICATION_SETTINGS_URL, request, axiosConfig)
            .then((response: AxiosResponse) => {
                return response.status === 200;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }

    public async generateRegistrationId(): Promise<number> {
        return await axios
            .post(this.NOTIFICATION_SETTINGS_URL, axiosConfig)
            .then((response: AxiosResponse) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }

    public async deactivateTelegramNotifications(): Promise<void> {
        return await axios
            .delete(this.NOTIFICATION_SETTINGS_URL, axiosConfig)
            .then((response: AxiosResponse) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }
}
