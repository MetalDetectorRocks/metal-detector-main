import axios, { AxiosError, AxiosResponse } from "axios";
import { axiosConfig } from "../config/axios.config";
import { MyArtistsResponse } from "../model/my-artists-response.model";
import { SearchResponse } from "../model/search-response.model";
import { AbstractRestClient } from "./abstract-rest-client";
import { ToastService } from "../service/toast-service";
import { UNKNOWN_ERROR_MESSAGE } from "../config/messages.config";

export class ArtistsRestClient extends AbstractRestClient {

    private readonly SEARCH_URL = "/rest/v1/artists/search";
    private readonly MY_ARTISTS_URL = "/rest/v1/my-artists";
    private readonly FOLLOW_ARTISTS_URL = "/rest/v1/artists/follow";
    private readonly UNFOLLOW_ARTISTS_URL = "/rest/v1/artists/unfollow";

    private readonly toastService: ToastService;

    constructor(toastService: ToastService) {
        super();
        this.toastService = toastService;
    }

    public async searchArtist(): Promise<SearchResponse> {
        axiosConfig.params = {
            query: this.getParameterFromUrl("query"),
            page: this.getPageFromUrl(),
            size: 40
        }

        return await axios.get(
            this.SEARCH_URL, axiosConfig
        ).then((response: AxiosResponse<SearchResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            console.error(error);
            throw error;
        });
    }

    public async fetchMyArtists(): Promise<MyArtistsResponse> {
        axiosConfig.params = {
            page: this.getPageFromUrl()
        }

        return await axios.get(
            this.MY_ARTISTS_URL, axiosConfig
        ).then((response: AxiosResponse<MyArtistsResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            console.error(error);
            throw error;
        });
    }

    public async followArtist(artistId: string, source: string): Promise<any> {
        return await axios.post(
            `${this.FOLLOW_ARTISTS_URL}/${source}/${artistId}`,
            {},
            axiosConfig
        ).catch((error: AxiosError) => {
            this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
            throw error;
        });
    }

    public async unfollowArtist(artistId: string, source: string): Promise<any> {
        return await axios.post(
            `${this.UNFOLLOW_ARTISTS_URL}/${source}/${artistId}`,
            {},
            axiosConfig
        ).catch((error: AxiosError) => {
            this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
            throw error;
        });
    }
}
