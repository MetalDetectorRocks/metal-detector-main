import axios, {AxiosError, AxiosResponse} from "axios";
import {axiosConfig} from "../config/axios.config";
import {AbstractRestClient} from "./abstract-rest-client";
import {ToastService} from "../service/toast-service";
import {UNKNOWN_ERROR_MESSAGE} from "../config/messages.config";
import {SpotifyUserAuthorizationResponse} from "../model/spotify-user-authorization-response.model";

export class SpotifyAuthorizationRestClient extends AbstractRestClient {

    private readonly SPOTIFY_AUTHORIZATION_ENDPOINT = "/rest/v1/auth/spotify";

    private readonly toastService: ToastService;

    constructor(toastService: ToastService) {
        super();
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
