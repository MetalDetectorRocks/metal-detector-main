import axios, { AxiosError, AxiosResponse } from "axios";
import { axiosConfig } from "../config/axios.config";
import { MyArtistsResponse } from "../model/my-artists-response.model";
import { SearchResponse } from "../model/search-response.model";
import { ToastService } from "../service/util/toast-service";
import { UNKNOWN_ERROR_MESSAGE } from "../config/messages.config";
import { UrlService } from "../service/util/url-service";
import { Artist } from "../model/artist.model";

export class ArtistsRestClient {
    private readonly SEARCH_URL = "/rest/v1/artists/search";
    private readonly MY_ARTISTS_URL = "/rest/v1/my-artists";
    private readonly FOLLOW_ARTISTS_URL = "/rest/v1/artists/follow";
    private readonly UNFOLLOW_ARTISTS_URL = "/rest/v1/artists/unfollow";
    private readonly TOP_ARTISTS_URL = "/rest/v1/artists/top";

    private readonly urlService: UrlService;
    private readonly toastService: ToastService;

    constructor(urlService: UrlService, toastService: ToastService) {
        this.urlService = urlService;
        this.toastService = toastService;
    }

    public async searchArtist(): Promise<SearchResponse> {
        axiosConfig.params = {
            query: this.urlService.getParameterFromUrl("query"),
            page: this.urlService.getPageFromUrl(),
            size: 10,
        };

        return await axios
            .get(this.SEARCH_URL, axiosConfig)
            .then((response: AxiosResponse<SearchResponse>) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }

    public async fetchMyArtists(): Promise<MyArtistsResponse> {
        axiosConfig.params = {
            page: this.urlService.getPageFromUrl(),
        };

        return await axios
            .get(this.MY_ARTISTS_URL, axiosConfig)
            .then((response: AxiosResponse<MyArtistsResponse>) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }

    public async followArtist(artistId: string, source: string): Promise<unknown> {
        return await axios
            .post(`${this.FOLLOW_ARTISTS_URL}/${source}/${artistId}`, {}, axiosConfig)
            .catch((error: AxiosError) => {
                this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
                throw error;
            });
    }

    public async unfollowArtist(artistId: string, source: string): Promise<unknown> {
        return await axios
            .post(`${this.UNFOLLOW_ARTISTS_URL}/${source}/${artistId}`, {}, axiosConfig)
            .catch((error: AxiosError) => {
                this.toastService.createErrorToast(UNKNOWN_ERROR_MESSAGE);
                throw error;
            });
    }

    public async fetchTopArtists(): Promise<Artist[]> {
        return axios
            .get(this.TOP_ARTISTS_URL)
            .then((response: AxiosResponse<Artist[]>) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }
}
