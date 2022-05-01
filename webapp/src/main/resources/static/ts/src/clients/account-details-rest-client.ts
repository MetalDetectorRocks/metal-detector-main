import { UserResponse } from "../model/user-response.model";
import axios, { AxiosError, AxiosResponse } from "axios";
import { axiosConfig } from "../config/axios.config";

export class AccountDetailsRestClient {
    private readonly CURRENT_USER_ENDPOINT = "/rest/v1/me";
    private readonly CURRENT_USER_EMAIL_ENDPOINT = "/rest/v1/me/email";
    private readonly CURRENT_USER_PASSWORD_ENDPOINT = "/rest/v1/me/password";

    public async getAccountDetails(): Promise<UserResponse> {
        return await axios
            .get(this.CURRENT_USER_ENDPOINT, axiosConfig)
            .then((response: AxiosResponse<UserResponse>) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error.message);
                throw error;
            });
    }

    public async updateEmailAddress(emailAddress: string): Promise<string> {
        axiosConfig.data = {
            emailAddress: emailAddress,
        };
        return await axios
            .patch(this.CURRENT_USER_EMAIL_ENDPOINT, axiosConfig)
            .then((response: AxiosResponse<string>) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error.message);
                throw error;
            });
    }

    public async deleteAccount(): Promise<void> {
        return await axios
            .delete(this.CURRENT_USER_ENDPOINT, axiosConfig)
            .then(() => {
                return;
            })
            .catch((error: AxiosError) => {
                console.error(error.message);
                throw error;
            });
    }

    public async updatePassword(oldPassword: string, newPassword: string, confirmPassword: string): Promise<void> {
        axiosConfig.data = {
            oldPlainPassword: oldPassword,
            newPlainPassword: newPassword,
            verifyNewPlainPassword: confirmPassword,
        };
        return await axios
            .patch(this.CURRENT_USER_PASSWORD_ENDPOINT, axiosConfig)
            .then(() => {
                return;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }
}
