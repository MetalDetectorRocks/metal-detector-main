import axios, {AxiosError, AxiosResponse} from "axios";
import {axiosConfig} from "../config/axios.config";
import {ToastService} from "../service/toast-service";
import {UNKNOWN_ERROR_MESSAGE} from "../config/messages.config";
import {SpotifyUserAuthorizationResponse} from "../model/spotify-user-authorization-response.model";

export class SpotifyAuthorizationRestClient {

    private readonly SPOTIFY_AUTHORIZATION_ENDPOINT = "/rest/v1/auth/spotify";

    private readonly toastService: ToastService;

    constructor(toastService: ToastService) {
        this.toastService = toastService;
    }

    public async getAuthorizationUrl(): Promise<SpotifyUserAuthorizationResponse> {
        return await axios.get(
          this.SPOTIFY_AUTHORIZATION_ENDPOINT, axiosConfig
        ).then((response: AxiosResponse<SpotifyUserAuthorizationResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
            throw error;
        });
    }
}
