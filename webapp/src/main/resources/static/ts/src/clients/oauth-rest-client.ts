import axios, { AxiosError, AxiosResponse } from "axios";
import { axiosConfig } from "../config/axios.config";
import { ToastService } from "../service/toast-service";
import { UNKNOWN_ERROR_MESSAGE } from "../config/messages.config";

export class OauthRestClient {
    private static readonly OAUTH2_AUTHORIZATION_CODE_FLOW_ENDPOINT = "/oauth2/authorization";
    private static readonly AUTHORIZATION_EXISTS_ENDPOINT = "/rest/v1/oauth";

    private readonly toastService: ToastService;

    constructor(toastService: ToastService) {
        this.toastService = toastService;
    }

    public authenticate(clientRegistrationId: string): void {
        window.location.href = `${OauthRestClient.OAUTH2_AUTHORIZATION_CODE_FLOW_ENDPOINT}/${clientRegistrationId}`;
    }

    public async existsAuthorization(registrationId: string): Promise<number> {
        return await axios
            .get(OauthRestClient.AUTHORIZATION_EXISTS_ENDPOINT + "/" + registrationId, axiosConfig)
            .then((response: AxiosResponse) => {
                return response.status;
            })
            .catch((error: AxiosError) => {
                if (error.response?.status == 404) {
                    return 404;
                } else {
                    this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
                    throw error;
                }
            });
    }

    public async deleteAuthorization(registrationId: string): Promise<AxiosResponse | void> {
        const uri = OauthRestClient.AUTHORIZATION_EXISTS_ENDPOINT + "/" + registrationId;
        return await axios
            .delete(uri, axiosConfig)
            .then(() => {
                return;
            })
            .catch((error: AxiosError) => {
                this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
                throw error;
            });
    }
}
