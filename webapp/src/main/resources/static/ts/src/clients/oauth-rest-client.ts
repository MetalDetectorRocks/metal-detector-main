import axios, { AxiosError, AxiosResponse } from "axios";
import { axiosConfig } from "../config/axios.config";
import { ToastService } from "../service/toast-service";
import { UNKNOWN_ERROR_MESSAGE } from "../config/messages.config";
import { UserAuthorizationExistsResponse } from "../model/user-authorization-exist-response.model";

export class OauthRestClient {
    public static readonly OAUTH2_AUTHORIZATION_CODE_FLOW_ENDPOINT = "/oauth2/authorization";
    private static readonly AUTHORIZATION_EXISTS_ENDPOINT = "/rest/v1/oauth";

    private readonly toastService: ToastService;

    constructor(toastService: ToastService) {
        this.toastService = toastService;
    }

    public async existsAuthorization(registrationId: string): Promise<UserAuthorizationExistsResponse> {
        return await axios
            .get(OauthRestClient.AUTHORIZATION_EXISTS_ENDPOINT + "/" + registrationId, axiosConfig)
            .then((response: AxiosResponse<UserAuthorizationExistsResponse>) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
                throw error;
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
