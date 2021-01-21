import {ToastService} from "../service/toast-service";
import {UserResponse} from "../model/user-response.model";
import axios, {AxiosError, AxiosResponse} from "axios";
import {axiosConfig} from "../config/axios.config";
import {UNKNOWN_ERROR_MESSAGE} from "../config/messages.config";

export class AccountDetailsRestClient {

    private readonly CURRENT_USER_ENDPOINT = "/rest/v1/me";
    private readonly CURRENT_USER_EMAIL_ENDPOINT = "/rest/v1/me/email"

    private readonly toastService: ToastService;

    constructor(toastService: ToastService) {
        this.toastService = toastService;
    }

    public async getAccountDetails(): Promise<UserResponse> {
        return await axios.get(
          this.CURRENT_USER_ENDPOINT, axiosConfig
        ).then((response: AxiosResponse<UserResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
            throw error;
        })
    }

    public async updateEmailAddress(emailAddress: string): Promise<string> {
        axiosConfig.data = {
            emailAddress: emailAddress
        }
        return await axios.patch(
          this.CURRENT_USER_EMAIL_ENDPOINT, axiosConfig
        ).then((response: AxiosResponse<string>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
            throw error;
        })
    }
}