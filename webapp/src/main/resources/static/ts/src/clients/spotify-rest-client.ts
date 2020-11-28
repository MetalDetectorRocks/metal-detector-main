import axios, {AxiosError, AxiosResponse} from "axios";
import {axiosConfig} from "../config/axios.config";
import {ToastService} from "../service/toast-service";
import {UNKNOWN_ERROR_MESSAGE} from "../config/messages.config";
import {SpotifyUserAuthorizationResponse} from "../model/spotify-user-authorization-response.model";
import {SpotifyFollowedArtistsResponse} from "../model/spotify-followed-artist-response.model";
import {SpotifyUserAuthorizationExistsResponse} from "../model/spotify-user-authorization-exist-response.model";
import {SpotifySynchronizeArtistsResponse} from "../model/spotify-synchronize-artists-response.model";

export class SpotifyRestClient {

    private readonly SPOTIFY_AUTHORIZATION_ENDPOINT = "/rest/v1/spotify/auth";
    private readonly SPOTIFY_SYNCHRONIZE_ARTISTS_ENDPOINT = "/rest/v1/spotify/synchronize";
    private readonly SPOTIFY_FOLLOWED_ARTISTS_ENDPOINT = "/rest/v1/spotify/followed-artists";

    private readonly toastService: ToastService;

    constructor(toastService: ToastService) {
        this.toastService = toastService;
    }

    public async existsAuthorization(): Promise<SpotifyUserAuthorizationExistsResponse> {
        return await axios.get(
            this.SPOTIFY_AUTHORIZATION_ENDPOINT, axiosConfig
        ).then((response: AxiosResponse<SpotifyUserAuthorizationExistsResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
            throw error;
        })
    }

    public async createAuthorizationUrl(): Promise<SpotifyUserAuthorizationResponse> {
        return await axios.post(
          this.SPOTIFY_AUTHORIZATION_ENDPOINT, axiosConfig
        ).then((response: AxiosResponse<SpotifyUserAuthorizationResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
            throw error;
        });
    }

    public async fetchInitialToken(state: string, code: string): Promise<void> {
        axiosConfig.data = {
            code: code,
            state: state
        };
        return await axios.put(
            this.SPOTIFY_AUTHORIZATION_ENDPOINT, axiosConfig
        ).then(() => {
            return;
        }).catch((error: AxiosError) => {
            this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
            throw error;
        });
    }

    public async fetchFollowedArtists(): Promise<SpotifyFollowedArtistsResponse> {
        axiosConfig.params = {
            fetchTypes: "ALBUMS"
        }
        return await axios.get(
          this.SPOTIFY_FOLLOWED_ARTISTS_ENDPOINT, axiosConfig
        ).then((response: AxiosResponse<SpotifyFollowedArtistsResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
            throw error;
        });
    }

    public async synchronizeArtists(artistIds: string[]): Promise<SpotifySynchronizeArtistsResponse> {
        axiosConfig.data = {
            artistIds: artistIds
        };
        return await axios.post(
            this.SPOTIFY_SYNCHRONIZE_ARTISTS_ENDPOINT, axiosConfig
        ).then((response: AxiosResponse<SpotifySynchronizeArtistsResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
            throw error;
        });
    }
}
